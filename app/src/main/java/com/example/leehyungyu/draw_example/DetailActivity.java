package com.example.leehyungyu.draw_example;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by LeeHyunGyu on 2018-05-11.
 */

public class DetailActivity extends AppCompatActivity{

    private ArrayList<Review> reviewArrayList = null;
    private DocumentBuilderFactory t_dbf = null;
    private DocumentBuilder t_db = null;
    private Document t_doc = null;
    private NodeList t_nodes = null;
    private Node t_node = null;
    private Element t_element = null;
    private InputSource t_is = null;

    private ServerThread serverThread;
    private String myResult;
    RatingBar rating;
    TextView tv_rating;
    EditText feedback;
    User currentUser;
    String starpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Publtolt publtolt = intent.getParcelableExtra("Publtolt");
        final String title = publtolt.getPBCTLT_PLC_NM();
        Double direction = publtolt.getDistance();
        String location = publtolt.getREFINE_ROADNM_ADDR();
        String call = publtolt.getMANAGE_INST_TELNO();
        String time = publtolt.getOPEN_TM_INFO();
        currentUser  = intent.getParcelableExtra("user");
        starpoint = intent.getStringExtra("starpoint");

        reviewArrayList = new ArrayList<>();
        serverThread = new ServerThread(serverHandler);
        serverThread.setDaemon(true);
        serverThread.start();

        try {
            t_dbf = DocumentBuilderFactory.newInstance();
            t_db = t_dbf.newDocumentBuilder();
            t_is = new InputSource();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }


        final TextView detail_title = (TextView) findViewById(R.id.detail_title);
        TextView detail_direction = (TextView) findViewById(R.id.detail_direction);
        TextView detail_location = (TextView) findViewById(R.id.detail_location);
        final TextView detail_call = (TextView) findViewById(R.id.detail_call);
        TextView detail_time = (TextView) findViewById(R.id.detail_time);
        TextView detail_review_tv = (TextView) findViewById(R.id.detail_review_tv);
        detail_review_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                    builder.setTitle("경고");
                    builder.setMessage("로그인을 먼저 해주세요")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                    builder.show();
                    return;
                }
                final RelativeLayout dlg_layout = (RelativeLayout) View.inflate(DetailActivity.this, R.layout.send_dialog, null);

                rating = (RatingBar) dlg_layout.findViewById(R.id.ratingBar1);
                tv_rating = (TextView) dlg_layout.findViewById(R.id.tv_rating);
                feedback = (EditText) dlg_layout.findViewById(R.id.feedback);
                rating.setStepSize((float) 0.5);        //별 색깔이 1칸씩줄어들고 늘어남 0.5로하면 반칸씩 들어감
                rating.setRating((float) 5.0);      // 처음보여줄때(색깔이 한개도없음) default 값이 0  이다
                rating.setIsIndicator(false);           //true - 별점만 표시 사용자가 변경 불가 , false - 사용자가 변경가능
                rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating,
                                                boolean fromUser) {
                        tv_rating.setText( "" + rating);
                    }
                });


                AlertDialog.Builder dlg = new AlertDialog.Builder(DetailActivity.this);
                dlg.setView(dlg_layout);
                dlg.setCancelable(false);

                dlg.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("SEND_REVIEW",currentUser.getId());
                        Log.d("SEND_REVIEW",title);
                        Log.d("SEND_REVIEW",String.valueOf(rating.getRating()));
                        Log.d("SEND_REVIEW",feedback.getText().toString());
                        serverThread.setAddReview(currentUser.getId() , title , String.valueOf(rating.getRating()) , feedback.getText().toString() , myResult);
                        serverThread.getFgHandler().sendEmptyMessage(3);

                    }
                });
                dlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),
                                "Cancel", Toast.LENGTH_SHORT).show();
                    }
                });

                // Showing Alert Message
                dlg.show();
            }
        });

        detail_title.setText(title);
        detail_location.setText(location);
        if(call.equals(""))
            detail_call.setText("NULL");
        else
            detail_call.setText(call);
        detail_time.setText(time);
        if(direction > 1000) {
            direction = Math.round(direction / 10.0) / 100.0;
            detail_direction.setText("" + direction + "km");
        } else
            detail_direction.setText("" + direction + "m");

        TextView detail_rating = findViewById(R.id.detail_rating);
        double temp;
        if(starpoint != null) {
            temp = Float.valueOf(starpoint);
            temp = Math.round(temp * 10.0) / 10.0;
            detail_rating.setText(String.valueOf(temp));
        } else
            detail_rating.setText("5.0");

        Button btn_review = (Button) findViewById(R.id.btn_review);
        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DETAIL_ACTIVITY","CLICK BTN");
                serverThread.setGetReview(title);
                serverThread.getFgHandler().sendEmptyMessage(4);
            }
        });
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

    private Handler serverHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1) {
                Toast.makeText(DetailActivity.this, "Your Review sent Successfully", Toast.LENGTH_SHORT).show();
            }
            else if (msg.what == 2) {
                reviewArrayList.clear();
                Log.d("DETAIL_ACTIVITY",msg.obj.toString().trim());
                if(msg.obj.toString().trim().equals("<reviews />")) {
                    Toast.makeText(DetailActivity.this, "reviews are empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    t_is.setCharacterStream(new StringReader(msg.obj.toString().trim()));
                    try {
                        t_doc = t_db.parse(t_is);
                        t_nodes = t_doc.getElementsByTagName("review"); // review
                        for (int i = 0; i < t_nodes.getLength(); i++) {
                            t_node = t_nodes.item(i);
                            t_element = (Element) t_node;

                            String review = t_element.getElementsByTagName("REVIEW").item(0).getTextContent();
                            String userid = t_element.getElementsByTagName("USER").item(0).getTextContent();
                            String star_point = t_element.getElementsByTagName("STAR_POINT").item(0).getTextContent();
                            String time = t_element.getElementsByTagName("TIME").item(0).getTextContent();
                            Log.d("DETAIL_ACTIVITY", userid);
                            Log.d("DETAIL_ACTIVITY", review);
                            Log.d("DETAIL_ACTIVITY", star_point);
                            Log.d("DETAIL_ACTIVITY", time);

                            reviewArrayList.add(new Review(userid, review, star_point, time));
                        }

                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("THREAD_ERR", "handler_error1");
                }
                Intent intent1 = new Intent(DetailActivity.this, ReviewActivity.class);
                intent1.putParcelableArrayListExtra("list",reviewArrayList);
                startActivity(intent1);
            }
        }
    };
}
