package com.skspruce.ism.detect.webapi.service;

import com.skspruce.ism.detect.webapi.RestConstants;
import com.skspruce.ism.detect.webapi.entity.AuditDetect;
import com.skspruce.ism.detect.webapi.repository.AuditDetectEsRepository;
import com.skspruce.ism.detect.webapi.repository.primary.AuditDetectRepository;
import com.skspruce.ism.detect.webapi.util.RestUtil;
import com.skspruce.ism.detect.webapi.vo.Message;
import com.skspruce.ism.detect.webapi.vo.ResponseMessage;
import net.sf.json.JSONObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuditDetectService {

    private static final Logger logger = LoggerFactory.getLogger(AuditDetectService.class);

    @Autowired
    private AuditDetectEsRepository auditDetectEsRepository;

    @Autowired
    private AuditDetectRepository auditDetectRepository;

    public String findAuditDetect(String userMac, String beginTime, String endTime) {

        JSONObject modelMap = new JSONObject();
        ResponseMessage responseMessage = RestUtil.addResponseMessageForModelMap(modelMap);

        try{
            SearchResponse searchResponse = auditDetectEsRepository.findIdByUserMac(userMac, beginTime, endTime);

            SearchHits hits = searchResponse.getHits();

            Set<String> ids = new HashSet<String>();
            for(int i = 0; i< hits.getHits().length; i++) {
                ids.add(hits.getAt(i).getId());
            }
            if(ids.size() > 0) {
               List<AuditDetect> auditDetects = auditDetectRepository.findByIdIn(ids);

                modelMap.put("data", auditDetects);
            }
        }catch (Exception e) {
            logger.error("", e);
            responseMessage
                    .setStatus(RestConstants.ReturnResponseMessageFailed);
            responseMessage.setMessage(new Message("查询轨迹失败!", "search track failed"));
            modelMap.put(RestConstants.ReturnResponseMessage, responseMessage);
            return modelMap.toString();
        }



        return "";
    }
}
