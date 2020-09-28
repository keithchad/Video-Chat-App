package com.chad.videochatapp.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.chad.videochatapp.Fragments.FriendsFragment;
import com.chad.videochatapp.Fragments.HomeFragment;
import com.chad.videochatapp.Fragments.SettingsFragment;
import com.chad.videochatapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{


    private Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setBarColors();
       getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
       initialize();

       checkForBatteryOptimization();

    }

    private void initialize() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {

                switch (item.getItemId()) {
                    case R.id.home :
                        selectedFragment = new HomeFragment();
                        break;

                    case R.id.user :
                        selectedFragment = new FriendsFragment();
                        break;

                    case R.id.settings :
                        selectedFragment = new SettingsFragment();
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, selectedFragment).commit();
                }

                return true;
            };

    private void setBarColors() {
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.white));
    }

    private void checkForBatteryOptimization() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

            if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Warning");
                builder.setMessage("Battery Optimization is Enabled,It can Interrupt running in background!");
                builder.setPositiveButton("Disable", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    startActivity(intent);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int REQUEST_CODE_BATTERY_OPTIMIZATIONS = 1;
        if(requestCode == REQUEST_CODE_BATTERY_OPTIMIZATIONS) {
            checkForBatteryOptimization();
        }
    }

    //    private void initialize() {
//
//        preferenceManager = new PreferenceManager(getApplicationContext());
//        TextView textTitle = findViewById(R.id.textTitle);
//        TextView textSignOut = findViewById(R.id.textSignOut);
//        textErrorMessage = findViewById(R.id.textErrorMessage);
////        userProgressBar = findViewById(R.id.userProgressBar);
//        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
//
//        swipeRefreshLayout.setOnRefreshListener(this::getUsers);
//
//
//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//
//        list = new ArrayList<>();
//        userAdapter = new UserAdapter(list, this);
//        recyclerView.setAdapter(userAdapter);
//
//        textSignOut.setOnClickListener(v -> signOut());
//
//       String title = String.format("%s %s",
//                preferenceManager.getString(Constants.KEY_FIRST_NAME),
//                preferenceManager.getString(Constants.KEY_LAST_NAME));
//
//
//        textTitle.setText(title);
//
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(task -> {
//                    if(task.isSuccessful() && task.getResult() != null) {
//                        sendFCMTokenToDatabase(task.getResult().getToken());
//                    }
//                });
//    }
//
//    private void getUsers() {
//        swipeRefreshLayout.setRefreshing(true);
////        userProgressBar.setVisibility(View.VISIBLE);
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        firestore.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {
//            swipeRefreshLayout.setRefreshing(false);
////            userProgressBar.setVisibility(View.GONE);
//            String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);
//            if(task.isSuccessful() && task.getResult() != null) {
//                list.clear();
//                for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                    if (myUserId.equals(documentSnapshot.getId())) {
//                        continue;
//                    }
//                    User user = new User();
//                    user.firstName = documentSnapshot.getString(Constants.KEY_FIRST_NAME);
//                    user.lastName = documentSnapshot.getString(Constants.KEY_LAST_NAME);
//                    user.email = documentSnapshot.getString(Constants.KEY_EMAIL);
//                    user.token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);
//
//                    list.add(user);
//                }
//                if (list.size() > 0) {
//                    userAdapter.notifyDataSetChanged();
//                }else {
//                    String error = String.format("%s", "No Users Available");
//                    textErrorMessage.setText(error);
//                    textErrorMessage.setVisibility(View.VISIBLE);
//                }
//            }else {
//                String error = String.format("%s", "No Users Available");
//                textErrorMessage.setText(error);
//                textErrorMessage.setVisibility(View.VISIBLE);
//            }
//        }).addOnFailureListener(e -> Toast.makeText(this, "Something Went wrong", Toast.LENGTH_SHORT).show());
//    }
//
//    private void sendFCMTokenToDatabase(String token) {
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        DocumentReference reference = firestore.collection(Constants.KEY_COLLECTION_USERS).document(
//                preferenceManager.getString(Constants.KEY_USER_ID)
//        );
//        reference.update(Constants.KEY_FCM_TOKEN, token)
//                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Token Updated Successfully", Toast.LENGTH_SHORT).show())
//                .addOnFailureListener(e -> Toast.makeText(this, "Unable to send Token: "+ e.getMessage(), Toast.LENGTH_SHORT).show());
//
//    }
//
//    private void signOut() {
//        Toast.makeText(this, "Signing Out...", Toast.LENGTH_SHORT).show();
//
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//
//        DocumentReference reference = firestore.collection(Constants.KEY_COLLECTION_USERS).document(
//                preferenceManager.getString(Constants.KEY_USER_ID)
//        );
//
//        HashMap<String, Object> updates = new HashMap<>();
//        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
//
//        reference.update(updates)
//                .addOnSuccessListener(aVoid -> {
//                    preferenceManager.clearPreferences();
//                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//                    finish();
//                }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Unable to sign Out", Toast.LENGTH_SHORT).show());
//    }

//    @Override
//    public void initiateVideoMeeting(User user) {
//        if (user.token == null || user.token.trim().isEmpty()) {
//            Toast.makeText(this, user.firstName + "" + user.lastName + " is not available for Video meeting!", Toast.LENGTH_SHORT).show();
//        }else {
//           Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
//           intent.putExtra("user", user);
//           intent.putExtra("type", "video");
//           startActivity(intent);
//        }
//    }
//
//    @Override
//    public void initiateAudioMeeting(User user) {
//        if (user.token == null || user.token.trim().isEmpty()) {
//            Toast.makeText(this, user.firstName + "" + user.lastName + " is not available for Audio meeting!", Toast.LENGTH_SHORT).show();
//        }else {
//            Toast.makeText(this, "Audio Meeting with " + user.firstName + " " + user.lastName, Toast.LENGTH_SHORT).show();
//        }
//    }
}