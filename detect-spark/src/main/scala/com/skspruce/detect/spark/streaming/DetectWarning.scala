package com.skspruce.detect.spark.streaming

import java.util
import java.util.Calendar

import com.skspruce.detect.spark.util.BroadcastWrapper
import com.skspruce.detect.utils.{BytesUtil, CassandraUtil, ESUtil, MacUtil}
import org.apache.commons.cli.{CommandLine, GnuParser, HelpFormatter, Option, Options}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{Partitioner, SparkConf, SparkContext}
import org.slf4j.LoggerFactory

/**
  * 布控人员预警实时处理
  */
object DetectWarning {

  val logger = LoggerFactory.getLogger(DetectWarning.getClass)
  Logger.getLogger("org").setLevel(Level.INFO)

  def main(args: Array[String]): Unit = {
    val options = getOptions()
    val parser = new GnuParser
    val cmd = parser.parse(options, args)

    if (cmd.hasOption("help")) {
      usage(options)
      System.exit(0)
    }

    if (!validateOptions(cmd, options)) {
      usage(options)
      System.exit(0)
    }

    //获取参数列表
    val appName = cmd.getOptionValue("app-name").trim
    val bootstrapServers = cmd.getOptionValue("bootstrap-servers").trim
    val groupId = cmd.getOptionValue("group-id").trim
    val interval = cmd.getOptionValue("interval").trim
    val topicList = cmd.getOptionValue("topics").trim
    val maxRatePerPartition = cmd.getOptionValue("max-rate").trim
    val updateInteval = cmd.getOptionValue("update-interval").trim
    val apTimeOut = cmd.getOptionValue("ap-timeout").trim.toInt

    //设置spark参数
    val conf = new SparkConf().setAppName(appName)
    //设置spark每秒最大从kafka但分区数据速度
    conf.set("spark.streaming.kafka.maxRatePerPartition", maxRatePerPartition)
    //以本地模式运行,仅测试用,本地测试在VM参数中加入-DisLocal=true
    val isLocal = System.getProperty("isLocal")
    if (isLocal.equals("true")) {
      conf.setMaster("local[4]")
    }
    //StreamingContext,里面包含SparkContext
    val ssc = new StreamingContext(conf, Seconds(interval.toInt))

    //获取executor instances数量,用于自定义分区
    val partition = ssc.sparkContext.getConf.get("spark.executor.instances", "3").toInt

    //输出当前运行参数
    println(s"input params:appName:$appName \t bootstrapServers:$bootstrapServers \t " +
      s"groupId:$groupId \t interval:$interval \t topicList:$topicList \t maxRatePerPartition:$maxRatePerPartition \t " +
      s"updateInterval:$updateInteval \t numExecutors:$partition \t " +
      s"updateInteval:$updateInteval minute \t apTimeOut:$apTimeOut")

    //consumer sets
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> bootstrapServers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[ByteArrayDeserializer],
      "group.id" -> groupId,
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    //topic sets
    val topics = topicList.split(",")

    //create direct stream
    val stream = KafkaUtils.createDirectStream[String, Array[Byte]](
      ssc,
      PreferConsistent,
      Subscribe[String, Array[Byte]](topics, kafkaParams)
    )

    stream.foreachRDD(rdd => {
      //更新共享数据,此处相关mysql操作会对kafka offsets commit有影响,比较奇葩......
      updateData(rdd.sparkContext, updateInteval.toInt)
      //获取共享数据
      val strategyData = BroadcastWrapper.getStrategyInstance(rdd.sparkContext).value
      val apAreaData = BroadcastWrapper.getApAreaInstance(rdd.sparkContext).value
      strategyData.foreach(println(_))
      //解析数据
      rdd.map(record => {
        val keys = record.key().split("_")
        val time = keys(3)
        val apMac = keys(1).toUpperCase
        val data = record.value()
        val userMacBytes = data.slice(28, 34)
        val userMac = BytesUtil.toHex(userMacBytes).toUpperCase
        //返回三元元组 (userMac , apMac , logTime)
        (MacUtil.formatMac(userMac), MacUtil.formatMac(apMac), time)
      }).filter(t => strategyData.contains(t._1)) //过虑数据
        .map(t => {
        //获取数据详情,区域ID与区域名称
        val apAreaInfo = apAreaData.get(t._2).getOrElse((0, "cannot confirm area info"))
        //获取策略信息
        val strategyInfo = strategyData.get(t._1).get
        //返回二元元组(userMac+"_"+areaId+"_"+streateId+"_"+areaName , logTime)
        (t._1 + "_" + apAreaInfo._1 + "_" + strategyInfo + "_" + apAreaInfo._2, t._3.toLong)
      }).repartitionAndSortWithinPartitions(
        //自定义分区,相同key数据进入同一partition
        new Partitioner {
          override def getPartition(key: Any): Int = {
            Math.abs(key.toString.hashCode) % partition
          }

          override def numPartitions: Int = partition
        }).foreachPartition(iter => {
        //将数据分组聚合
        val result = iter.toStream.groupBy(t => t._1)
        result.foreach(t => {
          val minTime = t._2.minBy(x => x._2)
          val maxTime = t._2.maxBy(x => x._2)
          val keys = t._1.split("_")
          val mas = keys(0) + "_" + keys(1) + "_" + keys(2)
          val areaName = keys(3)
          handlerData(mas, areaName, minTime._2, maxTime._2, apTimeOut)
          println(s"${t._1} \t maxTime:${maxTime._2} \t minTime:${minTime._2}")
        })
      })
    })

    ssc.start()
    ssc.awaitTermination()
  }

