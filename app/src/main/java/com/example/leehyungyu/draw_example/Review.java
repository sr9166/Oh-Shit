package com.example.leehyungyu.draw_example;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LeeHyunGyu on 2018-06-09.
 */

public class Review implements Parcelable {
    String userid;
    String content;
    String rating;
    String time;

    public Review() {}

    public Review(Parcel in) {readFromParcel(in);}

    public Review(String userid, String content, String rating, String time) {
        this.userid = userid;
        this.content = content;
        this.rating = rating;
        this.time = time;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userid);
        dest.writeString(content);
        dest.writeString(rating);
        dest.writeString(time);
    }

    private void readFromParcel(Parcel in) {
        userid = in.readString();
        content = in.readString();
        rating = in.readString();
        time = in.readString();
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {return new Review(in);}

        @Override
        public Review[] newArray(int size) {return new Review[size];}
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
