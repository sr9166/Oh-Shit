package com.example.leehyungyu.draw_example;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LeeHyunGyu on 2018-06-08.
 */

public class User implements Parcelable {
    String id;
    String name;
    String email;

    public User() {

    }

    public User(Parcel in) {readFromParcel(in);}

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(email);
    }

    private void readFromParcel(Parcel in) {
        id = in.readString();
        name = in.readString();
        email = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {return new User(in);}

        @Override
        public User[] newArray(int size) {return new User[size];}
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
