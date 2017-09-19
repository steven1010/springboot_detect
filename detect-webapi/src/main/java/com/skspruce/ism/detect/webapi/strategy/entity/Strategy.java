package com.skspruce.ism.detect.webapi.strategy.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class Strategy {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private String mac;

    private Integer accountType;

    private String accountId;

    private String areaIds;

    private Integer reportType;

    private String reportTarget;

    private Integer reportLevel;

    private Long addTime;

    public Strategy() {
    }

    public Strategy(Integer id, String name, String mac, Integer accountType,
                    String accountId, String areaIds, Integer reportType,
                    String reportTarget, Integer reportLevel, Long addTime) {
        super();
        this.id = id;
        this.name = name;
        this.mac = mac;
        this.accountType = accountType;
        this.accountId = accountId;
        this.areaIds = areaIds;
        this.reportType = reportType;
        this.reportTarget = reportTarget;
        this.reportLevel = reportLevel;
        this.addTime = addTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(String areaIds) {
        this.areaIds = areaIds;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public String getReportTarget() {
        return reportTarget;
    }

    public void setReportTarget(String reportTarget) {
        this.reportTarget = reportTarget;
    }

    public Integer getReportLevel() {
        return reportLevel;
    }

    public void setReportLevel(Integer reportLevel) {
        this.reportLevel = reportLevel;
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }
}
