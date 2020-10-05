package com.chad.videochatapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.videochatapp.Activities.OutgoingInvitationActivity;
import com.chad.videochatapp.Adapter.UserAdapter;
import com.chad.videochatapp.Authentication.LoginActivity;
import com.chad.videochatapp.Constants.Constants;
import com.chad.videochatapp.Listeners.UserListener;
import com.chad.videochatapp.Models.User;
import com.chad.videochatapp.R;
import com.chad.videochatapp.Utils.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FriendsFragment extends Fragment implements UserListener {


    private PreferenceManager preferenceManager;
    private List<User> list;
    private UserAdapter userAdapter;

    private TextView textErrorMessage;

    private ImageView imageConference;

    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        textErrorMessage = view.findViewById(R.id.textErrorMessage);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        TextView textTitle = view.findViewById(R.id.textTitle);

        imageConference = view.findViewById(R.id.imageConference);

        recyclerView = view.findViewById(R.id.recyclerView);
        preferenceManager = new PreferenceManager(Objects.requireNonNull(getContext()));

        swipeRefreshLayout.setOnRefreshListener(this::getUsers);

        list = new ArrayList<>();
        userAdapter = new UserAdapter(list, this);
        recyclerView.setAdapter(userAdapter);

        String title = String.format("%s %s",
                preferenceManager.getString(Constants.KEY_FIRST_NAME),
                preferenceManager.getString(Constants.KEY_LAST_NAME));


        textTitle.setText(title);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null) {
                        sendFCMTokenToDatabase(task.getResult().getToken());
                    }
                });

        getUsers();
        return view;
    }

    private void getUsers() {
        swipeRefreshLayout.setRefreshing(true);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {

            swipeRefreshLayout.setRefreshing(false);

            String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);

            if(task.isSuccessful() && task.getResult() != null) {
                list.clear();
                for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    if (myUserId.equals(documentSnapshot.getId())) {
                        continue;
                    }
                    User user = new User();
                    user.firstName = documentSnapshot.getString(Constants.KEY_FIRST_NAME);
                    user.lastName = documentSnapshot.getString(Constants.KEY_LAST_NAME);
                    user.email = documentSnapshot.getString(Constants.KEY_EMAIL);
                    user.token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);

                    list.add(user);
                }
                if (list.size() > 0) {
                    userAdapter.notifyDataSetChanged();
                }else {
                    String error = String.format("%s", "No Users Available");
                    textErrorMessage.setText(error);
                    textErrorMessage.setVisibility(View.VISIBLE);
                }
            }else {
                String error = String.format("%s", "No Users Available");
                textErrorMessage.setText(error);
                textErrorMessage.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Something Went wrong", Toast.LENGTH_SHORT).show());
    }

    private void sendFCMTokenToDatabase(String token) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference reference = firestore.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        reference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Token Updated Successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Unable to send Token: "+ e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    @Override
    public void initiateVideoMeeting(User user) {
        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(getContext(), user.firstName + "" + user.lastName + " is not available for Video meeting!", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(getContext(), OutgoingInvitationActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "video");
            startActivity(intent);
        }
    }

    @Override
    public void initiateAudioMeeting(User user) {
        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(getContext(), user.firstName + "" + user.lastName + " is not available for Audio meeting!", Toast.LENGTH_SHORT).show();
        }else {

            Intent intent = new Intent(getContext(), OutgoingInvitationActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "audio");
            startActivity(intent);
        }
    }

    @Override
    public void onMultipleUsersAction(Boolean isMultipleUsersSelected) {
        if(isMultipleUsersSelected) {
            imageConference.setVisibility(View.VISIBLE);
            imageConference.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), OutgoingInvitationActivity.class);
                intent.putExtra("selectedUsers", new Gson().toJson(userAdapter.getSelectedUsers()));
                intent.putExtra("type", "video");
                intent.putExtra("isMultiple", true);
                startActivity(intent);
            });
        }else {
            imageConference.setVisibility(View.GONE);
        }
    }


}