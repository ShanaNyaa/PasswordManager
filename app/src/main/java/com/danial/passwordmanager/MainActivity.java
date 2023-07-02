package com.danial.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.*;

public class MainActivity extends AppCompatActivity {

    private String userEmail;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
        }

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(getColorStateList(R.color.white));
        FirebaseManager.setUserEmailInNavigationHeader(this, userEmail);
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        Menu navMenu = navigationView.getMenu();
        navMenu.findItem(R.id.menuLogout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FirebaseManager.logout(MainActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Go back to login activity
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        Toast.makeText(MainActivity.this, "Logout successful.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Logout failed.", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        });

    }
}