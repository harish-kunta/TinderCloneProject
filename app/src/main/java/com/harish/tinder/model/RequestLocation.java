package com.harish.tinder.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestLocation implements Parcelable {
    private  double lat;
    private double lon;

    protected RequestLocation(Parcel in) {
        lat = in.readDouble();
        lon = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lon);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RequestLocation> CREATOR = new Creator<RequestLocation>() {
        @Override
        public RequestLocation createFromParcel(Parcel in) {
            return new RequestLocation(in);
        }

        @Override
        public RequestLocation[] newArray(int size) {
            return new RequestLocation[size];
        }
    };

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public RequestLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public RequestLocation(){

    }

}
