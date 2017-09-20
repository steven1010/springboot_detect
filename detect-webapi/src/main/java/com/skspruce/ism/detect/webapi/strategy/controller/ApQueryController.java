package com.skspruce.ism.detect.webapi.strategy.controller;


import com.skspruce.ism.detect.webapi.strategy.repository.mysqlsecondary.ApRepository;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/ias")
public class ApQueryController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApRepository apRepository;

    @ApiOperation(value = "Query by device(Fuzzy query)", notes = "Query by device")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mac", value = "device mac", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "apName", value = "device name", required = false, dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/apQuery", method = RequestMethod.GET)
    public List<Map<String, Object>> apQuery(String mac, String apName) {
        List<Object[]> objectQuery = null;

        if ((mac != null && !"".equals(mac)) && (apName != null && !"".equals(apName))) {
            try {
                objectQuery = apRepository.findByMacAndApName(mac, apName);
            } catch (Exception e) {
                logger.error("Query Exception!", e);
            }
        } else if ((mac != null && !"".equals(mac)) && (apName == null || "".equals(apName))) {
            try {
                objectQuery = apRepository.findByMac(mac);
            } catch (Exception e) {
                logger.error("Query Exception!", e);
            }
        } else if ((apName != null && !"".equals(apName)) && (mac == null || "".equals(mac))) {
            try {
                objectQuery = apRepository.findByApName(apName);
            } catch (Exception e) {
                logger.error("Query Exception!", e);
            }
        }
        if (objectQuery != null && !"[]".equals(objectQuery.toString())) {
            System.out.println("objectQuery size is " + objectQuery);
            return setKey(objectQuery);
        }
        return null;
    }


    @ApiOperation(value = "Query by place(Fuzzy query)", notes = "Query by place")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address", value = "address", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "no", value = "NO of place", required = false, dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/placeQuery", method = RequestMethod.GET)
    public List<Map<String, Object>> placeQuery(String address, String no) {
        List<Object[]> objectPlaceQuery = null;
        if ((address != null && !"".equals(address)) && (no != null && !"".equals(no))) {
            try {
                objectPlaceQuery = apRepository.findByAddressAndNo(address, no);
            } catch (Exception e) {
                logger.error("Query Exception!", e);
            }
        } else if ((address != null && !"".equals(address)) && (no == null || "".equals(no))) {
            try {
                objectPlaceQuery = apRepository.findByAddress(address);
            } catch (Exception e) {
                logger.error("Query Exception!", e);
            }
        } else if ((no != null && !"".equals(no)) && (address == null || "".equals(address))) {
            try {
                objectPlaceQuery = apRepository.findByNo(no);
            } catch (Exception e) {
                logger.error("Query Exception!", e);
            }
        }
        if (objectPlaceQuery != null && !"[]".equals(objectPlaceQuery.toString())) {
            return setKey(objectPlaceQuery);
        }
        return null;
    }

    public List<Map<String, Object>> setKey(List<Object[]> list) {
        String[] keyArr = {"apId", "mac", "apName", "remark", "areaId", "areaName", "location"};
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        for (Object[] objArr : list) {
            Map<String, Object> apMap = new HashMap<String, Object>();
            for (int i = 0; i < objArr.length; i++) {
                apMap.put(keyArr[i], objArr[i]);
            }
            mapList.add(apMap);
        }
        logger.info("The result is : " + mapList);
        return mapList;
    }

}
