package com.skspruce.ism.detect.webapi.strategy.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datastax.driver.core.Row;
import com.skspruce.ism.detect.webapi.strategy.mysqlprimaryentity.Strategy;
import com.skspruce.ism.detect.webapi.strategy.entity.StrategyEvent;
import com.skspruce.ism.detect.webapi.strategy.repository.mysqlprimary.StrategyJpaRepository;
import com.skspruce.ism.detect.webapi.strategy.util.*;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * 策略响应事件相关API controller
 */
@RestController
@RequestMapping("/strategy_event")
public class StrategyEventController {

    public static Logger logger = LoggerFactory.getLogger(StrategyEventController.class);


    public static String DEFAULT_INDEX = "detect";
    public static String DEFAULT_TYPE = "strategy_event";
    public static String DEFAULT_KEYSPACE = "detect";
    public static String DEFAULT_TABLE = "strategy_event";

    private static String keyspace = null;
    private static String table = null;
    private static String esIndex = null;
    private static String esType = null;

    //初始化数据存储信息
    static {
        keyspace = PropertiesUtil.getInstance().getString(PropertiesUtil.CASSANDRA_KEYSPACE, DEFAULT_KEYSPACE);
        table = PropertiesUtil.getInstance().getString(PropertiesUtil.CASSANDRA_TABLE, DEFAULT_TABLE);
        esIndex = PropertiesUtil.getInstance().getString(PropertiesUtil.ES_CLUSTER_INDEX, DEFAULT_INDEX);
        esType = PropertiesUtil.getInstance().getString(PropertiesUtil.ES_CLUSTER_TYPE, DEFAULT_TYPE);
    }

    @Autowired
    StrategyJpaRepository sjr;

    public static void main(String[] args) {
        SpringApplication.run(StrategyEventController.class, args);
    }

