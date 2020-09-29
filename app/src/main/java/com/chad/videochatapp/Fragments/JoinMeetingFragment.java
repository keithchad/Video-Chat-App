package com.chad.videochatapp.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.videochatapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;


public class JoinMeetingFragment extends Fragment {

    private TextInputEditText joinMeetingEditText;
    private ConnectivityManager connectivityManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view  = inflater.inflate(R.layout.fragment_join_meeting, container,false);

        initialize(view);
        return view;
    }

    private void initialize(View view) {
        joinMeetingEditText = view.findViewById(R.id.joinMeetingEditText);
        MaterialButton joinMeetingButton = view.findViewById(R.id.joinMeetingButton);

        connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        joinMeetingButton.setOnClickListener(v -> {
            if (joinMeetingEditText.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Enter a Code", Toast.LENGTH_SHORT).show();
            }else if (joinMeetingEditText.length() <= 8) {
                Toast.makeText(getContext(), "Enter a longer Code", Toast.LENGTH_SHORT).show();
            }else if ((connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED)
                    || (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED)) {

                Toast.makeText(getContext(), "Check your Internet Connection and try again", Toast.LENGTH_SHORT).show();
            }else {
                joinMeeting();
            }

        });
    }

    private void joinMeeting() {
        String joinMeetingCode = joinMeetingEditText.getText().toString();
        try {
            URL severURL = new URL("https://meet.jit.si");

            JitsiMeetConferenceOptions conferenceOptions =
                    new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(severURL)
                            .setWelcomePageEnabled(false)
                            .setRoom(joinMeetingCode)
                            .build();
            JitsiMeetActivity.launch(Objects.requireNonNull(getContext()), conferenceOptions);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}