package com.chad.videochatapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.videochatapp.Adapter.UserAdapter;
import com.chad.videochatapp.Authentication.LoginActivity;
import com.chad.videochatapp.Constants.Constants;
import com.chad.videochatapp.Models.User;
import com.chad.videochatapp.R;
import com.chad.videochatapp.Utils.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private List<User> list;
    private UserAdapter userAdapter;
    private TextView textErrorMessage;
    private ProgressBar userProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        getUsers();
    }

    private void initialize() {

        preferenceManager = new PreferenceManager(getApplicationContext());
        TextView textTitle = findViewById(R.id.textTitle);
        TextView textSignOut = findViewById(R.id.textSignOut);
        textErrorMessage = findViewById(R.id.textErrorMessage);
        userProgressBar = findViewById(R.id.userProgressBar);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        list = new ArrayList<>();
        userAdapter = new UserAdapter(list);
        recyclerView.setAdapter(userAdapter);

        textSignOut.setOnClickListener(v -> signOut());

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
    }

    private void getUsers() {
        userProgressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {
            userProgressBar.setVisibility(View.GONE);
            String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);
            if(task.isSuccessful() && task.getResult() != null) {
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
        }).addOnFailureListener(e -> Toast.makeText(this, "Something Went wrong", Toast.LENGTH_SHORT).show());
    }

    private void sendFCMTokenToDatabase(String token) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference reference = firestore.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        reference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Token Updated Successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Unable to send Token: "+ e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    private void signOut() {
        Toast.makeText(this, "Signing Out...", Toast.LENGTH_SHORT).show();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firestore.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());

        reference.update(updates)
                .addOnSuccessListener(aVoid -> {
                    preferenceManager.clearPreferences();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Unable to sign Out", Toast.LENGTH_SHORT).show());
    }

}