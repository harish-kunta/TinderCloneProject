package com.harish.tinder.model;

public class AvailableProfile {
    Profile profile;
    String text;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public AvailableProfile(Profile profile, String text) {
        this.profile = profile;
        this.text = text;
    }
}
