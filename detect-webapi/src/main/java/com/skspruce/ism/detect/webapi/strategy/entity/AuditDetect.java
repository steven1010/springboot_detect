package com.skspruce.ism.detect.webapi.strategy.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document(collection="AuditDetect")
public class AuditDetect implements Serializable {

    @Id
    private String _id;
    private String Time;
    private String ApMacString;
    private String UserMacString;
    private long PlaceCode;
    private String PlaceName;
    private String Location;

    public String getApMacString() {
        return ApMacString;
    }

    public void setApMacString(String apMacString) {
        ApMacString = apMacString;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getUserMacString() {
        return UserMacString;
    }

    public void setUserMacString(String userMacString) {
        UserMacString = userMacString;
    }

    public long getPlaceCode() {
        return PlaceCode;
    }

    public void setPlaceCode(long placeCode) {
        PlaceCode = placeCode;
    }

    public String getPlaceName() {
        return PlaceName;
    }

    public void setPlaceName(String placeName) {
        PlaceName = placeName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }
}
