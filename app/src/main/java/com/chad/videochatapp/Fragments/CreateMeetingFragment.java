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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_home, container, false);

        FragmentManager childFragment = getChildFragmentManager();
        FragmentTransaction childFragmentTransaction = childFragment.beginTransaction();
        SettingsFragment settingsFragment = new SettingsFragment();
        childFragmentTransaction.add(R.id.meetingContainer, settingsFragment);
        childFragmentTransaction.commit();

       return view;
    }

}