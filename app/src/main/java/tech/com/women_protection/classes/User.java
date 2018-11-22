package tech.com.women_protection.classes;

import java.util.Date;

public class User {
    private String user_id;
    private String name;
    private String email_id;
    private String Phone_Number;
    private String Password;
    private String register_date;
    private Boolean user_isActive;
    private String device_id;
    private String user_type;
    private String identity;
    private Boolean person_isActive;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private String gender;


    public String getRegister_date() {
        return register_date;
    }

    public void setRegister_date(String register_date) {
        this.register_date = register_date;
    }


    public String getPhone_Number() {
        return Phone_Number;
    }

    public void setPhone_Number(String phone_Number) {
        Phone_Number = phone_Number;
    }


    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;

    }

    public User() {
    }

    public User(String user_id, String name, String email_id, int Phone_Number, String Password, Date register_date, Boolean user_isActive, Boolean person_isActive, int device_id, String user_type) {
        this.user_id = user_id;
        this.name = name;
        this.email_id = email_id;
        this.Password = Password;
        this.user_isActive = user_isActive;
        this.person_isActive = person_isActive;
        this.user_type = user_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }


    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public Boolean getUser_isActive() {
        return user_isActive;
    }

    public void setUser_isActive(Boolean user_isActive) {
        this.user_isActive = user_isActive;
    }

    public Boolean getPerson_isActive() {
        return person_isActive;
    }

    public void setPerson_isActive(Boolean person_isActive) {
        this.person_isActive = person_isActive;
    }
}
