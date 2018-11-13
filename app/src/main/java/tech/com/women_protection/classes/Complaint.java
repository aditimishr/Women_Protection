package tech.com.women_protection.classes;

import java.io.Serializable;

public class Complaint implements Serializable {
    private String complaint_no;
    private String handled_by_admin_name;
    private Boolean isEmergency;
    private String caseDescriptionText;
    private String registered_by_name;
    private String status;
    private String grievance_type;
    private String registered_by_user_type;
    private int device_id;

    public Complaint() {
    }

    public Complaint(String complaint_no, String handled_by_admin_name, Boolean isEmergency, String caseDescriptionText, String registered_by_name, String status, String grievance_type, int device_id) {
        this.complaint_no = complaint_no;
        this.handled_by_admin_name = handled_by_admin_name;
        this.isEmergency = isEmergency;
        this.caseDescriptionText = caseDescriptionText;
        this.registered_by_name = registered_by_name;
        this.status = status;
        this.grievance_type = grievance_type;
        this.device_id = device_id;
    }

    public String getRegistered_by_user_type() {
        return registered_by_user_type;
    }

    public void setRegistered_by_user_type(String registered_by_user_type) {
        this.registered_by_user_type = registered_by_user_type;
    }

    public String getComplaint_no() {
        return complaint_no;
    }

    public void setComplaint_no(String complaint_no) {
        this.complaint_no = complaint_no;
    }

    public String getHandled_by_admin_name() {
        return handled_by_admin_name;
    }

    public void setHandled_by_admin_name(String handled_by_admin_name) {
        this.handled_by_admin_name = handled_by_admin_name;
    }

    public Boolean getEmergency() {
        return isEmergency;
    }

    public void setEmergency(Boolean emergency) {
        isEmergency = emergency;
    }

    public String getCaseDescriptionText() {
        return caseDescriptionText;
    }

    public void setCaseDescriptionText(String caseDescriptionText) {
        this.caseDescriptionText = caseDescriptionText;
    }

    public String getRegistered_by_name() {
        return registered_by_name;
    }

    public void setRegistered_by_name(String registered_by_name) {
        this.registered_by_name = registered_by_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGrievance_type() {
        return grievance_type;
    }

    public void setGrievance_type(String grievance_type) {
        this.grievance_type = grievance_type;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }
}
