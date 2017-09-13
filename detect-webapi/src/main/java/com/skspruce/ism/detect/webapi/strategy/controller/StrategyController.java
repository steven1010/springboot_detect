package com.skspruce.ism.detect.webapi.strategy.controller;

import com.skspruce.ism.detect.webapi.strategy.entity.Strategy;
import com.skspruce.ism.detect.webapi.strategy.repo.StrategyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 策略相关API controller
 */
@RestController
@RequestMapping("/strategy")
public class StrategyController {

    public static Logger logger = LoggerFactory.getLogger(StrategyController.class);

    @Autowired
    StrategyRepository sr;

    public static void main(String[] args) {
        SpringApplication.run(StrategyController.class, args);
    }

    @RequestMapping("/find_id")
    public Strategy findById(Integer id) {
        Strategy strategy = sr.findStrategyById(id);
        return strategy;
    }

    @RequestMapping("/get_page")
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
        Page<Strategy> strategies = sr.findAll(pageable);
        return strategies;
    }

    @RequestMapping("/delete")
    public ModelAndView deleteByIds(String ids){
        sr.deleteByIds(ids);

        return new ModelAndView("/strategy/get_page");
    }
}
