package com.skspruce.ism.detect.webapi.strategy.mysqlsecondaryentity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_dpi_app_info")
public class TbDpiAppInfo {

    @Id
    private String appId;
    private String nameZh;
    private String nameEn;

    public TbDpiAppInfo() {

    }

    public TbDpiAppInfo(String appId, String nameZh, String nameEn) {
        this.appId = appId;
        this.nameZh = nameZh;
        this.nameEn = nameEn;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }
}
