package com.chad.videochatapp.Models;

import java.io.Serializable;

public class User implements Serializable {
    public String firstName, profileImage, lastName, email, token;

    public User() {}

    public User(String firstName, String profileImage, String lastName, String email, String token) {
        this.firstName = firstName;
        this.profileImage = profileImage;
        this.lastName = lastName;
        this.email = email;
        this.token = token;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
