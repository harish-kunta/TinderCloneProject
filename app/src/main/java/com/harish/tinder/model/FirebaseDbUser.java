package com.harish.tinder.model;

public class FirebaseDbUser {
    public String name;
    public String email;
    public int dob;
    public String online;
    public String profileImageUrl;
    public String sex;
    public String profileImageUrlCompressed;
    public String uid;

    public boolean isTermsAgreed() {
        return termsAgreed;
    }

    public void setTermsAgreed(boolean termsAgreed) {
        this.termsAgreed = termsAgreed;
    }

    public boolean termsAgreed;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String deviceToken;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String status;

    public FirebaseDbUser() {

    }

    public FirebaseDbUser(String name, String email, int dob, String online, String profileImageUrl, String sex, String profileImageUrlCompressed, String uid) {
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.online = online;
        this.profileImageUrl = profileImageUrl;
        this.sex = sex;
        this.profileImageUrlCompressed = profileImageUrlCompressed;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDob() {
        return dob;
    }

    public void setDob(int dob) {
        this.dob = dob;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getProfileImageUrlCompressed() {
        return profileImageUrlCompressed;
    }

    public void setProfileImageUrlCompressed(String profileImageUrlCompressed) {
        this.profileImageUrlCompressed = profileImageUrlCompressed;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
