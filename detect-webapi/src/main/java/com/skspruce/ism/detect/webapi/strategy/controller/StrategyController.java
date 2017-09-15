package com.skspruce.ism.detect.webapi.strategy.controller;

import com.alibaba.fastjson.JSONObject;
import com.skspruce.ism.detect.webapi.strategy.entity.Strategy;
import com.skspruce.ism.detect.webapi.strategy.repo.StrategyJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * 策略相关API controller
 */
@RestController
@RequestMapping("/strategy")
public class StrategyController {

    public static Logger logger = LoggerFactory.getLogger(StrategyController.class);

    @Autowired
    StrategyJpaRepository sjr;

    public static void main(String[] args) {
        SpringApplication.run(StrategyController.class, args);
    }

    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public Strategy findById(@RequestBody Strategy strategy) {
        logger.info("id=" + strategy.getId());
        Strategy res = sjr.findStrategyById(strategy.getId());
        return res;
    }

    @RequestMapping("/get")
    public Page<Strategy> findPage(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                   @RequestParam(value = "size", defaultValue = "5") Integer size,
                                   @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                   @RequestParam(value = "sortType", defaultValue = "desc") String sortType) {
        Sort sort = null;
        if (sortType.equals("desc")) {
            sort = new Sort(Sort.Direction.DESC, sortField);
        } else {
            sort = new Sort(Sort.Direction.ASC, sortField);
        }

        Pageable pageable = new PageRequest(page, size, sort);
        Page<Strategy> strategies = sjr.findAll(pageable);
        return strategies;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ModelAndView deleteByIds(@RequestBody String ids) {
        JSONObject json = JSONObject.parseObject(ids);
        String[] allId = json.getString("ids").split(",");
        logger.info("ids=" + json.getString("ids"));
        Integer[] aryIds = new Integer[allId.length];
        for(int i=0;i<allId.length;i++){
            aryIds[i] = Integer.valueOf(allId[i]);
        }

        sjr.deleteByIds(aryIds);

        return new ModelAndView("/strategy/get_page");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Strategy update(@RequestBody Strategy strategy) {
        Strategy save = sjr.save(strategy);
        return save;
    }
}