  /**
    * 获取帮助信息
    *
    * @param opt
    */
  def usage(opt: Options): Unit = {
    val formatter = new HelpFormatter();
    formatter.printHelp("the information of spark-kafka real-time program", opt);
  }

  /**
    * 设置参数
    *
    * @return
    */
  def getOptions(): Options = {
    val options = new Options
    options.addOption("a", "app-name", true, "app name")
    options.addOption("i", "interval", true, "interval time (seconds)")
    options.addOption("b", "bootstrap-servers", true, "kafka broker list")
    options.addOption("g", "group-id", true, "kafka group id")
    options.addOption("t", "topics", true, "topic name list")
    options.addOption("m", "max-rate", true, "maxRatePerPartition or messages per second for each partiton")
    options.addOption("u", "update-interval", true, "the interval of update data,unit of minute")
    options.addOption("o", "ap-timeout", true, "time out of ap heart,unit of minute")
    options.addOption("h", "help", false, "help")
  }

  /**
    * 验证输入参数
    *
    * @param cmd
    * @param opts
    * @return
    */
  def validateOptions(cmd: CommandLine, opts: Options): Boolean = {
    var flag: Boolean = true
    val options = opts.getOptions.toArray()
    for (opt <- options) {
      if (opt.isInstanceOf[Option]) {
        val myOpt = opt.asInstanceOf[Option]
        val param: String = myOpt.getLongOpt
        if (!(param == "help")) {
          val isHas: Boolean = cmd.hasOption(param)
          if (!isHas) flag = false
        }
      }
    }

    flag
  }

  /**
    * 根据所设置的时间间隔更新共享数据<br/>
    * 此处更新会导致kafka offsets 自动commit,如有必要自行控制mysql事务
    *
    * @param sc       SparkContext
    * @param interval 更新间隔 单位:分钟
    */
  def updateData(sc: SparkContext, interval: Int): Unit = {
    val cal = Calendar.getInstance()
    val minute = cal.get(Calendar.MINUTE)
    val second = cal.get(Calendar.SECOND)
    if (minute % interval == 0 && second == 0) {
      BroadcastWrapper.update(sc, true)
    }
    logger.info("update broadcast......")
  }

  /**
    * 处理数据
    *
    * @param mas       usermac+"_"+areaId+"_"+strategyId
    * @param areaName  区域名称
    * @param minTime   在本次批处理中最小时间
    * @param maxTime   在本次批处理中最大时间
    * @param apTimeOut 与ap连接超时间间隔
    */
  def handlerData(mas: String, areaName: String, minTime: Long, maxTime: Long, apTimeOut: Int): Unit = {
    val row = CassandraUtil.queryToOne(s"select end_time from test.monitor_result where mas='$mas' limit 1")

    /**
      * 方法重复调用,添加新记录
      */
    def addNewData: Unit = {
      //添加cassandra数据
      val insertCql = s"insert into test.monitor_result(mas,begin_time,end_time,status) values('$mas',$minTime,$maxTime,0)"
      CassandraUtil.insertOrUpdate(insertCql)

      //将数据写入ES,用于索引
      val info = mas.split("_")
      val obj = new util.HashMap[String, Object]()
      obj.put("user_mac", info(0))
      obj.put("area_id", info(1))
      obj.put("strategy_id", info(2))
      obj.put("begin_time", java.lang.Long.valueOf(minTime))
      obj.put("area_name", areaName)
      ESUtil.addIndex("monitor_index", "monitor_detect", obj)
    }

    if (row == null) {
      addNewData
    } else {
      val endTime = row.getLong("end_time")
      val beginTime = row.getLong("begin_time")
      //与AP连接超时判定,成立则新增数据,否则更新最后时间
      if (minTime - endTime > 1000 * 60 * apTimeOut) {
        addNewData
      } else {
        val updateCql = s"update test.monitor_result set end_time=$maxTime where mas='$mas' and begin_time=$beginTime"
        CassandraUtil.insertOrUpdate(updateCql)
      }
    }
  }
}
