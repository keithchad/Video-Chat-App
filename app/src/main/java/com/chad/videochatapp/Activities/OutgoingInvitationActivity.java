package com.chad.videochatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.videochatapp.Models.User;
import com.chad.videochatapp.R;
import com.chad.videochatapp.Utils.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class OutgoingInvitationActivity extends AppCompatActivity {

    ImageView imageMeetingType;
    ImageView imageStopInvitation;
    TextView textFirstChar;
    TextView textUsername;
    TextView textEmail;

    private PreferenceManager preferenceManager;
    private String inviterToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_invitation);

        setBarColors();
        initialize();

    }

    private void initialize() {
        imageMeetingType = findViewById(R.id.imageMeetingType);
        imageStopInvitation = findViewById(R.id.imageStopInvitation);
        textFirstChar = findViewById(R.id.textFirstChar);
        textUsername = findViewById(R.id.textUsername);
        textEmail = findViewById(R.id.textEmail);

        preferenceManager  = new PreferenceManager(getApplicationContext());

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null) {
                inviterToken = task.getResult().getToken();
            }
        });

        User user = (User) getIntent().getSerializableExtra("user");
        String meetingType = getIntent().getStringExtra("type");

        if (user != null) {
            String username = String.format("%s %s", user.firstName, user.lastName);

            textFirstChar.setText(user.firstName.substring(0, 1));
            textUsername.setText(username);
            textEmail.setText(user.email);
        }

        if(meetingType != null) {
            if(meetingType.equals("video")) {
                imageMeetingType.setImageResource(R.drawable.ic_videocam);
            }else if(meetingType.equals("audio")) {
                imageMeetingType.setImageResource(R.drawable.ic_call);
            }
        }

        imageStopInvitation.setOnClickListener(v -> onBackPressed());

    }

    private void setBarColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorMeetingInvitationEnd));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMeetingInvitationStart));
        }
    }
}