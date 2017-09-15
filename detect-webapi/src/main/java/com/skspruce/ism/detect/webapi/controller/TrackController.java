package com.skspruce.ism.detect.webapi.controller;


import com.skspruce.ism.detect.webapi.service.AuditDetectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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
    public String getTrackByMac(String userMac) {
        return auditDetectService.findAuditDetect(userMac);
    }

}
