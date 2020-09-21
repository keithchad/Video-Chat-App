package com.chad.videochatapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.chad.videochatapp.Constants.Constants;
import com.chad.videochatapp.Models.User;
import com.chad.videochatapp.Network.APIClient;
import com.chad.videochatapp.Network.APIService;
import com.chad.videochatapp.R;
import com.chad.videochatapp.Utils.PreferenceManager;
import com.google.firebase.iid.FirebaseInstanceId;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingInvitationActivity extends AppCompatActivity {

    ImageView imageMeetingType;
    ImageView imageStopInvitation;
    TextView textFirstChar;
    TextView textUsername;
    TextView textEmail;

    private PreferenceManager preferenceManager;

    private String inviterToken = null;
    private String meetingRoom = null;
    private String meetingType = null;

    User user = (User) getIntent().getSerializableExtra("user");


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
        meetingType = getIntent().getStringExtra("type");

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

        imageStopInvitation.setOnClickListener(v -> {
            if(user != null){
                cancelInvitation(user.token);
                if (meetingType != null && user != null) {
                    initiateMeeting(meetingType, user.token);
                }
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null) {
                inviterToken = task.getResult().getToken();
            }
        });

    }

    private void initiateMeeting(String meetingType, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(Constants.KEY_FIRST_NAME, preferenceManager.getString(Constants.KEY_FIRST_NAME));
            data.put(Constants.KEY_LAST_NAME, preferenceManager.getString(Constants.KEY_LAST_NAME));
            data.put(Constants.KEY_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);

            meetingRoom = preferenceManager.getString(Constants.KEY_USER_ID) + "_" +
                    UUID.randomUUID().toString().substring(0, 5);

            data.put(Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION);

        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void cancelInvitation(String receiverToken) {
        try {

            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, Constants.REMOTE_MSG_INVITATION_CANCELLED);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION_CANCELLED);

        }catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void sendRemoteMessage(String remoteMessageBody, String type) {
        APIClient.getClient().create(APIService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (Constants.REMOTE_MSG_INVITATION.equals(Constants.REMOTE_MSG_INVITATION)){
                        Toast.makeText(OutgoingInvitationActivity.this, "Invitation Sent Successfully", Toast.LENGTH_SHORT).show();
                    }else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)) {
                        Toast.makeText(OutgoingInvitationActivity.this, "Invitation Cancelled", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else {
                    Toast.makeText(OutgoingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(OutgoingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if(type != null) {
                if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {

                    try {
                        URL severURL = new URL("https://meet.jit.si");

                        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                        builder.setServerURL(severURL);
                        builder.setWelcomePageEnabled(false);
                        builder.setRoom(meetingRoom);

                        if(meetingType.equals("audio")) {
                            builder.setVideoMuted(true);
                        }

//                        JitsiMeetConferenceOptions conferenceOptions =
//                                new JitsiMeetConferenceOptions.Builder()
//                                        .setServerURL(severURL)
//                                        .setWelcomePageEnabled(false)
//                                        .setRoom(meetingRoom)
//                                        .build();

                        JitsiMeetActivity.launch(OutgoingInvitationActivity.this, builder.build());
                        finish();

                    } catch (MalformedURLException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    Toast.makeText(context, "Invitation Accepted", Toast.LENGTH_SHORT).show();
                }else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)) {
                    Toast.makeText(context, "Invitation Rejected", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }

    private void setBarColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorMeetingInvitationEnd));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMeetingInvitationStart));
        }
    }
}