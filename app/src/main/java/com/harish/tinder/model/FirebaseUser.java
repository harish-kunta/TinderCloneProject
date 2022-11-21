package com.harish.tinder.model;

public class FirebaseUser {
    public String name;
    public String email;
    public int dob;
    public boolean online;
    public String profileImageUrl;
    public String sex;
    public String profileImageUrlCompressed;
    public String uid;

    public FirebaseUser(String name, String email, int dob, boolean online, String profileImageUrl, String sex, String profileImageUrlCompressed, String uid) {
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

    public boolean getOnline() {
        return online;
    }

    public void setOnline(boolean online) {
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
