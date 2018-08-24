package com.example.leehyungyu.draw_example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by LeeHyunGyu on 2018-05-03.
 */

public class ListActivity extends AppCompatActivity {

    private ArrayList<Publtolt> publtoltArrayList = null;
    // private test
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static String Log_Tag = "ListActivity";
    User currentUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);


        Intent intent = getIntent();
        publtoltArrayList = intent.getParcelableArrayListExtra("list");
        currentUser = intent.getParcelableExtra("user");
        DistanceCMP distanceCMP = new DistanceCMP();
        Collections.sort(publtoltArrayList,distanceCMP);
        //recyclerView start
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyRecyclerViewAdapter(publtoltArrayList);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
        protected void onResume() {
            super.onResume();
            ((MyRecyclerViewAdapter) adapter).setOnItemClickListener(new MyRecyclerViewAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Intent intent = new Intent(ListActivity.this, DetailActivity.class);
                    intent.putExtra("Publtolt",publtoltArrayList.get(position));
                    intent.putExtra("user",currentUser);
                    Double distance = publtoltArrayList.get(position).getDistance();
                    if(distance > 1000) {
                        distance = Math.round(distance / 10.0) / 100.0;
                        intent.putExtra("distance", "Distance : " + distance + "km");
                    } else {
                        intent.putExtra("distance", "Distance : " + distance + "m");
                    }
                    startActivity(intent);
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_distance) {
            DistanceCMP distanceCMP = new DistanceCMP();
            Collections.sort(publtoltArrayList,distanceCMP);
            adapter = new MyRecyclerViewAdapter(publtoltArrayList);
            recyclerView.setAdapter(adapter);
            return true;
        } else if (id == R.id.action_rating) {
            Toast.makeText(this, "Rating", Toast.LENGTH_SHORT).show();
            return true;
        } else if(id == R.id.action_name) {
            NameCMP nameCMP = new NameCMP();
            Collections.sort(publtoltArrayList,nameCMP);
            adapter = new MyRecyclerViewAdapter(publtoltArrayList);
            recyclerView.setAdapter(adapter);
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
