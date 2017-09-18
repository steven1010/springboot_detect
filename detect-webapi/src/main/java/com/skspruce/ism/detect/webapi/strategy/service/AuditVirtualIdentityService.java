package com.skspruce.ism.detect.webapi.strategy.service;

import com.skspruce.ism.detect.webapi.strategy.RestConstants;
import com.skspruce.ism.detect.webapi.strategy.entity.AuditDetect;
import com.skspruce.ism.detect.webapi.strategy.repository.AuditVirtualIdentityEsRepository;
import com.skspruce.ism.detect.webapi.strategy.repository.primary.AuditDetectRepository;
import com.skspruce.ism.detect.webapi.strategy.util.RestUtil;
import com.skspruce.ism.detect.webapi.strategy.util.TimeUtils;
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

    @Autowired
    private AuditDetectRepository auditDetectRepository;

    public String findAuditDetect(String account, String beginTime, String endTime) {

        JSONObject modelMap = new JSONObject();
        ResponseMessage responseMessage = RestUtil.addResponseMessageForModelMap(modelMap);


        try{
            SearchResponse searchResponse = auditVirtualIdentityEsRepository.findMacByAccount(account, beginTime, endTime);

            SearchHits hits = searchResponse.getHits();

            Set<String> macs = new HashSet<String>();
            for(int i = 0; i< hits.getHits().length; i++) {
                macs.add(hits.getAt(i).getSource().get("UserMacString").toString());
//                macs.add("5C:F7:E6:D1:FC:11");
            }
            if(macs.size() > 0) {
                List<AuditDetect> auditDetects = auditDetectRepository.findByApMacIn(macs, TimeUtils.string2Date(TimeUtils.MONGODBFORMAT, beginTime), TimeUtils.string2Date(TimeUtils.MONGODBFORMAT, endTime));
                System.out.println(TimeUtils.string2Date(TimeUtils.MONGODBFORMAT, beginTime));
                modelMap.put("data", auditDetects);
            }
        }catch (Exception e) {
            logger.error("", e);
            responseMessage
                    .setStatus(RestConstants.ReturnResponseMessageFailed);
            responseMessage.setMessage(new Message("查询虚拟账号历史轨迹失败!", "search track failed"));
            modelMap.put(RestConstants.ReturnResponseMessage, responseMessage);
            return modelMap.toString();
        }



        return modelMap.toString();
    }

    public String findLastAuditDetect(String userMac, Integer type) {

        JSONObject modelMap = new JSONObject();
        ResponseMessage responseMessage = RestUtil.addResponseMessageForModelMap(modelMap);

        try{
            SearchResponse searchResponse = auditVirtualIdentityEsRepository.findLastMacByAccount(userMac, type);

            SearchHits hits = searchResponse.getHits();

            Set<String> macs = new HashSet<String>();
            for(int i = 0; i< hits.getHits().length; i++) {
                macs.add(hits.getAt(i).getSource().get("ApMacString").toString());
            }
            if(macs.size() > 0) {
                List<AuditDetect> auditDetects = auditDetectRepository.findByIdIn(macs);

                modelMap.put("data", auditDetects);
            }
        }catch (Exception e) {
            logger.error("", e);
            responseMessage
                    .setStatus(RestConstants.ReturnResponseMessageFailed);
            responseMessage.setMessage(new Message("查询虚拟账号实时追踪失败!", "search track failed"));
            modelMap.put(RestConstants.ReturnResponseMessage, responseMessage);
            return modelMap.toString();
        }



        return modelMap.toString();
    }
}
