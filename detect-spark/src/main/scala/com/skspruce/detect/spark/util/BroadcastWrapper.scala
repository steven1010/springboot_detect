package com.skspruce.detect.spark.util

import com.skspruce.detect.model.Strategy
import com.skspruce.detect.utils.SQLHelper
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast

import scala.collection.mutable.Map

object BroadcastWrapper {

  @volatile private var apAreaInstance: Broadcast[Map[String,Tuple2[Int,String]]] = null
  @volatile private var strategyInstance:Broadcast[Map[String,Strategy]] = null

  def main(args: Array[String]): Unit = {
    //val strategy = getStrategy()
    val ss = getApArea()
    println(ss.mkString("-->"))
    //println(strategy.mkString("\t"))
  }

  private def getApArea(): Map[String,Tuple2[Int,String]] = {
    val conn = SQLHelper.getInstance().getIasConnection
    //此处设置为手动,否则影响kafka offsets commit....奇葩.....
    conn.setAutoCommit(false)
    val apAreaSql = "select ap.mac mac ,tap.area_id id,area.name name from tb_ap ap " +
      "left join tb_area_ap tap on ap.id=tap.ap_id " +
      "left join tb_area area on tap.area_id=area.id " +
      "where tap.area_id is not null"

    val result = conn.prepareStatement(apAreaSql).executeQuery()
    val map = scala.collection.mutable.Map[String, Tuple2[Int, String]]()
    while (result.next()) {
      val apMac = result.getString("mac").toUpperCase
      val areaId = result.getInt("id")
      val areaName = result.getString("name")

      map += (apMac -> (areaId, areaName))
    }
    conn.close()

    map
  }

  private def getStrategy():scala.collection.mutable.Map[String,Strategy]={
    val conn = SQLHelper.getInstance().getDetectConnection
    //此处设置为手动,否则会影响kafka offsets commit....奇葩.....
    conn.setAutoCommit(false)
    val strategySql = "select id,name,mac,account_type,account_id,area_ids," +
      "report_type,report_target,report_level from strategy"
    val result = conn.prepareStatement(strategySql).executeQuery()

    val map = scala.collection.mutable.Map[String, Strategy]()
    while (result.next()) {

      val strategy = new Strategy

      strategy.setId(result.getInt("id"))
      strategy.setName(result.getString("name"))
      strategy.setMac(result.getString("mac").toUpperCase)
      strategy.setAccountType(result.getInt("account_type"))
      strategy.setAccountId(result.getString("account_id"))
      strategy.setAreaIds(result.getString("area_ids"))
      strategy.setReportType(result.getInt("report_type"))
      strategy.setReportTarget(result.getString("report_target"))
      strategy.setReportLevel(result.getInt("report_level"))

      map += (strategy.getMac -> strategy)
    }
    conn.close()

    map
  }

  def update(sc: SparkContext, blocking: Boolean = false): Unit = {
    if (apAreaInstance != null)
      apAreaInstance.unpersist(blocking)
    apAreaInstance = sc.broadcast(getApArea())

    if(strategyInstance != null)
      strategyInstance.unpersist(blocking)
    strategyInstance = sc.broadcast(getStrategy())
  }

  def getApAreaInstance(sc: SparkContext): Broadcast[Map[String,Tuple2[Int,String]]] = {
    if (apAreaInstance == null) {
      synchronized {
        if (apAreaInstance == null) {
          apAreaInstance = sc.broadcast(getApArea())
        }
      }
    }
    apAreaInstance
  }

  def getStrategyInstance(sc: SparkContext): Broadcast[Map[String,Strategy]] = {
    if (strategyInstance == null) {
      synchronized {
        if (strategyInstance == null) {
          strategyInstance = sc.broadcast(getStrategy())
        }
      }
    }
    strategyInstance
  }
}
