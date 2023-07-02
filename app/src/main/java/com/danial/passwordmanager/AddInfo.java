package com.danial.passwordmanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddInfo extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText websiteEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);

        // Set up the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add Info");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Initialize the EditText fields
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        websiteEditText = findViewById(R.id.websiteEditText);

        // Set a click listener for the save button
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInfo();
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

    private void saveInfo() {
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

            // Perform the save operation to Firebase Firestore
            saveInfoToFirestore(userId, email, username, password, website);
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveInfoToFirestore(String userId, String email, String username, String password, String website) {
        // Save the username, password, and website to Firebase Firestore
        FirebaseManager.saveInfo(userId, email, username, password, website)
                .addOnSuccessListener(aVoid -> {
                    // Handle successful save
                    Toast.makeText(AddInfo.this, "Info saved successfully", Toast.LENGTH_SHORT).show();
                    // Notify the listener that info is saved
                    if (onInfoSavedListener != null) {
                        onInfoSavedListener.onInfoSaved();
                    }
                    // Finish the activity
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Handle save failure
                    Toast.makeText(AddInfo.this, "Failed to save info", Toast.LENGTH_SHORT).show();
                });
    }

    public interface OnInfoSavedListener {
        void onInfoSaved();
    }

    private OnInfoSavedListener onInfoSavedListener;

    public void setOnInfoSavedListener(OnInfoSavedListener listener) {
        this.onInfoSavedListener = listener;
    }
}
