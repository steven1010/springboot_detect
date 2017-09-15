package com.skspruce.ism.detect.webapi.strategy.vo;

import com.skspruce.ism.fm.backend.RestConstants;

public class ResponseMessage implements java.io.Serializable{

  private String status;
  public ResponseMessage() {
    status=RestConstants.ReturnResponseMessageSuccess;
  }
  private Message message;
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }
  public Message getMessage() {
    return message;
  }
  public void setMessage(Message message) {
    this.message = message;
  }
}
