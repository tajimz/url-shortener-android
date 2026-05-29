package com.tajim.urlshortener.auth;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
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
import com.tajim.urlshortener.databinding.ActivityRegisterBinding;
import com.tajim.urlshortener.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
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
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
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
        binding.btnRegister.setOnClickListener(v->{
            String name = binding.etName.getText().toString();
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();
            if (email.isEmpty() || password.isEmpty() || name.isEmpty()){
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            register(name, email, password);

        });
        binding.tvLogin.setOnClickListener(v->{
            onBackPressed();
        });
    }
    private void setLoading(boolean loading) {

        binding.btnRegister.setEnabled(!loading);

        binding.btnRegister.setText(loading ? "Loading..." : "Login");
    }
    private void register(String name,String email,String password){
        setLoading(true);

        AuthApi.register(name, email, password, deviceName, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {

                    setLoading(false);

                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                            RegisterActivity.this,
                                            MainActivity.class
                                    )
                            );

                            finish();

                        } else {

                            Toast.makeText(RegisterActivity.this, json.optString("message", "Registration failed"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();

                        Toast.makeText(
                                RegisterActivity.this,
                                "JSON error",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
        });


    }
}