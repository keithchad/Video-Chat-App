package com.chad.videochatapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.chad.videochatapp.Authentication.LoginActivity;
import com.chad.videochatapp.Constants.Constants;
import com.chad.videochatapp.R;
import com.chad.videochatapp.Utils.PreferenceManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN = 2500;

    Animation bottom;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initialize();
        delayForSplash();
        animation();
        setBarColors();

    }

    private void setBarColors() {
        getWindow().setStatusBarColor(getResources().getColor(R.color.splash_start_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.splash_end_color));
    }

    private void initialize() {
        preferenceManager = new PreferenceManager(this);
    }

    private void animation() {
        ImageView textName = findViewById(R.id.textName);

        bottom = AnimationUtils.loadAnimation(this, R.anim.bottom);

        textName.setAnimation(bottom);
    }

    private void delayForSplash() {
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            },SPLASH_SCREEN);
        }else {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            },SPLASH_SCREEN);
        }
    }



}