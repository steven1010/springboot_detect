package com.skspruce.ism.detect.webapi.strategy.controller;


import com.skspruce.ism.detect.webapi.strategy.service.AuditDetectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/track")
public class TrackController {

    @Autowired
    private AuditDetectService auditDetectService;

    @RequestMapping(value = "/getTrackByMac", method = RequestMethod.GET)
    public String getTrackByMac(String userMac, String beginTime, String endTime) {
        return auditDetectService.findAuditDetect(userMac, beginTime, endTime);
    }

}
