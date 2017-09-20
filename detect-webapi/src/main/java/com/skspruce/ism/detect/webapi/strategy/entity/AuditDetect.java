package com.skspruce.ism.detect.webapi.strategy.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document(collection="AuditDetect")
public class AuditDetect implements Serializable {

    @Id
    private String _id;
    @Field("Time")
    private String time;
    @Field("ApMacString")
    private String apMacString;
    @Field("UserMacString")
    private String userMacString;
    @Field("PlaceCode")
    private long placeCode;
    @Field("PlaceName")
    private String placeName;
    @Field("Location")
    private String location;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getApMacString() {
        return apMacString;
    }

    public void setApMacString(String apMacString) {
        this.apMacString = apMacString;
    }

    public String getUserMacString() {
        return userMacString;
    }

    public void setUserMacString(String userMacString) {
        this.userMacString = userMacString;
    }

    public long getPlaceCode() {
        return placeCode;
    }

    public void setPlaceCode(long placeCode) {
        this.placeCode = placeCode;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
