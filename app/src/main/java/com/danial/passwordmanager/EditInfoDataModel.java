package com.danial.passwordmanager;

public class EditInfoDataModel {
    private String username;
    private String password;
    private String website;

    public EditInfoDataModel(String username, String password, String website) {
        this.username = username;
        this.password = password;
        this.website = website;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
