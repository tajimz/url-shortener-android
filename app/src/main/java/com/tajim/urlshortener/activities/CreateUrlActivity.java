package com.tajim.urlshortener.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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

import com.tajim.urlshortener.R;
import com.tajim.urlshortener.api.ApiConfig;
import com.tajim.urlshortener.api.UrlApi;
import com.tajim.urlshortener.databinding.ActivityCreateUrlBinding;
import com.tajim.urlshortener.utils.AppUtils;
import com.tajim.urlshortener.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateUrlActivity extends AppCompatActivity {
    ActivityCreateUrlBinding binding;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupEdgeToEdge();
        setupLayout();
        initVariables();
        setupClickListeners();

    }
    private void setupEdgeToEdge(){
        EdgeToEdge.enable(this);
    }
    private void setupLayout(){
        binding = ActivityCreateUrlBinding.inflate(getLayoutInflater());
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
    }
    private void setupClickListeners(){
        binding.btnCreate.setOnClickListener(v->{
            String longUrl = binding.etLongUrl.getText().toString().trim();
            String shortCode = binding.etShortCode.getText().toString().trim();
            if (longUrl.isEmpty()) {
                Toast.makeText(this, "Long URL is required", Toast.LENGTH_SHORT).show();
                return;
            }
            storeUrl(longUrl,shortCode);

        });
    }
    private void storeUrl(String longUrl, String shortCode){
        binding.progress.setVisibility(VISIBLE);
        UrlApi.store(sessionManager.getToken(), longUrl, shortCode, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    binding.progress.setVisibility(GONE);
                    Toast.makeText(CreateUrlActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "";

                runOnUiThread(() -> {
                    binding.progress.setVisibility(GONE);

                    if (!response.isSuccessful()) {
                        handleError(body);
                    } else {
                        handleSuccess(body);
                    }
                });
            }
        });
    }

    private void handleSuccess(String body) {
        try {
            JSONObject json = new JSONObject(body);
            JSONObject data = json.getJSONObject("data");

            String shortCode = data.getString("short_code");
            String shortUrl = ApiConfig.PUBLIC_BASE + shortCode;

            AppUtils.copyToClipBoard(this, shortUrl);

            Toast.makeText(this, "URL Created", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Invalid server response", Toast.LENGTH_SHORT).show();
        }
    }
    private void handleError(String body) {
        try {
            JSONObject json = new JSONObject(body);
            String message = json.optString("message", "Request failed");

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show();
        }
    }

}