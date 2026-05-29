package com.tajim.urlshortener.auth;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tajim.urlshortener.R;
import com.tajim.urlshortener.activities.MainActivity;
import com.tajim.urlshortener.api.AuthApi;
import com.tajim.urlshortener.databinding.ActivityLoginBinding;
import com.tajim.urlshortener.databinding.ActivityMainBinding;
import com.tajim.urlshortener.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    SessionManager sessionManager;
    String deviceName;


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
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void initVariables(){
        sessionManager = new SessionManager(this);
         deviceName = Build.MANUFACTURER + " " + Build.MODEL;
    }
    private void setupClickListeners(){
        binding.btnLogin.setOnClickListener(v->{
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            login(email, password);

        });
        binding.tvRegister.setOnClickListener(v->{
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
    private void setLoading(boolean loading) {

        binding.btnLogin.setEnabled(!loading);

        binding.btnLogin.setText(loading ? "Loading..." : "Register");
    }
    private void login(String email, String password){
        setLoading(true);

        AuthApi.login(email, password, deviceName, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {

                    setLoading(false);

                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                runOnUiThread(() -> {

                    setLoading(false);

                    try {

                        JSONObject json = new JSONObject(body);

                        if (response.isSuccessful()) {

                            String token =
                                    json.getString("token");

                            sessionManager.saveToken(token);

                            startActivity(
                                    new Intent(
                                            LoginActivity.this,
                                            MainActivity.class
                                    )
                            );

                            finish();

                        } else {

                            Toast.makeText(LoginActivity.this, json.optString("message", "Login failed"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();

                        Toast.makeText(
                                LoginActivity.this,
                                "JSON error",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

            }
        });

    }
}