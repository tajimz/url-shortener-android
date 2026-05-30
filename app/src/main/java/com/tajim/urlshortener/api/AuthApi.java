package com.tajim.urlshortener.api;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AuthApi {
    public static void login(String email, String password,String deviceName, Callback callback) {

        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .add("device_name", deviceName)
                .build();

        Request request = ApiRequest.guest()
                .url(ApiConfig.BASE_URL + "/login")
                .post(body)
                .build();

        ApiClient.getClient()
                .newCall(request)
                .enqueue(callback);
    }

    public static void register(String name,String email,String password, String deviceName, Callback callback){
        RequestBody body = new FormBody.Builder()
                .add("name", name)
                .add("email", email)
                .add("password", password)
                .add("device_name", deviceName)
                .build();

        Request request = ApiRequest.guest()
                .url(ApiConfig.BASE_URL + "/register")
                .post(body)
                .build();

        ApiClient.getClient()
                .newCall(request)
                .enqueue(callback);
    }
}