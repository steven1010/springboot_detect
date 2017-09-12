package com.skspruce.ism.detect.spark.model;

import java.io.Serializable;

/**
 * 布控策略
 */
public class Strategy implements Serializable {
    private int id;
    private String name;
    private String mac;
    private int accountType;
    private String accountId;
    private String areaIds;
    private int reportType;
    private String reportTarget;
    private int reportLevel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
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

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public String getReportTarget() {
        return reportTarget;
    }

    public void setReportTarget(String reportTarget) {
        this.reportTarget = reportTarget;
    }

    public int getReportLevel() {
        return reportLevel;
    }

    public void setReportLevel(int reportLevel) {
        this.reportLevel = reportLevel;
    }
}
