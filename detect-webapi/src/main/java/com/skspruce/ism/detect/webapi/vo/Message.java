package com.skspruce.ism.detect.webapi.vo;

public class Message implements java.io.Serializable{
  private String zh_CN;
  private String en;
  public String getZh_CN() {
    return zh_CN;
  }
  public void setZh_CN(String zh_CN) {
    this.zh_CN = zh_CN;
  }
  public String getEn() {
    return en;
  }
  public void setEn(String en) {
    this.en = en;
  }
  public Message(String zh_CN, String en) {
    super();
    this.zh_CN = zh_CN;
    this.en = en;
  }
  public Message(String zh_CN) {
    super();
    this.zh_CN = zh_CN;
    this.en = zh_CN;
  }
  public Message() {
    super();
  }
  
  
}
