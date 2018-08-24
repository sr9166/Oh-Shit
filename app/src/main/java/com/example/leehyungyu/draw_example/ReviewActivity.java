package com.example.leehyungyu.draw_example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by LeeHyunGyu on 2018-06-09.
 */

public class ReviewActivity extends AppCompatActivity {
    private ArrayList<Review> reviewArrayList = null;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static String Log_Tag = "ReviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        User currentUser = null;

        Intent intent = getIntent();
        reviewArrayList = intent.getParcelableArrayListExtra("list");

        //recyclerView start
        recyclerView = (RecyclerView) findViewById(R.id.review_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ReviewAdapter(reviewArrayList);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Override
    protected void onResume() {
        super.onResume();
        ((ReviewAdapter) adapter).setOnItemClickListener(new ReviewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Toast.makeText(ReviewActivity.this, ""+reviewArrayList.get(position).getContent(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
