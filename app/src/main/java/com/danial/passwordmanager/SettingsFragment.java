package com.danial.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        LinearLayout updatePasswordLayout = rootView.findViewById(R.id.updatePasswordLayout);
        updatePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdatePasswordActivity();
            }
        });

        return rootView;
    }

    public void openUpdatePasswordActivity() {
        Intent intent = new Intent(getActivity(), UpdatePasswordActivity.class);
        startActivity(intent);
    }
}
