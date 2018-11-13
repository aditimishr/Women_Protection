package tech.com.women_protection.classes;

import java.io.Serializable;

public class LocationClass implements Serializable {
    private String common_id;
    private String complaint_no;
    private double latitude;
    private double longitude;
    private Boolean isSafeLocation;
    private String staus_location;

    public LocationClass() {

    }

    public String getStaus_location() {
        return staus_location;
    }

    public void setStaus_location(String staus_location) {
        this.staus_location = staus_location;
    }

    public LocationClass(String complaint_no, double latitude, double longitude, Boolean isSafeLocation) {
        this.complaint_no = complaint_no;
        this.latitude = latitude;

        this.longitude = longitude;
        this.isSafeLocation = isSafeLocation;
    }

    public String getCommon_id() {
        return common_id;
    }

    public void setCommon_id(String common_id) {
        this.common_id = common_id;
    }

    public String getComplaint_no() {
        return complaint_no;
    }

    public void setComplaint_no(String complaint_no) {
        this.complaint_no = complaint_no;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Boolean getSafeLocation() {
        return isSafeLocation;
    }

    public void setSafeLocation(Boolean safeLocation) {
        isSafeLocation = safeLocation;
    }
}
