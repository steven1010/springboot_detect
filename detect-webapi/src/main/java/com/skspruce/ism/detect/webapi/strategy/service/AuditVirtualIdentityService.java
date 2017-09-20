package com.skspruce.ism.detect.webapi.strategy.service;

import com.skspruce.ism.detect.webapi.strategy.RestConstants;
import com.skspruce.ism.detect.webapi.strategy.entity.AuditDetect;
import com.skspruce.ism.detect.webapi.strategy.repository.AuditDetectEsRepository;
import com.skspruce.ism.detect.webapi.strategy.repository.AuditVirtualIdentityEsRepository;
import com.skspruce.ism.detect.webapi.strategy.repository.primary.AuditDetectRepository;
import com.skspruce.ism.detect.webapi.strategy.util.RestUtil;
import com.skspruce.ism.detect.webapi.strategy.util.TimeUtils;
import com.skspruce.ism.detect.webapi.strategy.vo.Message;
import com.skspruce.ism.detect.webapi.strategy.vo.ResponseMessage;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuditVirtualIdentityService {

    private static final Logger logger = LoggerFactory.getLogger(AuditVirtualIdentityService.class);

    @Autowired
    private AuditVirtualIdentityEsRepository auditVirtualIdentityEsRepository;

    @Autowired
    private AuditDetectRepository auditDetectRepository;

    @Autowired
    private AuditDetectEsRepository auditDetectEsRepository;

    /**
     * 查询虚拟账号历史轨迹
     * @param account
     * @param beginTime
     * @param endTime
     * @return
     */
    public String findAuditDetect(String account, Integer type, String beginTime, String endTime) {

        JSONObject modelMap = new JSONObject();
        ResponseMessage responseMessage = RestUtil.addResponseMessageForModelMap(modelMap);


        try{
            SearchResponse searchResponse = auditVirtualIdentityEsRepository.findMacByAccount(account, type, beginTime, endTime);

            SearchHits hits = searchResponse.getHits();

            Set<String> userMacStrings = new HashSet<>();
            Map<String, Set<String>> userMacMap = new HashMap<String, Set<String>>();
            for(int i = 0; i< hits.getHits().length; i++) {
                if(userMacMap.get(hits.getAt(i).getSource().get("AppLoginAccount").toString()) == null) {
                    userMacMap.put(hits.getAt(i).getSource().get("AppLoginAccount").toString(), new HashSet<String>());
                }
                userMacMap.get(hits.getAt(i).getSource().get("AppLoginAccount").toString()).add(hits.getAt(i).getSource().get("UserMacString").toString());
                userMacStrings.add(hits.getAt(i).getSource().get("UserMacString").toString());
            }
            Map<String, List<AuditDetect>> auditDetectLists = new HashMap<String, List<AuditDetect>>();
            Set<String> keySet = userMacMap.keySet();
            List<AuditDetect> auditDetects = auditDetectRepository.findByApMacIn(userMacStrings, TimeUtils.string2Date(TimeUtils.MONGODBFORMAT, beginTime), TimeUtils.string2Date(TimeUtils.MONGODBFORMAT, endTime));
            for (String accountStr : keySet) {

                for(AuditDetect auditDetect : auditDetects) {
                    if (userMacMap.get(accountStr).contains(auditDetect.getUserMacString())) {
                        if (auditDetectLists.get(accountStr) == null) {
                            auditDetectLists.put(accountStr, new ArrayList<AuditDetect>());
                        }
                        auditDetectLists.get(accountStr).add(auditDetect);
                    }
                }
//                for(String usermac: userMacMap.get(accountStr)) {
//                    SearchResponse searchResponse_account =  auditDetectEsRepository.findIdByUserMac(usermac, beginTime, endTime);
//
//                    SearchHits hits_account = searchResponse_account.getHits();
//
//                    Map<String, Set<String>> dMap = new HashMap<String, Set<String>>();
//                    for(int i = 0; i< hits_account.getHits().length; i++) {
//                        if(dMap.get(usermac) == null) {
//                            dMap.put(usermac, new HashSet<String>());
//                        }
//                        dMap.get(usermac).add(hits_account.getAt(i).getId());
//                    }
//                    Set<String> keySet_account = userMacMap.keySet();
//
//                    for (String mac : keySet_account) {
//                        List<AuditDetect> auditDetects = auditDetectRepository.findByIdIn(userMacMap.get(mac));
//                        if(auditDetectLists.get(accountStr) == null) {
//                            auditDetectLists.put(accountStr, new ArrayList<AuditDetect>());
//                        }
//                        auditDetectLists.get(accountStr).addAll(auditDetects);
//                    }
//
//                }
                auditDetectLists.put(accountStr, auditDetects);
            }
            modelMap.put("data", auditDetectLists);
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

    /**
     * 实时查询虚拟账号位置
     * @param account
     * @param type 账号类型
     * @return
     */
    public String findLastAuditDetect(String account, Integer type) {

        JSONObject modelMap = new JSONObject();
        ResponseMessage responseMessage = RestUtil.addResponseMessageForModelMap(modelMap);

        try{
            SearchResponse searchResponse = auditVirtualIdentityEsRepository.findLastMacByAccount(account, type);

            SearchHits hits = searchResponse.getHits();

            Map<String, String> userMacMap = new HashMap<String, String>();
            for(int i = 0; i< hits.getHits().length; i++) {
                if(userMacMap.get(hits.getAt(i).getSource().get("AppLoginAccount").toString()) == null) {
                    userMacMap.put(hits.getAt(i).getSource().get("AppLoginAccount").toString(), hits.getAt(i).getSource().get("ApMacString").toString());
                }
            }
            Map<String, AuditDetect> auditDetectLists = new HashMap<String, AuditDetect>();
            Set<String> keySet = userMacMap.keySet();
            Set<String> ids = new HashSet<String>();
            List<AuditDetect> auditDetects = new ArrayList<>();
            String result = auditDetectRepository.findByUserMacString(userMacMap.values());
            logger.info("findLastAuditDetect result: "  +result);
            JSONObject resultObject = JSONObject.fromObject(result);

            JSONArray datas = resultObject.getJSONArray("result");
            for (Object dataObject : datas) {
                JSONObject idObject = (JSONObject)dataObject;
                JSONObject data = idObject.getJSONObject("_id");
                AuditDetect auditDetect = new AuditDetect();
                auditDetect.setApMacString(data.getString("apMacString"));
                auditDetect.setLocation(data.getString("location"));
                auditDetect.setPlaceCode(Long.valueOf(data.getString("placeCode")));
                auditDetect.setPlaceName(data.getString("placeName"));
                auditDetects.add(auditDetect);
            }

//            for (String accountStr : keySet) {

//                SearchResponse searchResponse_account =  auditDetectEsRepository.findLastIdByUserMac_account(userMacMap.get(accountStr));
//                SearchHits hits_account = searchResponse_account.getHits();
//
//                if(hits_account.getHits().length>0) {
//                    ids.add(hits_account.getAt(0).getId());
//                }
//                AuditDetect auditDetect = auditDetectRepository.findAuditDetect(userMacMap.get(accountStr));
//                auditDetectLists.put(accountStr, auditDetect);


//            }
//            List<AuditDetect> auditDetects = auditDetectRepository.findByIdIn(ids);
//            Set<String> accountSet = userMacMap.keySet();
//
            for(String accountStr : keySet) {
                for(AuditDetect auditDetect : auditDetects) {
                    if(userMacMap.get(accountStr).equals(auditDetect.getApMacString())) {
                        auditDetectLists.put(accountStr, auditDetect);
                    }
                }
            }

            modelMap.put("data", auditDetectLists);
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
