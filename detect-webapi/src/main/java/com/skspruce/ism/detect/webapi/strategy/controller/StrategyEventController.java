package com.skspruce.ism.detect.webapi.strategy.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datastax.driver.core.Row;
import com.skspruce.ism.detect.webapi.strategy.entity.Strategy;
import com.skspruce.ism.detect.webapi.strategy.entity.StrategyEvent;
import com.skspruce.ism.detect.webapi.strategy.repo.StrategyJpaRepository;
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
import org.springframework.web.servlet.ModelAndView;

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
     * @param number    当前页码
     * @param size      每页数据条数
     * @param sortField 排序字段
     * @param sortType  排序类型
     * @param areaName  区域名称匹配
     * @param areaId    区域ID匹配
     * @param userMac   目标MAC匹配
     * @param status    处理状态匹配
     * @return {@code ESPage<StrategyEvent>}
     */
    @RequestMapping(value = "/get_page")
    public ESPage<StrategyEvent> getPage(@RequestParam(value = "number", defaultValue = "0") Integer number,
                                         @RequestParam(value = "size", defaultValue = "5") Integer size,
                                         @RequestParam(value = "sortField", defaultValue = "begin_time") String sortField,
                                         @RequestParam(value = "sortType", defaultValue = "desc") String sortType,
                                         @RequestParam(value = "areaName", defaultValue = "") String areaName,
                                         @RequestParam(value = "areaId", defaultValue = "") String areaId,
                                         @RequestParam(value = "userMac", defaultValue = "") String userMac,
                                         @RequestParam(value = "status", defaultValue = "") String status) {

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
            QueryBuilder areaIdBuilder = QueryBuilders.termQuery("user_mac", userMac);
            all.must(areaIdBuilder);
        }
        if (!status.trim().equals("")) {
            QueryBuilder areaIdBuilder = QueryBuilders.termQuery("status", status);
            all.must(areaIdBuilder);
        }

        srb.setQuery(all);
        srb.setFrom(number * size).setSize(size).setExplain(true);
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
            String cql = "select end_time,handle_time from " + keyspace + "." + table
                    + " where mas='" + mas + "' and begin_time=" + strategyEvent.getBeginTime();
            logger.info(cql);
            Row row = CassandraUtil.queryToOne(cql);
            logger.info("row is null:" + (row == null));
            strategyEvent.setEndTime(row.getLong("end_time"));
            strategyEvent.setHandleTime(row.getLong("handle_time"));

            Strategy strategy = sjr.findOne(strategyEvent.getStrategyId());
            strategyEvent.setStrategyName(strategy.getName());

            list.add(strategyEvent);
        }

        ESPage<StrategyEvent> esPage = new ESPage<>();
        esPage.setNumber((long) number);
        esPage.setSize(size);
        esPage.setNumberOfElements(list.size());
        if (number == 0) {
            esPage.setFirst(true);
        }
        if (list.size() < size) {
            esPage.setLast(true);
        }
        esPage.setContent(list);
        esPage.setTotalElements(shs.getTotalHits());
        esPage.setTotalPage(esPage.getTotalElements() / size + 1);

        ESSortInfo esi = new ESSortInfo();
        esi.setProperty(sortType.toUpperCase());
        if (sortType.toUpperCase().equals("DESC")) {
            esi.setDescending(true);
        } else {
            esi.setAssending(true);
        }
        esi.setDirection(sortField);
        esPage.setSort(Collections.singletonList(esi));

        return esPage;
    }

    /**
     * 获取未处理告警数量
     *
     * @return {@code Map<String, Long>}
     */
    @RequestMapping(value = "/get_warning")
    public Map<String, Long> getWarningCount() {
        //ES query
        SearchRequestBuilder srb = ESUtil.getClient().prepareSearch(esIndex);
        srb.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        srb.setTypes(esType);

        //设置未处理告警与获取数据数量
        srb.setQuery(QueryBuilders.termQuery("status", 0));
        srb.setFrom(0).setSize(1);

        //执行查询
        SearchResponse response = srb.execute().actionGet();

        //返回未处理告警数量对象
        Map<String, Long> map = new HashMap<>();
        map.put("count", response.getHits().getTotalHits());

        return map;
    }

    /**
     * 策略事件响应确认
     *
     * @param strategyEvent
     * @return {@code ModelAndView}
     */
    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    public StrategyEvent confirm(@RequestBody StrategyEvent strategyEvent) throws Exception {
        //更新ES
        String esId = strategyEvent.getId();
        XContentBuilder builder = jsonBuilder().startObject().field("status", 1).endObject();
        ESUtil.updateById(esIndex, esType, esId, builder);
        //将处理状态标记为已处理
        strategyEvent.setStatus(1);

        //更新cassandra
        long time = System.currentTimeMillis();
        String mas = strategyEvent.getUserMac() + "_" + strategyEvent.getAreaId() + "_" + strategyEvent.getStrategyId();
        String cql = "update " + keyspace + "." + table + " set handle_time=" + time
                + " where mas='" + mas + "' and begin_time=" + strategyEvent.getBeginTime();
        CassandraUtil.executeCql(cql);
        //更新处理时间
        strategyEvent.setHandleTime(time);

        return strategyEvent;
    }

    /**
     * 删除操作
     *
     * @param content
     * @return {@code ModelAndView}
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ModelAndView delete(@RequestBody List<StrategyEvent> content) {
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
        //ES处理为懒操作,防止数据未更新,睡眠2秒
        try {
            Thread.sleep(1000 * 2);
        } catch (InterruptedException e) {
            logger.info("interrupted error:", e);
        }

        return new ModelAndView("/strategy_event/get_page");
    }

}
