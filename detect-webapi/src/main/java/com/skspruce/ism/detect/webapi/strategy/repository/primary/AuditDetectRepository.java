package com.skspruce.ism.detect.webapi.strategy.repository.primary;

import com.skspruce.ism.detect.webapi.strategy.entity.AuditDetect;
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
