package com.harish.tinder.model;

import com.google.gson.annotations.SerializedName;

public class Connections {
    @SerializedName("matches")
    public Matches matches;

    @SerializedName("yeps")
    public Yeps yeps;
}
