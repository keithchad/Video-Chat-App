package com.chad.videochatapp.Authentication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chad.videochatapp.Activities.MainActivity;
import com.chad.videochatapp.Constants.Constants;
import com.chad.videochatapp.R;
import com.chad.videochatapp.Utils.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputConfirmPassword;

    private MaterialButton buttonSignUp;

    private ProgressBar signUpProgressBar;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initialize();
        setBarColors();
    }

    private void initialize() {

        TextView textSignIn = findViewById(R.id.textSignIn);

        ImageView imageBack = findViewById(R.id.imageBack);

        inputFirstName = findViewById(R.id.inputFirstName);
        inputLastName = findViewById(R.id.inputLastName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);

        buttonSignUp = findViewById(R.id.buttonSignUp);

        signUpProgressBar = findViewById(R.id.signUpProgressBar);

        preferenceManager = new PreferenceManager(getApplicationContext());

        buttonSignUp.setOnClickListener(v -> {
            if(inputFirstName.getText().toString().trim().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Enter First Name", Toast.LENGTH_SHORT).show();
            }else if(inputLastName.getText().toString().trim().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
            }else if(inputEmail.getText().toString().trim().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            }else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                Toast.makeText(RegisterActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
            }else if(inputPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            }else if(inputConfirmPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Confirm your Password", Toast.LENGTH_SHORT).show();
            }else if(!inputPassword.getText().toString().equals(inputConfirmPassword.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "Password & Confirm Password must be same", Toast.LENGTH_SHORT).show();
            }else {
                signUp();
            }
        });
        imageBack.setOnClickListener(v -> onBackPressed());
        textSignIn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
    }

    private void signUp() {
        buttonSignUp.setVisibility(View.INVISIBLE);
        signUpProgressBar.setVisibility(View.VISIBLE);

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            if (firebaseUser != null) {
                String userId = firebaseUser.getUid();
                HashMap<String, Object> user = new HashMap<>();
                user.put(Constants.KEY_USER_ID, userId);
                user.put(Constants.KEY_FIRST_NAME, inputFirstName.getText().toString());
                user.put(Constants.KEY_LAST_NAME, inputLastName.getText().toString());
                user.put(Constants.KEY_EMAIL, email);
                user.put(Constants.KEY_PASSWORD, password);
                user.put(Constants.KEY_IMAGE_PROFILE, "https://firebasestorage.googleapis.com/v0/b/video-chatting-app-4cf70.appspot.com/o/profile.svg?alt=media&token=91548754-6629-4c01-a7b6-944a75130b05");
                user.put(Constants.KEY_JOB_DESCRIPTION, "Edit Job Description");

                firestore.collection(Constants.KEY_COLLECTION_USERS).document(firebaseUser.getUid()).set(user).addOnSuccessListener(documentReference -> {
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, firebaseUser.getUid());
                    preferenceManager.putString(Constants.KEY_FIRST_NAME, inputFirstName.getText().toString());
                    preferenceManager.putString(Constants.KEY_LAST_NAME, inputLastName.getText().toString());
                    preferenceManager.putString(Constants.KEY_EMAIL, inputEmail.getText().toString());

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }).addOnFailureListener(e -> {
                    signUpProgressBar.setVisibility(View.INVISIBLE);
                    buttonSignUp.setVisibility(View.VISIBLE);

                    Toast.makeText(RegisterActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show());





    }

    private void setBarColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorBackground));
        }
    }

}