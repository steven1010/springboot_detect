package com.skspruce.ism.detect.webapi.strategy.repository.primary.impl;


import com.mongodb.BasicDBObject;
import com.skspruce.ism.detect.webapi.strategy.entity.AuditDetect;
import com.skspruce.ism.detect.webapi.strategy.repository.primary.AuditDetectDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class AuditDetectRepositoryImpl implements AuditDetectDao {

    private static final Logger logger = LoggerFactory.getLogger(AuditDetectRepositoryImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<AuditDetect> findAuditDetectByApMacString(String apMac) {
        Query query = new Query();
        Criteria criteria = Criteria.where("ApMacString").is(apMac);
        query.addCriteria(criteria);
        query.skip(0);
        query.limit(1);
        return mongoTemplate.find(query, AuditDetect.class);
    }

    @Override
    public String findByUserMacString(Collection<String> userMac) {

        Criteria criteria = Criteria.where("ApMacString").in(userMac);

        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.group("apMacString", "location", "placeCode", "placeName"));

        AggregationResults<BasicDBObject> aggregationResults = mongoTemplate.aggregate(aggregation, AuditDetect.class, BasicDBObject.class);
        return aggregationResults.getRawResults().toString();
//        return null;
    }
//    @Override
//    public List<AuditDetect> findByApMacIn(Set<String> macs, Date beginTime, Date endTime) {
//        Query query = new Query();
//        Criteria criteria = Criteria.where("UserMacString").in(macs).andOperator(Criteria.where("Time").gte(beginTime),
//                Criteria.where("Time").lte(endTime));
//        query.addCriteria(criteria);
//        return mongoTemplate.find(query, AuditDetect.class);
//    }
}
