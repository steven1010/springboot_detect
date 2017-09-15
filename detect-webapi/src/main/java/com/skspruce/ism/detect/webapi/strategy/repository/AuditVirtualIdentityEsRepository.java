package com.skspruce.ism.detect.webapi.strategy.repository;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
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

    public SearchResponse findIdByAccount(String account, String startTime, String endTime) {

        SearchRequestBuilder queryBuilder = prepareSearch();
        queryBuilder.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("AppLoginAccount",account)).filter(QueryBuilders.rangeQuery("Time").gt(startTime).lt(endTime))).setFrom(0).setSize(10000);
        SearchResponse searchResponse = queryBuilder.get();

        return  searchResponse;
    }
}
