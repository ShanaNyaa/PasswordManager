package com.danial.passwordmanager;

public class UserModel {
    private String email;
    private boolean isEmailVerified;

    public UserModel(String email) {
        this.email = email;
        this.isEmailVerified = false;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }
}
