package com.skspruce.ism.detect.webapi.strategy.repository.primary;

import com.skspruce.ism.detect.webapi.strategy.entity.AuditDetect;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface AuditDetectDao {


    public List<AuditDetect> findAuditDetectByApMacString(String apMac);

    String findByUserMacString(Collection<String> userMac);
}
