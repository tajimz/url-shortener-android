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

}
