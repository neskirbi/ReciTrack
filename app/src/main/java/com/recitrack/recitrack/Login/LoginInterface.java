package com.recitrack.recitrack.Login;

import com.google.gson.JsonArray;
import com.recitrack.recitrack.BuildConfig;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LoginInterface {
    @Headers(BuildConfig.APP_KEY)
    @POST("api/Login")
    Call<JsonArray> Login(@Body JsonArray jsonArray);
}
