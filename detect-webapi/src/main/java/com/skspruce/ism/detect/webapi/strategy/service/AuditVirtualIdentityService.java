package com.skspruce.ism.detect.webapi.strategy.service;

import com.skspruce.ism.detect.webapi.strategy.RestConstants;
import com.skspruce.ism.detect.webapi.strategy.repository.AuditVirtualIdentityEsRepository;
import com.skspruce.ism.detect.webapi.strategy.util.RestUtil;
import com.skspruce.ism.detect.webapi.strategy.vo.Message;
import com.skspruce.ism.detect.webapi.strategy.vo.ResponseMessage;
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
public class AuditVirtualIdentityService {

    private static final Logger logger = LoggerFactory.getLogger(AuditVirtualIdentityService.class);
    @Autowired
    private AuditVirtualIdentityEsRepository auditVirtualIdentityEsRepository;

    public String findAuditDetect(String accout, String beginTime, String endTime) {

        JSONObject modelMap = new JSONObject();
        ResponseMessage responseMessage = RestUtil.addResponseMessageForModelMap(modelMap);

        try{
            SearchResponse searchResponse = auditVirtualIdentityEsRepository.findIdByAccount(accout, beginTime, endTime);

            SearchHits hits = searchResponse.getHits();

            Set<String> ids = new HashSet<String>();
            for(int i = 0; i< hits.getHits().length; i++) {
                ids.add(hits.getAt(i).getId());
            }
            if(ids.size() > 0) {
//                List<AuditDetect> auditDetects = auditDetectRepository.findByIdIn(ids);

//                modelMap.put("data", auditDetects);
            }
        }catch (Exception e) {
            logger.error("", e);
            responseMessage
                    .setStatus(RestConstants.ReturnResponseMessageFailed);
            responseMessage.setMessage(new Message("查询轨迹失败!", "search track failed"));
            modelMap.put(RestConstants.ReturnResponseMessage, responseMessage);
            return modelMap.toString();
        }



        return modelMap.toString();
    }
}
