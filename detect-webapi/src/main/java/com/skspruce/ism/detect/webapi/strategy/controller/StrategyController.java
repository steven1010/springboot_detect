package com.skspruce.ism.detect.webapi.strategy.controller;

import com.skspruce.ism.detect.webapi.strategy.mysqlprimaryentity.Strategy;
import com.skspruce.ism.detect.webapi.strategy.mysqlsecondaryentity.TbArea;
import com.skspruce.ism.detect.webapi.strategy.mysqlsecondaryentity.TbDpiAppInfo;
import com.skspruce.ism.detect.webapi.strategy.repository.mysqlprimary.StrategyJpaRepository;
import com.skspruce.ism.detect.webapi.strategy.repository.mysqlsecondary.TaJpaRepository;
import com.skspruce.ism.detect.webapi.strategy.repository.mysqlsecondary.TdapJpaRepository;
import com.skspruce.ism.detect.webapi.strategy.util.PageUtil;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 策略相关API controller
 */
@RestController
@RequestMapping("/strategy")
public class StrategyController {

    public static Logger logger = LoggerFactory.getLogger(StrategyController.class);

    @Autowired
    StrategyJpaRepository sjr;

    @Autowired
    TdapJpaRepository tdapJr;

    @Autowired
    TaJpaRepository taJr;

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
    public Map<String, Object> findById(@RequestBody Strategy strategy) {
        Map<String, Object> map = new HashMap<>();
        map.put(PageUtil.ERROR, null);
        map.put(PageUtil.TYPE, null);
        map.put(PageUtil.SUCCESS, true);
        try {
            Strategy res = sjr.findOne(strategy.getId());
            map.put(PageUtil.DATA, res);
        } catch (Exception e) {
            logger.error("findById error:", e);
            map.put(PageUtil.SUCCESS, false);
        }
        return map;
    }

    /**
     * 分页查询策略
     *
     * @param pageIndex 当前页码
     * @param pageSize  每页显示数量
     * @param sortField 排序字段
     * @param sortType  排序类型
     * @return {@code Page<Strategy>}
     */
    @RequestMapping(value = "/get_page", method = RequestMethod.GET)
    public Map<String, Object> getPage(@RequestParam(value = "pageIndex", defaultValue = "0") Integer pageIndex,
                                       @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                       @RequestParam(value = "sortType", defaultValue = "desc") String sortType) {
        Map<String, Object> map = new HashMap<>();
        map.put(PageUtil.ERROR, null);
        map.put(PageUtil.TYPE, null);
        map.put(PageUtil.SUCCESS, true);
        try {
            Sort sort = null;
            if (sortType.equals("desc")) {
                sort = new Sort(Sort.Direction.DESC, sortField);
            } else {
                sort = new Sort(Sort.Direction.ASC, sortField);
            }

            Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
            Page<Strategy> strategies = sjr.findAll(pageable);

            List<Long> areaIds = new ArrayList<>();

            List<Strategy> content = strategies.getContent();
            for (Strategy stra : content) {
                String ids = stra.getAreaIds();
                if (ids != null && !ids.trim().isEmpty()) {
                    String[] split = ids.split(",");
                    for (String areaId : split) {
                        areaIds.add(Long.valueOf(areaId));
                    }
                }
            }

            if (areaIds.size() > 0) {
                Long[] longs = new Long[areaIds.size()];
                areaIds.toArray(longs);
                List<TbArea> tbAreas = taJr.findByIds(longs);
                Map<String, String> tbAreaMap = new HashMap<>();
                for (TbArea area : tbAreas) {
                    tbAreaMap.put(String.valueOf(area.getId()), area.getName());
                }

                for (Strategy stra : content) {
                    String ids = stra.getAreaIds();
                    StringBuilder sb = new StringBuilder();
                    if (ids != null && !ids.trim().isEmpty()) {
                        String[] split = ids.split(",");
                        for (int i = 0; i < split.length; i++) {
                            if (i == (split.length - 1)) {
                                sb.append(tbAreaMap.get(split[i]));
                            } else {
                                sb.append(tbAreaMap.get(split[i])).append(",");
                            }
                        }
                        stra.setAreaNames(sb.toString());
                    }
                }
            }

            map.put(PageUtil.DATA, strategies.getContent());
            map.put(PageUtil.TOTAL_PAGE, strategies.getTotalPages());
            map.put(PageUtil.TOTAL, strategies.getTotalElements());
        } catch (Exception e) {
            logger.error("findPage error:", e);
            map.put(PageUtil.SUCCESS, false);
            map.put(PageUtil.ERROR, e.getMessage());
        }
        return map;
    }

