package com.skspruce.ism.detect.webapi.strategy.repository.primary;

import com.skspruce.ism.detect.webapi.strategy.entity.AuditDetect;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public interface AuditDetectRepository extends MongoRepository<AuditDetect, String>, AuditDetectDao {
    @Query(value="{'_id':{'$in':?0}}")
    List<AuditDetect> findByIdIn(Set<String> ids);

    AuditDetect findBy_id(String id);

    @Query(value="{'UserMacString':{'$in':?0},'Time' : {'$gt' : ?1, '$lt' : ?2}}")
    List<AuditDetect> findByApMacIn(Set<String> macs, Date beginTime, Date endTime);

    @Query(value="{'UserMacString':?0,'Time' : {'$gt' : ?1, '$lt' : ?2}}")
    List<AuditDetect> findByUserMacString(String macs, Date beginTime, Date endTime);

    @Query(value="{'UserMacString':?0}")
    AuditDetect findAuditDetect(String userMac);


}
