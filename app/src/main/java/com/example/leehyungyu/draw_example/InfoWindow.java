package com.example.leehyungyu.draw_example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by LeeHyunGyu on 2018-05-03.
 */

public class InfoWindow  {
    private Context context;
    private final View mWindow;



    public InfoWindow(Context ctx) {
        context = ctx;
        mWindow = LayoutInflater.from(ctx).inflate(R.layout.infowindow , null);
    }
    public Context getContext(){
        return context;
    }



}
