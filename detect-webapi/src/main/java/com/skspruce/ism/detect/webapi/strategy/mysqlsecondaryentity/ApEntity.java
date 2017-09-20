package com.skspruce.ism.detect.webapi.strategy.mysqlsecondaryentity;

import javax.persistence.*;

@Entity
@Table(name = "tb_ap")
public class ApEntity {
  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  @Column(name="id")
  private Long id;
  @Column(name="top_id")
  private Long top_id;
  private String mac;
  private String remark;
  private java.sql.Timestamp create_date;
  private Long create_user;
  private String location;
  private Long floor;
  private Long maintain_status;
  private java.sql.Timestamp last_report_time;
  private String ap_name;
  private Long cluster_id;
  private Long id_in_cluster;
  private Long id_in_center;
  private Long device_type;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTop_id() {
    return top_id;
  }

  public void setTop_id(Long top_id) {
    this.top_id = top_id;
  }
  @Column(name="mac")
  public String getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }
  @Column(name="remark")
  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }
  @Column(name="create_date")
  public java.sql.Timestamp getCreate_date() {
    return create_date;
  }

  public void setCreate_date(java.sql.Timestamp create_date) {
    this.create_date = create_date;
  }
  @Column(name="create_user")
  public Long getCreate_user() {
    return create_user;
  }

  public void setCreate_user(Long create_user) {
    this.create_user = create_user;
  }
  @Column(name="location")
  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
  @Column(name="floor")
  public Long getFloor() {
    return floor;
  }

  public void setFloor(Long floor) {
    this.floor = floor;
  }
  @Column(name="maintain_status")
  public Long getMaintain_status() {
    return maintain_status;
  }

  public void setMaintain_status(Long maintain_status) {
    this.maintain_status = maintain_status;
  }
  @Column(name="last_report_time")
  public java.sql.Timestamp getLast_report_time() {
    return last_report_time;
  }

  public void setLast_report_time(java.sql.Timestamp last_report_time) {
    this.last_report_time = last_report_time;
  }
  @Column(name="ap_name")
  public String getAp_name() {
    return ap_name;
  }

  public void setAp_name(String ap_name) {
    this.ap_name = ap_name;
  }
  @Column(name="cluster_id")
  public Long getCluster_id() {
    return cluster_id;
  }

  public void setCluster_id(Long cluster_id) {
    this.cluster_id = cluster_id;
  }
  @Column(name="id_in_cluster")
  public Long getId_in_cluster() {
    return id_in_cluster;
  }

  public void setId_in_cluster(Long id_in_cluster) {
    this.id_in_cluster = id_in_cluster;
  }
  @Column(name="id_in_center")
  public Long getId_in_center() {
    return id_in_center;
  }

  public void setId_in_center(Long id_in_center) {
    this.id_in_center = id_in_center;
  }
  @Column(name="device_type")
  public Long getDevice_type() {
    return device_type;
  }

  public void setDevice_type(Long device_type) {
    this.device_type = device_type;
  }


  @Override
  public String toString() {
    return "ApEntity{" +
            "id=" + id +
            ", top_id=" + top_id +
            ", mac='" + mac + '\'' +
            ", remark='" + remark + '\'' +
            ", create_date=" + create_date +
            ", create_user=" + create_user +
            ", location='" + location + '\'' +
            ", floor=" + floor +
            ", maintain_status=" + maintain_status +
            ", last_report_time=" + last_report_time +
            ", ap_name='" + ap_name + '\'' +
            ", cluster_id=" + cluster_id +
            ", id_in_cluster=" + id_in_cluster +
            ", id_in_center=" + id_in_center +
            ", device_type=" + device_type +
            '}';
  }
}
