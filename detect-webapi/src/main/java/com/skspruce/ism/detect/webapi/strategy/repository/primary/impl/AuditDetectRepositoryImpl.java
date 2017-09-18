package com.skspruce.ism.detect.webapi.strategy.repository.primary.impl;


import com.skspruce.ism.detect.webapi.strategy.entity.AuditDetect;
import com.skspruce.ism.detect.webapi.strategy.repository.primary.AuditDetectDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class AuditDetectRepositoryImpl implements AuditDetectDao {

    private static final Logger logger = LoggerFactory.getLogger(AuditDetectRepositoryImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;
//    @Override
//    public List<AuditDetect> findByApMacIn(Set<String> macs, Date beginTime, Date endTime) {
//        Query query = new Query();
//        Criteria criteria = Criteria.where("UserMacString").in(macs).andOperator(Criteria.where("Time").gte(beginTime),
//                Criteria.where("Time").lte(endTime));
//        query.addCriteria(criteria);
//        return mongoTemplate.find(query, AuditDetect.class);
//    }
}
