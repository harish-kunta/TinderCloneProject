package com.harish.tinder.model;

import com.google.gson.annotations.SerializedName;

public class Profile {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public Profile(String id, String name, int age, String profile_pic, int distance) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.profile_pic = profile_pic;
        this.distance=distance;
    }

    @SerializedName("user_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("age")
    private int age;
    @SerializedName("profileImageUrl")
    private String profile_pic;
    @SerializedName("distance")
    private int distance;
}


