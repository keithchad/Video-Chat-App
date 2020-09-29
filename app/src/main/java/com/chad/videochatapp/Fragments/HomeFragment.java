package com.chad.videochatapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.chad.videochatapp.R;

public class HomeFragment extends Fragment{

    private Fragment selectedFragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_home, container, false);

       initialize(view);
       getChildFragmentManager().beginTransaction().replace(R.id.meetingContainer, new CreateMeetingFragment()).commit();

       return view;
    }

    private void initialize(View view) {

        TextView createMeetingText = view.findViewById(R.id.createMeetingText);
        TextView joinMeetingText = view.findViewById(R.id.joinMeetingText);

        createMeetingText.setOnClickListener(this::onButtonClick);

        joinMeetingText.setOnClickListener(this::onButtonClick);
    }

    private void onButtonClick(View v) {

        switch (v.getId()) {
            case R.id.createMeetingText :
                selectedFragment = new CreateMeetingFragment();
                break;

            case R.id.joinMeetingText :
                selectedFragment = new JoinMeetingFragment();
                break;
        }

        if (selectedFragment != null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.meetingContainer, selectedFragment).commit();
        }
    }

}