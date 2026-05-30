package com.tajim.urlshortener.activities;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tajim.urlshortener.R;
import com.tajim.urlshortener.api.ApiConfig;
import com.tajim.urlshortener.api.UrlApi;
import com.tajim.urlshortener.databinding.ActivityShowUrlBinding;
import com.tajim.urlshortener.utils.AppUtils;
import com.tajim.urlshortener.utils.SessionManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ShowUrlActivity extends AppCompatActivity {
        ActivityShowUrlBinding binding;
        String longUrl, shortCode;
        long clicks, id;
        SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setupLayout();
        initVariables();
        initClickListeners();
    }
    private void enableEdgeToEdge(){
        EdgeToEdge.enable(this);
    }
    private void setupLayout(){
        binding = ActivityShowUrlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void initVariables(){
        binding.progress.setVisibility(GONE);
        sessionManager = new SessionManager(this);
        longUrl = getIntent().getStringExtra("long_url");
        shortCode = getIntent().getStringExtra("short_code");
        clicks = getIntent().getLongExtra("clicks", 0);
        id = getIntent().getLongExtra("id", 0);
        binding.tvLongUrl.setText(longUrl);
        binding.tvShortUrl.setText(ApiConfig.PUBLIC_BASE+shortCode);
        binding.chipClicks.setText("Clicks: "+clicks);


    }
    private void initClickListeners(){
        binding.btnCopy.setOnClickListener(v->{
            AppUtils.copyToClipBoard(this, ApiConfig.PUBLIC_BASE+shortCode);
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
        });

        binding.btnDelete.setOnClickListener(v->{
            delete(id);
        });
    }
    private void delete(long id){
        UrlApi.destroy(sessionManager.getToken(), id, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(()->{
                    handleFailure(e);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(()->{
                    handleResponse(response);
                });
            }
        });
    }
    private void handleFailure(IOException e){
        binding.progress.setVisibility(GONE);
        Toast.makeText(ShowUrlActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    private void handleResponse(Response response){
        if (response.isSuccessful()){
            Toast.makeText(this, "URL Deleted", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();

        }else {
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }
        response.close();
    }

}