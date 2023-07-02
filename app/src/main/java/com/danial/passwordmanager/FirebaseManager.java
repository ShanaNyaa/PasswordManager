package com.danial.passwordmanager;

import android.app.*;
import android.content.Context;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.*;
import com.google.android.material.navigation.*;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void createUser(String email, String password, final OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        UserModel userModel = new UserModel(email); // Create UserModel object without password
                        db.collection("users").document(authResult.getUser().getUid()).set(userModel);
                    }
                });
    }

    public static Task<Void> saveInfo(String userId, String email, String username, String password, String website) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userCollection = db.collection("users").document(userId).collection("userData");

        DocumentReference newDocumentRef = userCollection.document();

        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("password", password);
        userData.put("website", website);

        return newDocumentRef.set(userData);
    }

    public void sendEmailVerification(final OnCompleteListener<Void> listener) {
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(listener);
    }

    public void login(String email, String password, final OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    public void sendPasswordResetEmail(String email, final OnCompleteListener<Void> listener) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(listener);
    }

    public void updateUserEmailVerification(String uid, boolean isEmailVerified) {
        db.collection("users").document(uid)
                .update("emailVerified", isEmailVerified)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Field update successful
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Field update failed
                    }
                });
    }

    public static void setUserEmailInNavigationHeader(Context context, String userEmail) {
        View headerView = ((NavigationView) ((Activity) context).findViewById(R.id.navigationView)).getHeaderView(0);
        TextView userEmailTextView = headerView.findViewById(R.id.userEmailTextView);
        userEmailTextView.setText(userEmail);
    }

    public static void logout(Context context, OnCompleteListener<Void> listener) {
        FirebaseAuth.getInstance().signOut();
        listener.onComplete(Tasks.forResult(null));
    }

    public static void getDataList(final String userId, FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference dataCollection = db.collection("users").document(userId).collection("userData");

        dataCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DataModel> dataList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String line1 = document.getString("website");
                    String line2 = document.getString("username");
                    String documentId = document.getId();
                    dataList.add(new DataModel(line1, line2, documentId));
                }
                callback.onCallback(dataList);
            } else {
                Log.e("FirebaseManager", "Error getting data", task.getException());
                callback.onCallback(new ArrayList<DataModel>());
            }
        });
    }

    public static void getDocumentList(String userId, FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentRef = db.collection("users").document(userId).collection("userData").document();

        String documentId = documentRef.getId();
    }

    public static void deleteData(String userId, String documentId, OnCompleteListener<Void> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentRef = db.collection("users")
                .document(userId)
                .collection("userData")
                .document(documentId);
        documentRef.delete()
                .addOnCompleteListener(listener);
    }

    public static Task<Void> updateInfo(String userId, String documentId, String email, String username, String password, String website) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userCollection = db.collection("users").document(userId).collection("userData");

        DocumentReference documentRef = userCollection.document(documentId);

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("email", email);
        updatedData.put("username", username);
        updatedData.put("password", password);
        updatedData.put("website", website);

        return documentRef.update(updatedData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle successful update
                        Log.d("FirebaseManager", "Info updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle update failure
                        Log.e("FirebaseManager", "Failed to update info", e);
                    }
                });
    }
}
