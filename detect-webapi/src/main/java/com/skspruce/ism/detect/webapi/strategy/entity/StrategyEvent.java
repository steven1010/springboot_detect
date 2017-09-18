package com.skspruce.ism.detect.webapi.strategy.entity;

public class StrategyEvent {
    private String id;
    private String userMac;
    private Long beginTime;
    private Integer areaId;
    private String areaName;
    private Integer strategyId;
    private String strategyName;
    private Integer status;

    private Long endTime;
    private Long handleTime;

    public StrategyEvent() {
    }

    public StrategyEvent(String id, String userMac, Long beginTime, Integer areaId, String areaName, Integer strategyId, Integer status) {
        this.id = id;
        this.userMac = userMac;
        this.beginTime = beginTime;
        this.areaId = areaId;
        this.areaName = areaName;
        this.strategyId = strategyId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserMac() {
        return userMac;
    }

    public void setUserMac(String userMac) {
        this.userMac = userMac;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Integer strategyId) {
        this.strategyId = strategyId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Long handleTime) {
        this.handleTime = handleTime;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }
}
