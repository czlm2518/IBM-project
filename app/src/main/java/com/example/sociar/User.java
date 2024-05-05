package com.example.sociar;

public class User {
    public float longitude, latitude;
    public String name;
    public String[] interests;

    public User(String name, float longitude, float latitude, String... interests) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.interests = interests;
    }

    public User(int name, float longitude, float latitude, String... interests) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = String.valueOf(name);
        this.interests = interests;
    }
}
