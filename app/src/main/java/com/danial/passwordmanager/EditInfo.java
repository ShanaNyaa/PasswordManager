package com.danial.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditInfo extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText websiteEditText;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        // Set up the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Edit Info");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Retrieve the document ID passed from the previous activity
        Intent intent = getIntent();
        documentId = intent.getStringExtra("documentId");

        // Initialize the EditText fields
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        websiteEditText = findViewById(R.id.websiteEditText);

        // Create an instance of FirebaseDataRetriever
        FirebaseDataRetriever firebaseDataRetriever = new FirebaseDataRetriever();

        // Get the user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Retrieve the data from the database using FirebaseDataRetriever
            firebaseDataRetriever.getEditInfoData(userId, documentId, new FirebaseDataRetriever.FirebaseCallback<EditInfoDataModel>() {
                @Override
                public void onCallback(EditInfoDataModel data) {
                    if (data != null) {
                        // Set the retrieved data to the EditText fields
                        usernameEditText.setText(data.getUsername());
                        passwordEditText.setText(data.getPassword());
                        websiteEditText.setText(data.getWebsite());
                    } else {
                        // Handle case when data is not found
                        Toast.makeText(EditInfo.this, "Data not found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }

        // Set a click listener for the save button
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditedInfo();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        if (item.getItemId() == android.R.id.home) {
            // User clicked the back button
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveEditedInfo() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String website = websiteEditText.getText().toString().trim();

        // Validate the input fields
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }

        if (TextUtils.isEmpty(website)) {
            websiteEditText.setError("Website is required");
            return;
        }

        // Get the user ID and email
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String email = currentUser.getEmail();

            // Perform the update operation to Firebase Firestore
            updateInfoInFirestore(userId, documentId, email, username, password, website);
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }

        // Finish the activity
        finish();
    }

    private void updateInfoInFirestore(String userId, String documentId, String email, String username, String password, String website) {
        // Call the existing updateInfo method in FirebaseManager
        FirebaseManager.updateInfo(userId, documentId, email, username, password, website)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle successful update
                        Toast.makeText(EditInfo.this, "Info updated successfully", Toast.LENGTH_SHORT).show();
                        // Notify the listener or perform any additional actions
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle update failure
                        Toast.makeText(EditInfo.this, "Failed to update info", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}