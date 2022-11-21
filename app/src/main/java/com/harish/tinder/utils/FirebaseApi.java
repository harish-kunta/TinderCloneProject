package com.harish.tinder.utils;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FirebaseApi {
    @Headers({"Authorization: key=AAAA9KpjWmA:APA91bEo2CiJ8hq5pTZaDnPyipkaVA9Jj4NqWWN-Dld3dtPTx20FE5uVgLc8DekukP9gUuTVcAOF2_n8z8kDcL26x-8L04ciP6Df9Y0x2qixoJMstGB5OL9toLcdW9s_ARLnlvzkdj6s",
            "Content-Type:application/json"})
    @POST("fcm/send")
    Call<FirebaseMessage> sendMessage(@Body FirebaseMessage message);
}
