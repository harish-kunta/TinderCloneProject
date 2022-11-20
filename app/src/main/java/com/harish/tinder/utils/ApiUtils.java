package com.harish.tinder.utils;


public class ApiUtils {


    public static String BASEURL = "https://fcm.googleapis.com/" ;



    //http://153.77.204.1:5001/flms/



    public static FirebaseApi sendNotificationService() {
        return FirebaseClient.getClient(BASEURL).create(FirebaseApi.class);
    }


}