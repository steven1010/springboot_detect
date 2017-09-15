package com.skspruce.ism.detect.webapi.strategy.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.skspruce.ism.detect.webapi.strategy.entity.StrategyEvent;
import com.skspruce.ism.detect.webapi.strategy.util.ESUtil;
import javafx.scene.control.Pagination;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 策略响应事件相关API controller
 */
@RestController
@RequestMapping("/strategy_event")
public class StrategyEventController {

    public static Logger logger = LoggerFactory.getLogger(StrategyEventController.class);

    public static void main(String[] args) {
        SpringApplication.run(StrategyEventController.class, args);
    }

    @RequestMapping(value = "/get")
    public List<StrategyEvent> getPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "size", defaultValue = "5") Integer size,
                                       @RequestParam(value = "sortField", defaultValue = "begin_time") String sortField,
                                       @RequestParam(value = "sortType", defaultValue = "desc") String sortType) {

        SearchRequestBuilder srb = ESUtil.getClient().prepareSearch("detect");
        srb.setSearchType(SearchType.DFS_QUERY_AND_FETCH);
        srb.setTypes("strategy_event");

        srb.setQuery(QueryBuilders.matchQuery("area_name", "办公"));
        srb.setFrom((page - 1) * size).setSize(size).setExplain(true);
        srb.addSort(sortField, SortOrder.DESC);

        SearchResponse searchResponse = srb.execute().actionGet();
        SearchHits shs = searchResponse.getHits();
        List<StrategyEvent> list = new ArrayList<>();
        for (SearchHit sh : shs) {
            Map source = sh.getSource();
            String str = JSON.toJSONString(source);
            System.out.println("################"+str);
            StrategyEvent strategyEvent = JSONObject.parseObject(str, StrategyEvent.class);
            list.add(strategyEvent);
        }

        return list;
    }

}
