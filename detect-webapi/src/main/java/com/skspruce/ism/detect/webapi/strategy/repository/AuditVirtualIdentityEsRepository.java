package com.skspruce.ism.detect.webapi.strategy.repository;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Repository;

@Repository
public class AuditVirtualIdentityEsRepository extends ElasticSearchRepository {


    @Override
    protected String getIndex() {
        return "ias_virtual_identity";
    }

    @Override
    protected String getType() {
        return "AuditVirtualIdentity";
    }

    public SearchResponse findMacByAccount(String account, Integer type, String startTime, String endTime) {

        SearchRequestBuilder queryBuilder = prepareSearch();
        queryBuilder.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.regexpQuery("AppLoginAccount.keyword",".*"+account+".*")).must(QueryBuilders.termQuery("AppType", type)).filter(QueryBuilders.rangeQuery("Time").gt(startTime).lt(endTime))).setFrom(0).setSize(10000);
        SearchResponse searchResponse = queryBuilder.get();

        return  searchResponse;
    }

    public SearchResponse findLastMacByAccount(String account, Integer type) {

        SearchRequestBuilder queryBuilder = prepareSearch();
        queryBuilder.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.regexpQuery("AppLoginAccount.keyword",".*"+account+".*")).must(QueryBuilders.termQuery("AppType", type))).setFrom(0).setSize(10000).addSort("Time", SortOrder.DESC);
        SearchResponse searchResponse = queryBuilder.get();

        return  searchResponse;
    }
}
