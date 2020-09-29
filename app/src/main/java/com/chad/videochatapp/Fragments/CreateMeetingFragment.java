package com.chad.videochatapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chad.videochatapp.R;

public class CreateMeetingFragment extends Fragment{

    private TextView createMeeting;
    private TextView joinMeeting;
    private Fragment selectedFragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_home, container, false);

        createMeeting = view.findViewById(R.id.createMeetingText);
        joinMeeting = view.findViewById(R.id.joinMeetingText);

        createMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(v);
            }
        });


        joinMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(v);
            }
        });

        getChildFragmentManager().beginTransaction()
                .replace(R.id.meetingContainer, new SettingsFragment()).commit();

//        FragmentManager childFragment = getChildFragmentManager();
//        FragmentTransaction childFragmentTransaction = childFragment.beginTransaction();
//        SettingsFragment settingsFragment = new SettingsFragment();
//        childFragmentTransaction.add(R.id.meetingContainer, settingsFragment);
//        childFragmentTransaction.commit();

       return view;
    }

    private void onButtonClick(View v) {

        switch (v.getId()) {
            case R.id.createMeetingText :
                selectedFragment = new SettingsFragment();
                break;

            case R.id.joinMeetingText :
                selectedFragment = new FriendsFragment();
                break;
        }

        if (selectedFragment != null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.meetingContainer, selectedFragment).commit();
        }
    }

}