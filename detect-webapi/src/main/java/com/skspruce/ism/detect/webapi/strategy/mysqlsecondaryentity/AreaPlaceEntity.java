package com.skspruce.ism.detect.webapi.strategy.mysqlsecondaryentity;

import javax.persistence.*;

@Entity
@Table(name = "tb_area_place")
public class AreaPlaceEntity {
  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  @Column(name="id")
  private Long id;
  private String no;
  private String active;
  private String open_start;
  private String open_end;
  private String address;
  private String location;
  private String owner_name;
  private String owner_contact;
  private Long owner_cert;
  private String owner_cert_number;
  private Long service_type;
  private Long business_type;
  private String network_account;
  private String network_ip;
  private Long network_provider;
  private Long network_type;
  private Long user_auth;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNo() {
    return no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  public String getActive() {
    return active;
  }

  public void setActive(String active) {
    this.active = active;
  }

  public String getOpen_start() {
    return open_start;
  }

  public void setOpen_start(String open_start) {
    this.open_start = open_start;
  }

  public String getOpen_end() {
    return open_end;
  }

  public void setOpen_end(String open_end) {
    this.open_end = open_end;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getOwner_name() {
    return owner_name;
  }

  public void setOwner_name(String owner_name) {
    this.owner_name = owner_name;
  }

  public String getOwner_contact() {
    return owner_contact;
  }

  public void setOwner_contact(String owner_contact) {
    this.owner_contact = owner_contact;
  }

  public Long getOwner_cert() {
    return owner_cert;
  }

  public void setOwner_cert(Long owner_cert) {
    this.owner_cert = owner_cert;
  }

  public String getOwner_cert_number() {
    return owner_cert_number;
  }

  public void setOwner_cert_number(String owner_cert_number) {
    this.owner_cert_number = owner_cert_number;
  }

  public Long getService_type() {
    return service_type;
  }

  public void setService_type(Long service_type) {
    this.service_type = service_type;
  }

  public Long getBusiness_type() {
    return business_type;
  }

  public void setBusiness_type(Long business_type) {
    this.business_type = business_type;
  }

  public String getNetwork_account() {
    return network_account;
  }

  public void setNetwork_account(String network_account) {
    this.network_account = network_account;
  }

  public String getNetwork_ip() {
    return network_ip;
  }

  public void setNetwork_ip(String network_ip) {
    this.network_ip = network_ip;
  }

  public Long getNetwork_provider() {
    return network_provider;
  }

  public void setNetwork_provider(Long network_provider) {
    this.network_provider = network_provider;
  }

  public Long getNetwork_type() {
    return network_type;
  }

  public void setNetwork_type(Long network_type) {
    this.network_type = network_type;
  }

  public Long getUser_auth() {
    return user_auth;
  }

  public void setUser_auth(Long user_auth) {
    this.user_auth = user_auth;
  }
}
