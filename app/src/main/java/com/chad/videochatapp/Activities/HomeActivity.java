package com.chad.videochatapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.chad.videochatapp.Fragments.CreateMeetingFragment;
import com.chad.videochatapp.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportFragmentManager().beginTransaction().replace(R.id.meetingContainer, new CreateMeetingFragment()).commit();

    }
}