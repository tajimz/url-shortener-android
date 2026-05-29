package com.tajim.urlshortener.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tajim.urlshortener.R;
import com.tajim.urlshortener.auth.LoginActivity;
import com.tajim.urlshortener.databinding.ActivityMainBinding;
import com.tajim.urlshortener.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupEdgeToEdge();
        setupLayout();
        initVariables();
        checkIfLoggedIn();
        setupClickListeners();



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
    }
    private void checkIfLoggedIn(){

        if(!sessionManager.isLoggedIn()){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
    private void setupClickListeners(){
        binding.btnLogout.setOnClickListener(v->{
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}