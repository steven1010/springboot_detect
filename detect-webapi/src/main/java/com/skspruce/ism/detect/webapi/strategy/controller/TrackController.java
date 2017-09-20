package com.skspruce.ism.detect.webapi.strategy.controller;


import com.skspruce.ism.detect.webapi.strategy.service.AuditDetectService;
import com.skspruce.ism.detect.webapi.strategy.service.AuditVirtualIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户和虚拟账号轨迹查询 Controller
 */
@RestController
@RequestMapping("/track")
public class TrackController {

    @Autowired
    private AuditDetectService auditDetectService;

    @Autowired
    private AuditVirtualIdentityService auditVirtualIdentityService;

    @RequestMapping(value = "/getTrackByMac", method = RequestMethod.GET)
    public String getTrackByMac(@RequestParam  String userMac, @RequestParam String beginTime, @RequestParam String endTime) {
        return auditDetectService.findAuditDetect(userMac, beginTime, endTime);
    }

    @RequestMapping(value = "/getLastTrackByMac", method = RequestMethod.GET)
    public String getLastTrackByMac(@RequestParam String userMac) {
        return auditDetectService.findLastAuditDetect(userMac);
    }

    @RequestMapping(value = "/getVirtualTrackByMac", method = RequestMethod.GET)
    public String getVirtualTrackByMac(@RequestParam Integer type, @RequestParam String account, @RequestParam String beginTime, @RequestParam String endTime) {
        return auditVirtualIdentityService.findAuditDetect(account, type, beginTime, endTime);
    }

    @RequestMapping(value = "/getLastVirtualTrackByMac", method = RequestMethod.GET)
    public String getLastVirtualTrackByMac(@RequestParam Integer type, @RequestParam String account) {
        return auditVirtualIdentityService.findLastAuditDetect(account, type);
    }

}