    /**
     * 获取所有策略信息
     *
     * @return
     */
    @RequestMapping(value = "/get_all", method = RequestMethod.GET)
    public Map<String, Object> getAll() {
        Map<String, Object> map = new HashMap<>();
        map.put(PageUtil.ERROR, null);
        map.put(PageUtil.TYPE, null);
        map.put(PageUtil.SUCCESS, true);
        try {
            List<Strategy> all = sjr.findAll();
            map.put(PageUtil.DATA, all);
            map.put(PageUtil.TOTAL, all.size());
        } catch (Exception e) {
            logger.error("findPage error:", e);
            map.put(PageUtil.SUCCESS, false);
            map.put(PageUtil.ERROR, e.getMessage());
        }
        return map;
    }

    /**
     * 删除策略,可批量删除,多个id以','分割
     *
     * @param content POST JSON {"ids":"1,2,3"}
     * @return {@link ModelAndView}
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Map<String, Object> delete(@RequestBody List<Strategy> content) {
        /*JSONObject json = JSONObject.parseObject(ids);
        String[] allId = json.getString("ids").split(",");
        logger.info("ids=" + json.getString("ids"));
        Integer[] aryIds = new Integer[allId.length];
        for (int i = 0; i < allId.length; i++) {
            aryIds[i] = Integer.valueOf(allId[i]);
        }

        sjr.deleteByIds(aryIds);*/
        //sjr.delete(content);
        Map<String, Object> map = new HashMap<>();
        map.put(PageUtil.ERROR, null);
        map.put(PageUtil.TYPE, null);
        map.put(PageUtil.SUCCESS, true);
        try {
            sjr.deleteInBatch(content);
        } catch (Exception e) {
            logger.error("delete error:", e);
            map.put(PageUtil.SUCCESS, false);
            map.put(PageUtil.ERROR, e.getMessage());
        }

        return map;
    }

    /**
     * 更新或添加,如果ID不存在则新增,存在则添加
     *
     * @param strategy
     * @return 当前操作对象 {@link Strategy}
     */
    @RequestMapping(value = "/upsert", method = RequestMethod.POST)
    public Map<String, Object> upsert(@RequestBody Strategy strategy) {
        Map<String, Object> map = new HashMap<>();
        map.put(PageUtil.ERROR, null);
        map.put(PageUtil.TYPE, null);
        map.put(PageUtil.SUCCESS, true);
        try {
            if (strategy.getId() == null || strategy.getId() == 0) {
                strategy.setAddTime(System.currentTimeMillis());
            }
            Strategy save = sjr.save(strategy);
            map.put(PageUtil.DATA, save);
        } catch (Exception e) {
            logger.error("upsert error,", e);
            map.put(PageUtil.SUCCESS, false);
            map.put(PageUtil.ERROR, e.getMessage());
        }
        return map;
    }

    /**
     * 获取所有虚拟帐号类型信息
     *
     * @return
     */
    @RequestMapping(value = "/get_vat", method = RequestMethod.GET)
    public Map<String, Object> getVirtualAccountType() {
        Map<String, Object> map = new HashMap<>();
        map.put(PageUtil.ERROR, null);
        map.put(PageUtil.TYPE, null);
        map.put(PageUtil.SUCCESS, true);
        try {
            List<TbDpiAppInfo> all = tdapJr.findAll();
            map.put(PageUtil.DATA, all);
            map.put(PageUtil.TOTAL, all.size());
        } catch (Exception e) {
            logger.error("getVirtualAccountType error:", e);
            map.put(PageUtil.SUCCESS, false);
            map.put(PageUtil.ERROR, e.getMessage());
        }
        return map;
    }
}
