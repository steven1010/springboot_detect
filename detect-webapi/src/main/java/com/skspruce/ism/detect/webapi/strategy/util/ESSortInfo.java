package com.skspruce.ism.detect.webapi.strategy.util;

/**
 * 排序信息
 */
public class ESSortInfo {

    private String direction = "DESC";

    private String property;

    private boolean ignoreCase = false;

    private String nullHandling = "NATIVE";

    private boolean assending = false;

    private boolean descending = false;

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public String getNullHandling() {
        return nullHandling;
    }

    public void setNullHandling(String nullHandling) {
        this.nullHandling = nullHandling;
    }

    public boolean isAssending() {
        return assending;
    }

    public void setAssending(boolean assending) {
        this.assending = assending;
    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }
}
