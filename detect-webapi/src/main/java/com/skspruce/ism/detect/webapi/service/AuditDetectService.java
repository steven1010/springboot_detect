package com.skspruce.ism.detect.webapi.service;

import com.skspruce.ism.detect.webapi.repository.AuditDetectEsRepository;
import com.skspruce.ism.detect.webapi.repository.primary.AuditDetectRepository;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuditDetectService {

    @Autowired
    private AuditDetectEsRepository auditDetectEsRepository;

    @Autowired
    private AuditDetectRepository auditDetectRepository;

    public void findAuditDetect(String usermac) {
        SearchResponse searchResponse = auditDetectEsRepository.findIdByUserMac("D8:EB:97:26:2F:49", "2017-08-29T04:02:46+08:00", "2017-08-29T04:12:47+08:00");

        SearchHits hits = searchResponse.getHits();

        Set<String> ids = new HashSet<String>();
        for(int i = 0; i< hits.getHits().length; i++) {
            ids.add(hits.getAt(i).getId());
        }
        System.out.println(ids.size());
        System.out.println(auditDetectRepository.findByIdIn(ids).size());
    }
}
