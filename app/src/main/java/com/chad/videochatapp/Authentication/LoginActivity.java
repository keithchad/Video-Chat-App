package com.chad.videochatapp.Authentication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;
    private MaterialButton buttonSignIn;
    private ProgressBar signInProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
        setBarColors();

    }

    private void initialize() {

        TextView textSignUp = findViewById(R.id.textSignUp);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonSignIn = findViewById(R.id.buttonLogIn);
        signInProgressBar = findViewById(R.id.signInProgressBar);

        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());

        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        buttonSignIn.setOnClickListener(v -> {
           if(inputEmail.getText().toString().trim().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
           }else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
               Toast.makeText(LoginActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
           }else if(inputPassword.getText().toString().trim().isEmpty()) {
               Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
           }else {
               signIn();
           }
        });

        textSignUp.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

    }

    private void signIn() {

        buttonSignIn.setVisibility(View.INVISIBLE);
        signInProgressBar.setVisibility(View.VISIBLE);

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else {
                signInProgressBar.setVisibility(View.INVISIBLE);
                buttonSignIn.setVisibility(View.VISIBLE);

                Toast.makeText(LoginActivity.this, "Unable to sign In", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Unable to sign In " + e.getMessage(), Toast.LENGTH_SHORT).show());


    }

    private void setBarColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorBackground));
        }
    }
}