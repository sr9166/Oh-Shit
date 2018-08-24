package com.example.leehyungyu.draw_example;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import su.levenetc.android.textsurface.TextSurface;

/**
 * Created by LeeHyunGyu on 2018-06-07.
 */

public class CreditActivity extends AppCompatActivity {
    private TextSurface textSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);
        textSurface = (TextSurface) findViewById(R.id.text_surface);

        textSurface.postDelayed(new Runnable() {
            @Override public void run() {
                CreditText.play(textSurface);
                Handler handler = new Handler();
                handler.postDelayed(new creditHandler(),7300);
            }
        }, 1000);
    }

    private class creditHandler implements  Runnable{

        @Override
        public void run() {
            CreditActivity.this.finish();
        }
    }
}