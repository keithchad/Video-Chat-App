package com.chad.videochatapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.chad.videochatapp.Authentication.LoginActivity;
import com.chad.videochatapp.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        setBarColors();
        initialize();
    }

    private void initialize() {

        Button getStatedButton = findViewById(R.id.getStartedButton);

        getStatedButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void setBarColors() {
        getWindow().setStatusBarColor(getResources().getColor(R.color.splash_start_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.splash_end_color));
    }
}