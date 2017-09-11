package com.skspruce.ism.detect.webapi.repository;

import com.skspruce.ism.detect.webapi.util.TimeUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

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

        String[] likeText = new String[] {userMac};
        SearchRequestBuilder queryBuilder = prepareSearch();
//        queryBuilder.setQuery(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("UserMacString", "*"+userMac+"*")).filter(QueryBuilders.rangeQuery("Time").gt(startTime).lt(endTime))).setFrom(0).setSize(10000);
        queryBuilder.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("UserMacString",userMac)).filter(QueryBuilders.rangeQuery("Time").gt(startTime).lt(endTime))).setFrom(0).setSize(10000);
        SearchResponse searchResponse = queryBuilder.get();

        return  searchResponse;
    }
}
