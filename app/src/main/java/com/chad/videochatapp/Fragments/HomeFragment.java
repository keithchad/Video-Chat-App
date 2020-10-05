package com.chad.videochatapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.chad.videochatapp.Constants.Constants;
import com.chad.videochatapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class HomeFragment extends Fragment{

    private Fragment selectedFragment = null;

    ImageView imageProfile;
    TextView textUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_home, container, false);

       initialize(view);
       getChildFragmentManager().beginTransaction().replace(R.id.meetingContainer, new CreateMeetingFragment()).commit();
       getUserInfo();

       return view;
    }

    private void initialize(View view) {

        TextView joinMeetingText = view.findViewById(R.id.joinMeetingText);
        TextView createMeetingText = view.findViewById(R.id.createMeetingText);

        imageProfile = view.findViewById(R.id.imageProfile);
        textUsername = view.findViewById(R.id.textUsername);

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

    private void getUserInfo() {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {

            if(task.isSuccessful() && task.getResult() != null) {
                for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                    String firstName = documentSnapshot.getString(Constants.KEY_FIRST_NAME);
                    String lastName = documentSnapshot.getString(Constants.KEY_LAST_NAME);
                    //String imageProfile = documentSnapshot.getString(Constants.KEY_IMAGE_PROFILE);

                    String userName = firstName + lastName;

                    textUsername.setText(userName);

                    //Glide.with(getContext()).load(imageProfile).into(imageProfile);

                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Something Went wrong", Toast.LENGTH_SHORT).show());
    }

}