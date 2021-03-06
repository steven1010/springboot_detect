package com.skspruce.ism.detect.webapi.strategy.repository;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Repository
public class AuditDetectEsRepository extends ElasticSearchRepository {


    @Override
    protected String getIndex() {
        return "ias_detect";
    }

    @Override
    protected String getType() {
        return "AuditDetect";
    }

    public SearchResponse findIdByUserMac(String userMac, String startTime, String endTime) {

        SearchRequestBuilder queryBuilder = prepareSearch();
        String reg = "*"+userMac+"*";
        queryBuilder.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.regexpQuery("UserMacString.keyword", ".*"+userMac+".*")).filter(QueryBuilders.rangeQuery("Time").gt(startTime).lt(endTime))).setFrom(0).setSize(10000);
        SearchResponse searchResponse = queryBuilder.get();

        return  searchResponse;
    }

    public SearchResponse findLastIdByUserMac(String userMac) {

        SearchRequestBuilder queryBuilder = prepareSearch();
        queryBuilder.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.regexpQuery("UserMacString",".*"+userMac+".*"))).setFrom(0).setSize(10000).addSort("Time", SortOrder.DESC);
        SearchResponse searchResponse = queryBuilder.get();

        return  searchResponse;
    }

    public SearchResponse findLastIdByUserMac_account(String userMac) {

        SearchRequestBuilder queryBuilder = prepareSearch();
        queryBuilder.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.regexpQuery("UserMacString",".*"+userMac+".*"))).setFrom(0).setSize(1).addSort("Time", SortOrder.DESC);
        SearchResponse searchResponse = queryBuilder.get();

        return  searchResponse;
    }
}
