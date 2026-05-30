package com.tajim.urlshortener.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tajim.urlshortener.adapters.UrlAdapter;
import com.tajim.urlshortener.api.UrlApi;
import com.tajim.urlshortener.auth.LoginActivity;
import com.tajim.urlshortener.databinding.ActivityMainBinding;
import com.tajim.urlshortener.models.ShortUrl;
import com.tajim.urlshortener.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    SessionManager sessionManager;
    UrlAdapter urlAdapter;
    List<ShortUrl> urlList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupEdgeToEdge();
        setupLayout();
        initVariables();
        checkIfLoggedIn();
        setupClickListeners();
        fetchUrls();



    }
    private void setupEdgeToEdge(){
        EdgeToEdge.enable(this);
    }
    private void setupLayout(){
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void initVariables(){
        sessionManager = new SessionManager(this);
        urlList = new ArrayList<>();
        urlAdapter = new UrlAdapter(urlList);
        binding.recyclerView.setAdapter(urlAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void checkIfLoggedIn(){

        if(!sessionManager.isLoggedIn()){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
    private void setupClickListeners(){

    }
    private void fetchUrls(){
        UrlApi.index(sessionManager.getToken(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                Log.d("MainActivity", "onResponse: "+body);
                try {
                    parseUrls(body);
                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "Parse error", Toast.LENGTH_SHORT).show()
                    );
                }


            }
        });
    }

    private void parseUrls(String body) throws Exception{
        JSONObject jsonObject = new JSONObject(body);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        List<ShortUrl> newList = new ArrayList<>();
        for (int i = 0 ; i < jsonArray.length(); i ++){
            JSONObject object = jsonArray.getJSONObject(i);
            ShortUrl shortUrl = new ShortUrl(
                    object.getLong("id"),
                    object.getString("long_url"),
                    object.getString("short_code"),
                    object.getLong("clicks")
            );
            newList.add(shortUrl);

        }
        runOnUiThread(() -> setUrls(newList));


    }
    private void setUrls(List<ShortUrl> newList){
        urlList.clear();
        urlList.addAll(newList);
        urlAdapter.notifyDataSetChanged();
        Log.d("MainActivity", newList.toString());
    }
}