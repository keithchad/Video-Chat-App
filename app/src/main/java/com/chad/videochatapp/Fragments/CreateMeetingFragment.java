package com.chad.videochatapp.Fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.chad.videochatapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class CreateMeetingFragment extends Fragment {

    private TextInputEditText createMeetingEditText;

    private ConnectivityManager connectivityManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_create_meeting, container, false);

        initialize(view);

        return view;
    }

    private void initialize(View view) {

        createMeetingEditText = view.findViewById(R.id.createMeetingEditText);
        TextInputLayout createTextInputLayout = view.findViewById(R.id.createTextInputLayout);
        MaterialButton createMeetingButton = view.findViewById(R.id.createMeetingButton);

        createTextInputLayout.setEndIconCheckable(true);
        createTextInputLayout.setEndIconOnClickListener(v -> {
            if (createMeetingEditText.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Enter a Code", Toast.LENGTH_SHORT).show();
            }else if (createMeetingEditText.length() <= 8) {
                Toast.makeText(getContext(), "Enter a longer Code", Toast.LENGTH_SHORT).show();
            }else {
                copyToClipboard();
                Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
            }

        });

        connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        createMeetingButton.setOnClickListener(v -> {
            if (createMeetingEditText.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Enter a Code", Toast.LENGTH_SHORT).show();
            }else if (createMeetingEditText.length() <= 8) {
                Toast.makeText(getContext(), "Enter a longer Code", Toast.LENGTH_SHORT).show();
            } else {
                createMeeting();
            }
        });

    }

    private void copyToClipboard() {
        String createdMeetingCode = createMeetingEditText.getText().toString();

        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Create Meeting", createdMeetingCode);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        }
    }

    private void createMeeting() {

        String createdMeetingCode = createMeetingEditText.getText().toString();

        try {
            URL severURL = new URL("https://meet.jit.si");

            JitsiMeetConferenceOptions conferenceOptions =
                    new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(severURL)
                            .setWelcomePageEnabled(false)
                            .setRoom(createdMeetingCode)
                            .build();
            JitsiMeetActivity.launch(Objects.requireNonNull(getContext()), conferenceOptions);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
}