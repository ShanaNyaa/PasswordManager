package com.danial.passwordmanager;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseDataRetriever {
    private FirebaseFirestore db;

    public FirebaseDataRetriever() {
        db = FirebaseFirestore.getInstance();
    }

    public void getEditInfoData(String userId, String documentId, FirebaseCallback<EditInfoDataModel> callback) {
        CollectionReference dataCollection = db.collection("users").document(userId).collection("userData");

        dataCollection.document(documentId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String username = document.getString("username");
                    String password = document.getString("password");
                    String website = document.getString("website");
                    EditInfoDataModel data = new EditInfoDataModel(username, password, website);
                    callback.onCallback(data);
                } else {
                    // Document does not exist
                    callback.onCallback(null);
                }
            } else {
                Log.e("FirebaseDataRetriever", "Error getting data", task.getException());
                callback.onCallback(null);
            }
        });
    }

    public interface FirebaseCallback<T> {
        void onCallback(T data);
    }
}
