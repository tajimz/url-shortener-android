package com.tajim.urlshortener.api;

import okhttp3.Callback;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UrlApi {
    public static void index(String token, Callback callback){
        Request request = ApiRequest.authorized(token)
                .url(ApiConfig.BASE_URL+"/urls")
                .get()
                .build();

        ApiClient.getClient()
                .newCall(request)
                .enqueue(callback);
    }


    public static void store(String token, String longUrl, String shortCode, Callback callback){
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        bodyBuilder.add("long_url", longUrl);
        if (shortCode != null && !shortCode.isBlank()) bodyBuilder.add("short_code", shortCode); //short Code is Optional

        RequestBody body = bodyBuilder.build();

        Request request = ApiRequest.authorized(token)
                .url(ApiConfig.BASE_URL+"/urls")
                .post(body)
                .build();

        ApiClient.getClient()
                .newCall(request)
                .enqueue(callback);
    }

}
