package com.skspruce.ism.detect.spark.model;

import java.io.Serializable;

/**
 * 布控策略
 */
public class Strategy implements Serializable {
    private Integer id;
    private String name;
    private String mac;
    private String accountType;
    private String accountId;
    private String areaIds;
    private Integer reportType;
    private String reportTarget;
    private Integer reportLevel;

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

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
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
}
