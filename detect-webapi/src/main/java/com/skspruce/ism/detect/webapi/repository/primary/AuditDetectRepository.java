package com.skspruce.ism.detect.webapi.repository;

import com.skspruce.ism.detect.webapi.entity.AuditDetect;
import com.skspruce.ism.detect.webapi.repository.primary.AuditDetectDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public interface AuditDetectRepository extends MongoRepository<AuditDetect, String>, AuditDetectDao {
    @Query(value="{'_id':{'$in':?0}}")
    List<AuditDetect> findByIdIn(Set<String> ids);

    AuditDetect findBy_id(String id);
}
