package com.example.leehyungyu.draw_example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by LeeHyunGyu on 2018-06-09.
 */

public class ProfileAcitivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String userid = intent.getStringExtra("userid");
        String username = intent.getStringExtra("username");
        String useremail = intent.getStringExtra("useremail");

        TextView profile_id = (TextView) findViewById(R.id.profile_id);
        TextView profile_name = (TextView) findViewById(R.id.profile_name);
        TextView profile_email = (TextView) findViewById(R.id.profile_email);

        profile_id.setText(userid);
        profile_name.setText(username);
        profile_email.setText(useremail);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
