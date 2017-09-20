package com.skspruce.ism.detect.webapi.strategy.service;


import com.skspruce.ism.detect.webapi.strategy.RestConstants;
import com.skspruce.ism.detect.webapi.strategy.entity.AuditDetect;
import com.skspruce.ism.detect.webapi.strategy.repository.AuditDetectEsRepository;
import com.skspruce.ism.detect.webapi.strategy.repository.primary.AuditDetectRepository;
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

import java.util.*;

@Service
public class AuditDetectService {

    private static final Logger logger = LoggerFactory.getLogger(AuditDetectService.class);

    @Autowired
    private AuditDetectEsRepository auditDetectEsRepository;

    @Autowired
    private AuditDetectRepository auditDetectRepository;

    /**
     * 查询用户历史轨迹
     * @param userMac
     * @param beginTime
     * @param endTime
     * @return
     */
    public String findAuditDetect(String userMac, String beginTime, String endTime) {

        JSONObject modelMap = new JSONObject();
        ResponseMessage responseMessage = RestUtil.addResponseMessageForModelMap(modelMap);

        try{
            SearchResponse searchResponse = auditDetectEsRepository.findIdByUserMac(userMac, beginTime, endTime);

            SearchHits hits = searchResponse.getHits();
            Set<String> ids = new HashSet<>();
            Map<String, Set<String>> userMacMap = new HashMap<String, Set<String>>();
            for(int i = 0; i< hits.getHits().length; i++) {
                if(userMacMap.get(hits.getAt(i).getSource().get("UserMacString").toString()) == null) {
                    userMacMap.put(hits.getAt(i).getSource().get("UserMacString").toString(), new HashSet<String>());
                }
                userMacMap.get(hits.getAt(i).getSource().get("UserMacString").toString()).add(hits.getAt(i).getId());
                ids.add(hits.getAt(i).getId());
            }
            Map<String, List<AuditDetect>> auditDetectLists = new HashMap<String, List<AuditDetect>>();
            Set<String> keySet = userMacMap.keySet();
            List<AuditDetect> auditDetects = auditDetectRepository.findByIdIn(ids);
            for (String mac : keySet) {
                for(AuditDetect auditDetect : auditDetects) {
                    if (userMacMap.get(mac).contains(auditDetect.get_id())) {
                        if (auditDetectLists.get(mac) == null) {
                            auditDetectLists.put(mac, new ArrayList<AuditDetect>());
                        }
                        auditDetectLists.get(mac).add(auditDetect);
                    }
                }
//                List<AuditDetect> auditDetects = auditDetectRepository.findByIdIn(userMacMap.get(mac));
//                auditDetectLists.put(mac, auditDetects);
            }
            modelMap.put("data", auditDetectLists);

        }catch (Exception e) {
            logger.error("", e);
            responseMessage
                    .setStatus(RestConstants.ReturnResponseMessageFailed);
            responseMessage.setMessage(new Message("查询历史轨迹失败!", "search track failed"));
            modelMap.put(RestConstants.ReturnResponseMessage, responseMessage);
            return modelMap.toString();
        }



        return modelMap.toString();
    }

    /**
     * 实时查询用户位置
     * @param userMac
     * @return
     */
    public String findLastAuditDetect(String userMac) {

        JSONObject modelMap = new JSONObject();
        ResponseMessage responseMessage = RestUtil.addResponseMessageForModelMap(modelMap);

        try{
            SearchResponse searchResponse = auditDetectEsRepository.findLastIdByUserMac(userMac);
            SearchHits hits = searchResponse.getHits();
            Map<String, String> userMacMap = new HashMap<String, String>();
            for(int i = 0; i< hits.getHits().length; i++) {
                if(userMacMap.get(hits.getAt(i).getSource().get("UserMacString").toString()) == null) {
                    userMacMap.put(hits.getAt(i).getSource().get("UserMacString").toString(), hits.getAt(i).getId());
                }
            }
            Map<String, AuditDetect> auditDetectLists = new HashMap<String, AuditDetect>();
            Set<String> keySet = userMacMap.keySet();

            for (String mac : keySet) {
                AuditDetect auditDetects = auditDetectRepository.findBy_id(userMacMap.get(mac));
                auditDetectLists.put(mac, auditDetects);
            }
            modelMap.put("data", auditDetectLists);
        }catch (Exception e) {
            logger.error("", e);
            responseMessage
                    .setStatus(RestConstants.ReturnResponseMessageFailed);
            responseMessage.setMessage(new Message("查询实时追踪失败!", "search track failed"));
            modelMap.put(RestConstants.ReturnResponseMessage, responseMessage);
            return modelMap.toString();
        }



        return modelMap.toString();
    }
}
