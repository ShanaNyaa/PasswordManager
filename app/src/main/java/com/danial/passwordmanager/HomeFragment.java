package com.danial.passwordmanager;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private List<DataModel> dataList;
    private String userId;

    @Override
    public void onResume() {
        super.onResume();
        refreshDataList();
    }

    private void refreshDataList() {
        FirebaseManager.getDataList(userId, new FirebaseCallback() {
            @Override
            public void onCallback(List<DataModel> data) {
                dataList.clear();
                dataList.addAll(data);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new HomeAdapter();
        recyclerView.setAdapter(adapter);

        // Initialize dataList
        dataList = new ArrayList<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        // Retrieve data list from Firebase Firestore using FirebaseManager
        FirebaseManager.getDataList(userId, new FirebaseCallback() {
            @Override
            public void onCallback(List<DataModel> data) {
                dataList.clear();
                dataList.addAll(data);
                adapter.notifyDataSetChanged();
            }
        });

        FloatingActionButton fabAdd = rootView.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the button click here
                // Start the desired activity
                Intent intent = new Intent(getActivity(), AddInfo.class);
                startActivity(intent);
            }
        });

        FirebaseManager.getDocumentList(userId, new FirebaseCallback() {
            @Override
            public void onCallback(List<DataModel> documentData) {
                if (!documentData.isEmpty()) {
                    DataModel documentModel = documentData.get(0);
                    String foundDocumentId = documentModel.getDocumentId();
                    // Use the foundDocumentId as needed
                    // For example, you can show it in a Toast message
                    Toast.makeText(getActivity(), "Found documentId: " + foundDocumentId, Toast.LENGTH_SHORT).show();
                } else {
                    // DocumentId not found
                    Toast.makeText(getActivity(), "DocumentId not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            // Set the click listener on the ViewHolder
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    if (position != RecyclerView.NO_POSITION && position < dataList.size()) {
                        DataModel clickedItem = dataList.get(position);
                        String documentId = clickedItem.getDocumentId(); // Assuming the document ID is already available in DataModel

                        // Start the EditInfo activity and pass the retrieved data to it
                        Intent intent = new Intent(v.getContext(), EditInfo.class);
                        intent.putExtra("documentId", documentId);
                        intent.putExtra("line1", clickedItem.getLine1());
                        intent.putExtra("line2", clickedItem.getLine2());
                        v.getContext().startActivity(intent);
                    }
                }
            });

            // Set the long click listener on the ViewHolder
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    if (position != RecyclerView.NO_POSITION && position < dataList.size()) {
                        DataModel longClickedItem = dataList.get(position);
                        String documentId = longClickedItem.getDocumentId();

                        Log.d("DeleteData", "Position: " + position + ", DocumentId: " + documentId); // Debug log

                        // Change the background color of the item view to a darker color
                        int darkerColor = ContextCompat.getColor(requireContext(), R.color.black);
                        v.setBackgroundColor(darkerColor);

                        if (documentId != null) {
                            // Show a dialog or options for deleting the data
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                            builder.setTitle("Delete Data")
                                    .setMessage("Are you sure you want to delete this data?")
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Delete the data using FirebaseManager
                                            FirebaseManager.deleteData(userId, documentId, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Data successfully deleted
                                                        Toast.makeText(getActivity(), "Data deleted successfully", Toast.LENGTH_SHORT).show();
                                                        // Refresh the data list after deletion
                                                        refreshDataList();
                                                    } else {
                                                        // Error occurred while deleting the data
                                                        Toast.makeText(getActivity(), "Failed to delete data", Toast.LENGTH_SHORT).show();
                                                    }
                                                    // Restore the background color of the item view
                                                    int normalColor = ContextCompat.getColor(requireContext(), R.color.colorGrey);
                                                    v.setBackgroundColor(normalColor);
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Restore the background color of the item view
                                            int normalColor = ContextCompat.getColor(requireContext(), R.color.colorGrey);
                                            v.setBackgroundColor(normalColor);
                                        }
                                    })
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            // Restore the background color of the item view
                                            int normalColor = ContextCompat.getColor(requireContext(), R.color.colorGrey);
                                            v.setBackgroundColor(normalColor);
                                        }
                                    })
                                    .show();
                        } else {
                            // Handle the case where documentId is null
                            Log.e("DeleteData", "Invalid documentId"); // Debug log
                            Toast.makeText(getActivity(), "Invalid data. Unable to delete.", Toast.LENGTH_SHORT).show();
                            // Restore the background color of the item view
                            int normalColor = ContextCompat.getColor(requireContext(), R.color.colorGrey);
                            v.setBackgroundColor(normalColor);
                        }

                        return true;
                    }

                    return false;
                }

            });

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DataModel item = dataList.get(position);
            // Set the two lines of data in your item layout
            holder.textViewLine1.setText(item.getLine1());
            holder.textViewLine2.setText(item.getLine2());

            // Set click listener for the copy icon
            holder.copyIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an instance of FirebaseDataRetriever
                    FirebaseDataRetriever dataRetriever = new FirebaseDataRetriever();

                    // Get the password using the dataRetriever instance
                    dataRetriever.getEditInfoData(userId, item.getDocumentId(), new FirebaseDataRetriever.FirebaseCallback<EditInfoDataModel>() {
                        @Override
                        public void onCallback(EditInfoDataModel data) {
                            if (data != null) {
                                String password = data.getPassword();

                                // Create an instance of XChaCha20EncryptionHelper
                                XChaCha20EncryptionHelper encryptionHelper = new XChaCha20EncryptionHelper();

                                // Copy the password to the clipboard
                                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Password", password);
                                clipboard.setPrimaryClip(clip);
                                // Show a toast message to indicate successful copying
                                Toast.makeText(getActivity(), "Password copied to clipboard", Toast.LENGTH_SHORT).show();
                            } else {
                                // Error occurred or password not found
                                Toast.makeText(getActivity(), "Failed to retrieve password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            // Set click listener for the delete icon
            holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show a dialog or options for deleting the data
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setTitle("Delete Data")
                            .setMessage("Are you sure you want to delete this data?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Delete the data using FirebaseManager
                                    String documentId = item.getDocumentId();
                                    FirebaseManager.deleteData(userId, documentId, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Data successfully deleted
                                                Toast.makeText(getActivity(), "Data deleted successfully", Toast.LENGTH_SHORT).show();
                                                // Refresh the data list after deletion
                                                refreshDataList();
                                            } else {
                                                // Error occurred while deleting the data
                                                Toast.makeText(getActivity(), "Failed to delete data", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing or perform any action on cancel
                                }
                            })
                            .show();
                }
            });
        }


        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewLine1;
            TextView textViewLine2;
            ImageView copyIcon;
            ImageView deleteIcon;

            ViewHolder(View itemView) {
                super(itemView);
                textViewLine1 = itemView.findViewById(R.id.textViewLine1);
                textViewLine2 = itemView.findViewById(R.id.textViewLine2);
                copyIcon = itemView.findViewById(R.id.copyIcon);
                deleteIcon = itemView.findViewById(R.id.deleteIcon);
            }
        }
    }
}
