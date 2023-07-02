package com.danial.passwordmanager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Random;

public class PasswordGeneratorFragment extends Fragment {

    private Switch digitsSwitch;
    private Switch lettersSwitch;
    private Switch symbolsSwitch;
    private SeekBar lengthSeekBar;
    private TextView lengthValueTextView;
    private TextView generatedPasswordTextView;
    private Button copyButton;

    private Random random;

    private ClipboardManager clipboardManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_password_generator, container, false);

        digitsSwitch = rootView.findViewById(R.id.digitsSwitch);
        lettersSwitch = rootView.findViewById(R.id.lettersSwitch);
        symbolsSwitch = rootView.findViewById(R.id.symbolsSwitch);
        lengthSeekBar = rootView.findViewById(R.id.lengthSeekBar);
        lengthValueTextView = rootView.findViewById(R.id.lengthValueTextView);
        generatedPasswordTextView = rootView.findViewById(R.id.generatedPasswordTextView);
        copyButton = rootView.findViewById(R.id.copyButton);

        random = new Random();

        lengthSeekBar.setMax(36); // Maximum password length

        lengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int passwordLength = progress + 4; // Minimum password length is 4
                lengthValueTextView.setText(String.valueOf(passwordLength));
                generatePassword();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        digitsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                generatePassword();
            }
        });

        lettersSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                generatePassword();
            }
        });

        symbolsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                generatePassword();
            }
        });

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyPasswordToClipboard();
            }
        });

        ImageView refreshIcon = rootView.findViewById(R.id.refreshIcon);
        refreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePassword();
            }
        });

        clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        generatePassword();

        return rootView;
    }

    private void generatePassword() {
        StringBuilder passwordBuilder = new StringBuilder();

        int passwordLength = lengthSeekBar.getProgress() + 4; // Minimum password length is 4

        boolean hasDigits = digitsSwitch.isChecked();
        boolean hasLetters = lettersSwitch.isChecked();
        boolean hasSymbols = symbolsSwitch.isChecked();

        if (!hasDigits && !hasLetters && !hasSymbols) {
            digitsSwitch.setChecked(true); // Toggle on digits if neither digits, letters, nor symbols is selected
            hasDigits = true;
        }

        String characterSet = "";
        if (hasDigits) {
            characterSet += "0123456789";
        }
        if (hasLetters) {
            characterSet += "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }
        if (hasSymbols) {
            characterSet += "!@#$%^&*()-_=+[{]};:'\"<>,.?/";
        }

        while (passwordBuilder.length() < passwordLength) {
            char randomChar = characterSet.charAt(random.nextInt(characterSet.length()));
            passwordBuilder.append(randomChar);
        }

        String generatedPassword = passwordBuilder.toString();
        generatedPasswordTextView.setText(generatedPassword);
    }


    private void copyPasswordToClipboard() {
        String generatedPassword = generatedPasswordTextView.getText().toString();
        if (!TextUtils.isEmpty(generatedPassword)) {
            ClipData clipData = ClipData.newPlainText("Generated Password", generatedPassword);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getActivity(), "Password copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }
}
