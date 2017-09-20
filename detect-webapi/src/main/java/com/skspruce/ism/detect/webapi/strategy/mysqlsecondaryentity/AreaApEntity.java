package com.skspruce.ism.detect.webapi.strategy.mysqlsecondaryentity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb_area_ap")
public class AreaApEntity {

  private Long area_id;
  private Long ap_id;

  @OneToMany(mappedBy = "areaApEntity",cascade={CascadeType.PERSIST,CascadeType.REMOVE},fetch = FetchType.EAGER)
  private List<ApEntity> apEntityList;



  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  @Column(name="area_id")
  public Long getArea_id() {
    return area_id;
  }

  public void setArea_id(Long area_id) {
    this.area_id = area_id;
  }
  @Column(name="ap_id")
  public Long getAp_id() {
    return ap_id;
  }

  public void setAp_id(Long ap_id) {
    this.ap_id = ap_id;
  }
}
