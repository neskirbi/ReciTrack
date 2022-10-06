package com.recitrack.recitrack.Principal;

import com.google.gson.JsonArray;
import com.recitrack.recitrack.BuildConfig;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PrincipalInterface {

    @Headers(BuildConfig.APP_KEY)
    @POST("api/Remisiones")
    Call<JsonArray> Remisiones(@Body JsonArray jsonArray);

    @Headers(BuildConfig.APP_KEY)
    @POST("api/GetObras")
    Call<JsonArray> GetObras(@Body JsonArray jsonArray);
}