    /**
     * 分页查询并返回与JPA同样PAGE模板
     *
     * @param pageIndex 当前页码
     * @param pageSize  每页数据条数
     * @param sortField 排序字段
     * @param sortType  排序类型
     * @param areaName  区域名称匹配
     * @param areaId    区域ID匹配
     * @param userMac   目标MAC匹配
     * @param status    处理状态匹配
     * @return {@code ESPage<StrategyEvent>}
     */
    @RequestMapping(value = "/get_page", method = RequestMethod.GET)
    public Map<String, Object> getPage(@RequestParam(value = "pageIndex", defaultValue = "0") Integer pageIndex,
                                       @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "sortField", defaultValue = "begin_time") String sortField,
                                       @RequestParam(value = "sortType", defaultValue = "desc") String sortType,
                                       @RequestParam(value = "areaName", defaultValue = "", required = false) String areaName,
                                       @RequestParam(value = "areaId", defaultValue = "", required = false) String areaId,
                                       @RequestParam(value = "userMac", defaultValue = "", required = false) String userMac,
                                       @RequestParam(value = "status", defaultValue = "", required = false) String status) {
        Map<String, Object> map = new HashMap<>();
        map.put(PageUtil.ERROR, null);
        map.put(PageUtil.TYPE, null);
        map.put(PageUtil.SUCCESS, true);

        try {
            //ES query
            SearchRequestBuilder srb = ESUtil.getClient().prepareSearch(esIndex);
            srb.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            srb.setTypes(esType);

            BoolQueryBuilder all = QueryBuilders.boolQuery();
            if (!areaName.trim().equals("")) {
                QueryBuilder areaNameBuilder = QueryBuilders.matchQuery("area_name", areaName);
                all.must(areaNameBuilder);
            }
            if (!areaId.trim().equals("")) {
                QueryBuilder areaIdBuilder = QueryBuilders.termQuery("area_id", areaId);
                all.must(areaIdBuilder);
            }
            if (!userMac.trim().equals("")) {
                QueryBuilder areaIdBuilder = QueryBuilders.termQuery("user_mac", userMac.toUpperCase());
                all.must(areaIdBuilder);
            }
            if (!status.trim().equals("")) {
                QueryBuilder areaIdBuilder = QueryBuilders.termQuery("status", status);
                all.must(areaIdBuilder);
            }

            srb.setQuery(all);
            srb.setFrom(pageIndex * pageSize).setSize(pageSize).setExplain(true);
            if (sortType.toUpperCase().equals("DESC")) {
                srb.addSort(sortField, SortOrder.DESC);
            } else {
                srb.addSort(sortField, SortOrder.ASC);
            }

            SearchResponse searchResponse = srb.execute().actionGet();
            SearchHits shs = searchResponse.getHits();
            List<StrategyEvent> list = new ArrayList<>();
            for (SearchHit sh : shs) {
                Map source = sh.getSource();
                String str = JSON.toJSONString(source);
                StrategyEvent strategyEvent = JSONObject.parseObject(str, StrategyEvent.class);
                strategyEvent.setId(sh.getId());

                //cassandra表中主键
                String mas = strategyEvent.getUserMac() + "_" + strategyEvent.getAreaId() + "_" + strategyEvent.getStrategyId();
                String cql = "select end_time,handle_time,event_record from " + keyspace + "." + table
                        + " where mas='" + mas + "' and begin_time=" + strategyEvent.getBeginTime();
                logger.info(cql);
                Row row = CassandraUtil.queryToOne(cql);
                logger.info("row is null:" + (row == null));
                if (row != null) {
                    strategyEvent.setEndTime(row.getLong("end_time"));
                    strategyEvent.setHandleTime(row.getLong("handle_time"));
                    strategyEvent.setEventRecord(row.getString("event_record"));
                }

                Strategy strategy = sjr.findOne(strategyEvent.getStrategyId());
                if (strategy != null) {
                    strategyEvent.setStrategyName(strategy.getName());
                }

                list.add(strategyEvent);
            }
            map.put(PageUtil.DATA, list);
            map.put(PageUtil.TOTAL_PAGE, shs.getTotalHits() / pageSize + 1);
            map.put(PageUtil.TOTAL, shs.getTotalHits());
        } catch (Exception e) {
            logger.error("getPage error:", e);
            map.put(PageUtil.SUCCESS, false);
            map.put(PageUtil.ERROR, e.getMessage());
        }


        return map;
    }

    /**
     * 获取未处理告警数量
     *
     * @return {@code Map<String, Long>}
     */
    @RequestMapping(value = "/get_warning", method = RequestMethod.GET)
    public Map<String, Object> getWarningCount() {
        Map<String, Object> map = new HashMap<>();
        map.put(PageUtil.ERROR, null);
        map.put(PageUtil.TYPE, null);
        map.put(PageUtil.SUCCESS, true);
        try {
            //ES query
            SearchRequestBuilder srb = ESUtil.getClient().prepareSearch(esIndex);
            srb.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            srb.setTypes(esType);

            //设置未处理告警与获取数据数量
            srb.setQuery(QueryBuilders.termQuery("status", 0));
            srb.setFrom(0).setSize(1);

            //执行查询
            SearchResponse response = srb.execute().actionGet();
            Map<String, Object> count = new HashMap<>();
            count.put("count", response.getHits().getTotalHits());
            map.put(PageUtil.DATA, count);
        } catch (Exception e) {
            logger.error("get_warning error:", e);
            map.put(PageUtil.SUCCESS, false);
            map.put(PageUtil.ERROR, e.getMessage());
        }

        return map;
    }

    /**
     * 策略事件响应确认
     *
     * @param strategyEvent
     * @return {@code ModelAndView}
     */
    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    public Map<String, Object> confirm(@RequestBody StrategyEvent strategyEvent) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put(PageUtil.ERROR, null);
        map.put(PageUtil.TYPE, null);
        map.put(PageUtil.SUCCESS, true);
        try {
            //更新ES
            String esId = strategyEvent.getId();
            XContentBuilder builder = jsonBuilder().startObject().field("status", 1).endObject();
            ESUtil.updateById(esIndex, esType, esId, builder);
            //将处理状态标记为已处理
            strategyEvent.setStatus(1);

            //更新cassandra
            long time = System.currentTimeMillis();
            String mas = strategyEvent.getUserMac() + "_" + strategyEvent.getAreaId() + "_" + strategyEvent.getStrategyId();
            String cql = "update " + keyspace + "." + table + " set handle_time=" + time + " handle_record=" + strategyEvent.getEventRecord()
                    + " where mas='" + mas + "' and begin_time=" + strategyEvent.getBeginTime();
            CassandraUtil.executeCql(cql);
            //更新处理时间
            strategyEvent.setHandleTime(time);
            map.put(PageUtil.DATA, strategyEvent);
        } catch (Exception e) {
            logger.error("confirm error:", e);
            map.put(PageUtil.SUCCESS, false);
            map.put(PageUtil.ERROR, e.getMessage());
        }

        return map;
    }

    /**
     * 删除操作
     *
     * @param content
     * @return {@code ModelAndView}
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Map<String, Object> delete(@RequestBody List<StrategyEvent> content) {
        Map<String, Object> map = new HashMap<>();
        map.put(PageUtil.ERROR, null);
        map.put(PageUtil.TYPE, null);
        map.put(PageUtil.SUCCESS, true);
        try {
            List<String> esIds = new ArrayList<>(content.size());
            List<String> cqls = new ArrayList<>(content.size());

            for (StrategyEvent se : content) {
                esIds.add(se.getId());
                String mas = se.getUserMac() + "_" + se.getAreaId() + "_" + se.getStrategyId();
                String cql = "delete from " + keyspace + "." + table + " where mas='" + mas + "' and begin_time=" + se.getBeginTime();
                cqls.add(cql);
            }


            ESUtil.deleteBatch(esIndex, esType, esIds);
            CassandraUtil.batchCqls(cqls);
            //ES处理为懒操作,防止数据未更新,睡眠1秒
            try {
                Thread.sleep(1000 * 1);
            } catch (InterruptedException e) {
                logger.info("interrupted error:", e);
            }
            map.put(PageUtil.DATA, content);
        } catch (Exception e) {
            logger.error("delete error:", e);
            map.put(PageUtil.SUCCESS, false);
            map.put(PageUtil.ERROR, e.getMessage());
        }

        return map;
    }

}
