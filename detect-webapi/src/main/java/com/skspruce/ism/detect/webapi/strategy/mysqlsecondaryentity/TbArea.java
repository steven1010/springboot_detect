package com.skspruce.ism.detect.webapi.strategy.mysqlsecondaryentity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_area")
public class TbArea {
    @Id
    private Long id;
    private String name;

    public TbArea() {
    }

    public TbArea(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
