package com.tajim.urlshortener.models;

public class ShortUrl {
    public String long_url;
    public String short_code;
    public long id;
    public long clicks;
    public ShortUrl(long id, String long_url, String short_code, long clicks){
        this.long_url = long_url;
        this.short_code = short_code;
        this.id = id;
        this.clicks = clicks;
    }
}
