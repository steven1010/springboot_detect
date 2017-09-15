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

import java.util.List;

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

    /**
     * 根据ID查询
     *
     * @param strategy 封装JSON数据对象
     * @return {@link Strategy}
     */
    @RequestMapping(value = "/find_id", method = RequestMethod.POST)
    public Strategy findById(@RequestBody Strategy strategy) {
        logger.info("id=" + strategy.getId());
        Strategy res = sjr.findOne(strategy.getId());
        return res;
    }

    /**
     * 分页查询策略
     *
     * @param number    当前页码
     * @param size      每页显示数量
     * @param sortField 排序字段
     * @param sortType  排序类型
     * @return {@code Page<Strategy>}
     */
    @RequestMapping("/get_page")
    public Page<Strategy> findPage(@RequestParam(value = "number", defaultValue = "0") Integer number,
                                   @RequestParam(value = "size", defaultValue = "5") Integer size,
                                   @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                   @RequestParam(value = "sortType", defaultValue = "desc") String sortType) {
        Sort sort = null;
        if (sortType.equals("desc")) {
            sort = new Sort(Sort.Direction.DESC, sortField);
        } else {
            sort = new Sort(Sort.Direction.ASC, sortField);
        }

        Pageable pageable = new PageRequest(number, size, sort);
        Page<Strategy> strategies = sjr.findAll(pageable);
        return strategies;
    }

    /**
     * 删除策略,可批量删除,多个id以','分割
     *
     * @param content POST JSON {"ids":"1,2,3"}
     * @return {@link ModelAndView}
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ModelAndView delete(@RequestBody List<Strategy> content) {
        /*JSONObject json = JSONObject.parseObject(ids);
        String[] allId = json.getString("ids").split(",");
        logger.info("ids=" + json.getString("ids"));
        Integer[] aryIds = new Integer[allId.length];
        for (int i = 0; i < allId.length; i++) {
            aryIds[i] = Integer.valueOf(allId[i]);
        }

        sjr.deleteByIds(aryIds);*/
        //sjr.delete(content);
        sjr.deleteInBatch(content);

        return new ModelAndView("/strategy/get_page");
    }

    /**
     * 更新或添加,如果ID不存在则新增,存在则添加
     *
     * @param strategy
     * @return 当前操作对象 {@link Strategy}
     */
    @RequestMapping(value = "/upsert", method = RequestMethod.POST)
    public Strategy update(@RequestBody Strategy strategy) {
        Strategy save = sjr.save(strategy);
        return save;
    }
}
