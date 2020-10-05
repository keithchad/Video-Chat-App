package com.chad.videochatapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.videochatapp.Authentication.LoginActivity;
import com.chad.videochatapp.Constants.Constants;
import com.chad.videochatapp.Models.User;
import com.chad.videochatapp.R;
import com.chad.videochatapp.Utils.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;


public class SettingsFragment extends Fragment {

    private PreferenceManager preferenceManager;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

    private TextView textUserName;
    private TextView textEmail;
    private TextView textJobDescription;

    private ImageView imageProfile;
    private ImageView editUserName;
    private ImageView editJobDescription;

    private MaterialButton buttonSignOut;
    private MaterialButton buttonEditProfile;
    private FloatingActionButton buttonEditImage;

    private BottomSheetDialog bottomSheetDialogPickImage;
    private BottomSheetDialog bottomSheetDialogEditName;
    private BottomSheetDialog bottomSheetDialogEditAbout;
    private ProgressDialog progressDialog;
    private AlertDialog dialogSignOut;

    private static int REQUEST_CODE_GALLERY = 111;
    private Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        textUserName = view.findViewById(R.id.textUsername);
        textEmail = view.findViewById(R.id.textEmail);
        textJobDescription = view.findViewById(R.id.textJobDescription);

        buttonSignOut = view.findViewById(R.id.buttonSignOut);
        buttonEditProfile = view.findViewById(R.id.buttonEditProfile);
        buttonEditImage = view.findViewById(R.id.buttonEditImage);

        imageProfile = view.findViewById(R.id.imageProfile);
        editUserName = view.findViewById(R.id.editName);
        editJobDescription = view.findViewById(R.id.editJobDescription);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        preferenceManager = new PreferenceManager(Objects.requireNonNull(getContext()));

        progressDialog = new ProgressDialog(getContext());

        buttonSignOut.setOnClickListener(v -> signOut());

        buttonEditProfile.setOnClickListener(v -> {
            buttonEditImage.setVisibility(View.VISIBLE);
            editUserName.setVisibility(View.VISIBLE);
            editJobDescription.setVisibility(View.VISIBLE);
        });

        getUserInfo();

        return view;
    }

    private void getUserInfo() {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {

            if(task.isSuccessful() && task.getResult() != null) {
                for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                    String firstName = documentSnapshot.getString(Constants.KEY_FIRST_NAME);
                    String lastName = documentSnapshot.getString(Constants.KEY_LAST_NAME);
                    String email = documentSnapshot.getString(Constants.KEY_EMAIL);
//                    String firstName = documentSnapshot.getString(Constants.KEY_FIRST_NAME);
//                    String lastName = documentSnapshot.getString(Constants.KEY_LAST_NAME);
//                    String email = documentSnapshot.getString(Constants.KEY_EMAIL);

                    String userName = String.format("%s %s",
                            firstName,
                            lastName);

                    textUserName.setText(userName);
                    textEmail.setText(email);

                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Something Went wrong", Toast.LENGTH_SHORT).show());
    }

    private void signOut() {
        Toast.makeText(getContext(), "Signing Out...", Toast.LENGTH_SHORT).show();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firestore.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());

        reference.update(updates)
                .addOnSuccessListener(aVoid -> {
                    preferenceManager.clearPreferences();
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }).addOnFailureListener(e -> Toast.makeText(getContext(), "Unable to sign Out", Toast.LENGTH_SHORT).show());
    }

//    private String getFileExtension(Uri uri) {
//
//        ContentResolver contentResolver = getContext();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
//
//    }



//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
//            imageUri = data.getData();
//
//            uploadToFirebase();
//        }
//
//    }

    private void uploadToFirebase() {

        if(imageUri != null) {
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
            StorageReference storageReference = firebaseStorage.getReference().child("Profile Images/" + System.currentTimeMillis()+".");
            storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!urlTask.isSuccessful());

                Uri downloadUrl = urlTask.getResult();

                final String storageDownloadUrl = String.valueOf(downloadUrl);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("imageProfile", storageDownloadUrl);
                progressDialog.dismiss();
                firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS).document(firebaseUser.getUid()).update(hashMap)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                            getUserInfo();

                        });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Upload Failed!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void openGallery() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), REQUEST_CODE_GALLERY);

    }
}