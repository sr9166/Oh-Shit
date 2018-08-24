package com.example.leehyungyu.draw_example;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int REQUEST_LOGIN = 2003;
    private GoogleMap googleMap = null; // 구글맵
    private Marker currentMarker = null; // 지정 위치 마커
    private Marker BeforeMarker = null;
    private int radius = 1000;   // 반경 원 크기
    private Double CurrentLat;
    private Double CurrentLng;
    private PathThread pathThread;
    private Location CurrentLoc;

    private String locInfo1 = null; // 현재위치 위도 저장값
    private String locInfo2 = null; // 현재위치 경도 저장값
    private APIThread apiThread; // api thread 생성
    private ServerThread serverThread;
    private ArrayList<Marker> previous_marker = null;
    private HashMap<String,String> starpointList = null;
    private DocumentBuilderFactory t_dbf = null;
    private DocumentBuilder t_db = null;
    private Document t_doc = null;
    private NodeList t_nodes = null;
    private Node t_node = null;
    private Element t_element = null;
    private InputSource t_is = null;

    private ArrayList<Publtolt> publtoltArrayList = null;
    private Animation fab_open, fab_close, fab_rotate_open, fab_rotate_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private TextView fatv1, fatv2;

    private ArrayList<LatLng> mapPoints = null;
    private ArrayList<TrackModel> trackModels = null;
    private int totalDistance;
    private Publtolt CurrentPubltolt = null;
    private Polyline polyline = null;
    private Circle circle;
    private Button infoButton1, infoButton2;
    private ViewGroup infoWindow;
    private OnInfoWindowElemTouchListener infoButtonListener, infobuttonlistener2;
    private  MapWrapperLayout mapWrapperLayout;

    private Button Login_Button;

    private User currentUser;


    //RatingBar initiate
    RatingBar rating;
    TextView tv_rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // git test here!
        try {
            t_dbf = DocumentBuilderFactory.newInstance();
            t_db = t_dbf.newDocumentBuilder();
            t_is = new InputSource();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        apiThread = new APIThread(mHandler);
        apiThread.setDaemon(true);
        apiThread.start();
        serverThread = new ServerThread(serverHandler);
        serverThread.setDaemon(true);
        serverThread.start();

        previous_marker = new ArrayList<>();
        starpointList = new HashMap<>();
        mapPoints = new ArrayList<>();
        trackModels = new ArrayList<>();

        pathThread = new PathThread(pathHandler);
        pathThread.setDaemon(true);
        pathThread.start();

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_rotate_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_open);
        fab_rotate_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_close);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fatv1 = (TextView) findViewById(R.id.fatv1);
        fatv2 = (TextView) findViewById(R.id.fatv2);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        Login_Button = (Button) findViewById(R.id.Login_Button);
        Login_Button.setOnClickListener(this);
        Login_Button.setText("LOGIN");

        GetMyGPS();
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.fab)
            anim();
        if(id == R.id.fab1) {
            anim();
            Message msg = new Message();
            msg.what = 1;
            publtoltArrayList = new ArrayList<>();
            apiThread.getFgHandler().sendMessage(msg);

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("LOADING...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }
        if(id == R.id.fab2) {
            anim();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final EditText editText = new EditText(MainActivity.this);
            builder.setTitle("도시별 검색");
            builder.setMessage("시를 정확히 입력해주세요\nEx) 수원시")
                    .setView(editText)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(editText.getText().toString().trim().length() == 0) {
                                        dialog.cancel();
                                        Toast.makeText(MainActivity.this, "empty", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Message msg = new Message();
                                        msg.obj = editText.getText().toString();
                                        msg.what = 2;
                                        publtoltArrayList = new ArrayList<Publtolt>();
                                        apiThread.getFgHandler().sendMessage(msg);
                                    }
                                }
                            })
                    .setNegativeButton("CANCEL",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
            builder.show();
        }

        if(id == R.id.Login_Button) {
            if(Login_Button.getText() == "LOGIN") {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent,REQUEST_LOGIN);
            }
            else {
                Login_Button.setText("LOGIN");
                currentUser = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Success!");
                builder.setMessage("로그아웃 성공")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                builder.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful login logic here

                String userid = data.getStringExtra("userid");
                String username = data.getStringExtra("username");
                String useremail = data.getStringExtra("useremail");
                currentUser = new User(userid,username,useremail);
                Login_Button.setText("LOGOUT");

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Success!");
                builder.setMessage("로그인 성공" + "\n" + username + "님 환영합니다.")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                builder.show();
            }
        }
    }

    public void anim() {
        if(isFabOpen) {
            fab.startAnimation(fab_rotate_close);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fatv1.startAnimation(fab_close);
            fatv2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab.startAnimation(fab_rotate_open);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fatv1.startAnimation(fab_open);
            fatv2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_howtouse) {
            Toast.makeText(this, "How To Use", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_notice) {
            Toast.makeText(this, "Notice", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
            if(currentUser == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            }
            else {
                Intent intent = new Intent(MainActivity.this, ProfileAcitivity.class);
                intent.putExtra("userid",currentUser.getId());
                intent.putExtra("username",currentUser.getName());
                intent.putExtra("useremail",currentUser.getEmail());
                startActivity(intent);
            }
        } else if (id == R.id.nav_listview) {

            if(publtoltArrayList == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("경고");
                builder.setMessage("화장실 검색을 먼저 해주세요")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                builder.show();
            }
            else {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putParcelableArrayListExtra("list", publtoltArrayList);
                intent.putExtra("user",currentUser);
                startActivity(intent);
            }
        } else if (id == R.id.nav_radius) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final EditText editText = new EditText(MainActivity.this);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setTitle("반경 설정");
            builder.setMessage("기본 반경원 크기는 1km 입니다.\nEX) 500 입력시 500m")
                    .setView(editText)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(editText.getText().toString().trim().length() == 0) {
                                        dialog.cancel();
                                        Toast.makeText(MainActivity.this, "empty", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(Double.parseDouble(editText.getText().toString()) > 10000 )  {
                                        dialog.cancel();
                                        Toast.makeText(MainActivity.this, "Out of limits", Toast.LENGTH_SHORT).show();
                                    } else {
                                        radius = Integer.parseInt(editText.getText().toString());
                                        circle.setRadius(radius);

                                        if(publtoltArrayList==null) {
                                            return;
                                        }
                                        if (previous_marker != null) {
                                            for (int i = 0; i < previous_marker.size(); i++) {
                                                previous_marker.get(i).remove();
                                            }
                                            previous_marker.clear();
                                        }
                                        for(int i = 0 ; i < publtoltArrayList.size(); i++) {
                                            Publtolt publtolt_temp = publtoltArrayList.get(i);
                                            if(publtolt_temp.getDistance() < radius) {

                                                MarkerOptions markerOptions2 = new MarkerOptions();
                                                LatLng latLng = new LatLng(publtolt_temp.getREFINE_WGS84_LAT(),publtolt_temp.getREFINE_WGS84_LOGT());
                                                markerOptions2.position(latLng)
                                                        .title(publtolt_temp.getPBCTLT_PLC_NM())
                                                        .snippet(publtolt_temp.getREFINE_ROADNM_ADDR())
                                                        .icon(BitmapDescriptorFactory.defaultMarker(204f));

                                                InfoWindowData info = new InfoWindowData();
                                                info.setImage("@drawable/ic_wc_black_48dp");
                                                Double distance = publtolt_temp.getDistance();
                                                if(distance > 1000) {
                                                    distance = Math.round(distance / 10.0) / 100.0;
                                                    info.setHotel("Distance : " + distance + "km");
                                                } else
                                                    info.setHotel("Distance : " + distance + "m");

                                                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                                    @Override
                                                    public View getInfoWindow(Marker marker) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public View getInfoContents(Marker marker) {
                                                        infoButtonListener.setMarker(marker);
                                                        infobuttonlistener2.setMarker(marker);

                                                        infoWindow.setLayoutParams(new RelativeLayout.LayoutParams(900,RelativeLayout.LayoutParams.WRAP_CONTENT));


                                                        TextView name_tv = infoWindow.findViewById(R.id.name);
                                                        TextView details_tv = infoWindow.findViewById(R.id.details);
                                                        // ImageView img = view.findViewById(R.id.pic);

                                                        TextView hotel_tv = infoWindow.findViewById(R.id.hotels);
                                                        TextView food_tv = infoWindow.findViewById(R.id.food);
                                                        TextView transport_tv = infoWindow.findViewById(R.id.transport);

                                                        name_tv.setText(marker.getTitle());
                                                        details_tv.setText(marker.getSnippet());

                                                        InfoWindowData infoWindowData = new InfoWindowData();
                                                        Publtolt newone = new Publtolt();
                                                        infoWindowData.setImage("@drawable/ic_wc_black_48dp");

                                                        RatingBar ratBar = (RatingBar) infoWindow.findViewById(R.id.ratingBar);
                                                        ratBar.setRating((float)2.5);
                                                        TextView tv = (TextView) infoWindow.findViewById(R.id.tv);
                                                        tv.setText("2.5");

                                                        newone = (Publtolt)marker.getTag();

                                                        if(newone.getMANAGE_INST_TELNO().equals(""))
                                                        {
                                                            ImageView v = infoWindow.findViewById(R.id.telephone);
                                                            v.setVisibility(View.INVISIBLE);
                                                        }
                                                        else
                                                        {
                                                            infoWindow.setVisibility(View.VISIBLE);
                                                        }
                                                        double distance = newone.getDistance();
                                                        if(distance > 1000) {
                                                            distance = Math.round(distance / 10.0) / 100.0;
                                                            infoWindowData.setHotel("Distance : " + distance + "km");
                                                        } else
                                                            infoWindowData.setHotel("Distance : " + distance + "m");



                                                        int imageId = MainActivity.this.getResources().getIdentifier(infoWindowData.getImage().toLowerCase(),"drawable", MainActivity.this.getPackageName());
                                                        //  img.setImageResource(imageId);

                                                        infoWindowData.setFood(newone.getOPEN_TM_INFO());
                                                        infoWindowData.setTransport(newone.getMANAGE_INST_TELNO());



                                                        hotel_tv.setText(infoWindowData.getHotel());
                                                        food_tv.setText(infoWindowData.getFood());
                                                        transport_tv.setText(infoWindowData.getTransport());

                                                        mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);

                                                        return infoWindow;
                                                    }
                                                });

                                                Marker m = googleMap.addMarker(markerOptions2);
                                                m.setTag(publtolt_temp);
                                                previous_marker.add(m);
                                            }
                                        }
                                        if(previous_marker.size()==0)
                                            Toast.makeText(MainActivity.this, "반경안에 화장실이 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                    .setNegativeButton("CANCEL",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
            builder.show();
        } else if (id == R.id.nav_mapstyle) {
            final String [] items = {"none", "normal", "satellite", "terrain", "hybrid"};
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Select Google Map Type");
            builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(MainActivity.this, items[which], Toast.LENGTH_SHORT).show();
                    switch (which) {
                        case 0 :
                            googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                            break;
                        case 1:
                            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            break;
                        case 2:
                            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            break;
                        case 3:
                            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            break;
                        case 4:
                            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            break;
                    }
                    dialog.dismiss();
                }

            });
            builder.show();

        } else if (id == R.id.nav_credit) {
            Intent intent = new Intent(MainActivity.this,CreditActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            String templateId = "10157";

            KakaoLinkService.getInstance().sendCustom(this, templateId, null, new ResponseCallback<KakaoLinkResponse>() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    Logger.e(errorResult.toString());
                }

                @Override
                public void onSuccess(KakaoLinkResponse result) {
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다.
                }
            });
        } else if (id == R.id.nav_send) {
            final RelativeLayout dlg_layout = (RelativeLayout) View.inflate(this, R.layout.send_dialog, null);

            rating = (RatingBar) dlg_layout.findViewById(R.id.ratingBar1);
            tv_rating = (TextView) dlg_layout.findViewById(R.id.tv_rating);
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


            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
//        dlg.setTitle();
            dlg.setView(dlg_layout);
            dlg.setCancelable(false);

            dlg.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getApplicationContext(),
                            "Your Feedback sent Successfully", Toast.LENGTH_SHORT).show();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        drawer.closeDrawers();
        return true;
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;
        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative);
        googleMap.getUiSettings().setCompassEnabled(true); // 나침반 설정
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Publtolt publtolt = (Publtolt) marker.getTag();
                intent.putExtra("Publtolt",publtolt);
                intent.putExtra("user",currentUser);
                intent.putExtra("starpoint",starpointList.get(publtolt.getPBCTLT_PLC_NM()));
                startActivity(intent);
            }
        });
        setCurrentLoc(CurrentLoc);

        mapWrapperLayout.init(googleMap, getPixelsFromDp(this, 39+20));

        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.infowindow,null);
        this.infoButton1 = (Button)infoWindow.findViewById(R.id.button2);
        this.infoButton2 = (Button)infoWindow.findViewById(R.id.road_research);

        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) { // 권한 있는 경우

            googleMap.setMyLocationEnabled(true);

            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() { // 현재 위치 버튼 클릭 시
                @Override
                public boolean onMyLocationButtonClick() {
                    BeforeMarker = null;
                    Location location = googleMap.getMyLocation();
                    setCurrentLoc(location);


                    LatLng latLng = new LatLng(currentMarker.getPosition().latitude,
                            currentMarker.getPosition().longitude
                    );

                    String locInfo = getAddress(latLng); // 현재 마커의 주소를 가져옴
                    String[] split = locInfo.split(" ");

                    // 도,시 별 주소 저장
                    locInfo1 = split[1];
                    locInfo2 = split[2];
                    Log.d("LOC_INFO", locInfo1 + " " + locInfo2);

                    if(publtoltArrayList==null) {
                        return true;
                    }
                    for(int i = 0; i < publtoltArrayList.size(); i++) {

                        Double distance_temp, lat_temp, lng_temp;
                        Publtolt publtolt_temp = publtoltArrayList.get(i);
                        Log.d("DRAGMARKER",publtolt_temp.getREFINE_WGS84_LAT().toString());
                        lat_temp = publtolt_temp.getREFINE_WGS84_LAT();
                        lng_temp = publtolt_temp.getREFINE_WGS84_LOGT();
                        distance_temp = Math.round(calDistance(CurrentLat,CurrentLng,lat_temp,lng_temp)*100)/100.0;
                        publtolt_temp.setDistance(distance_temp);
                    }
                    if (previous_marker != null) {
                        for (int i = 0; i < previous_marker.size(); i++) {
                            previous_marker.get(i).remove();
                        }
                        previous_marker.clear();
                    }
                    for(int i = 0 ; i < publtoltArrayList.size(); i++) {
                        Publtolt publtolt_temp = publtoltArrayList.get(i);
                        if(publtolt_temp.getDistance() < radius) {

                            MarkerOptions markerOptions1 = new MarkerOptions();
                            LatLng latLng2 = new LatLng(publtolt_temp.getREFINE_WGS84_LAT(),publtolt_temp.getREFINE_WGS84_LOGT());
                            markerOptions1.position(latLng2)
                                    .title(publtolt_temp.getPBCTLT_PLC_NM())
                                    .snippet(publtolt_temp.getREFINE_ROADNM_ADDR())
                                    .icon(BitmapDescriptorFactory.defaultMarker(204f));

                            InfoWindowData info = new InfoWindowData();
                            info.setImage("@drawable/ic_wc_black_48dp");
                            Double distance = publtolt_temp.getDistance();
                            if(distance > 1000) {
                                distance = Math.round(distance / 10.0) / 100.0;
                                info.setHotel("Distance : " + distance + "km");
                            } else
                                info.setHotel("Distance : " + distance + "m");

                            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                @Override
                                public View getInfoWindow(Marker marker) {
                                    return null;
                                }

                                @Override
                                public View getInfoContents(Marker marker) {
                                    infoButtonListener.setMarker(marker);
                                    infobuttonlistener2.setMarker(marker);

                                    infoWindow.setLayoutParams(new RelativeLayout.LayoutParams(900,RelativeLayout.LayoutParams.WRAP_CONTENT));


                                    TextView name_tv = infoWindow.findViewById(R.id.name);
                                    TextView details_tv = infoWindow.findViewById(R.id.details);
                                    // ImageView img = view.findViewById(R.id.pic);

                                    TextView hotel_tv = infoWindow.findViewById(R.id.hotels);
                                    TextView food_tv = infoWindow.findViewById(R.id.food);
                                    TextView transport_tv = infoWindow.findViewById(R.id.transport);

                                    name_tv.setText(marker.getTitle());
                                    details_tv.setText(marker.getSnippet());

                                    InfoWindowData infoWindowData = new InfoWindowData();
                                    Publtolt newone = new Publtolt();
                                    infoWindowData.setImage("@drawable/ic_wc_black_48dp");

                                    RatingBar ratBar = (RatingBar) infoWindow.findViewById(R.id.ratingBar);
                                    ratBar.setRating((float)2.5);
                                    TextView tv = (TextView) infoWindow.findViewById(R.id.tv);
                                    tv.setText("2.5");

                                    newone = (Publtolt)marker.getTag();

                                    if(newone.getMANAGE_INST_TELNO().equals(""))
                                    {
                                        ImageView v = infoWindow.findViewById(R.id.telephone);
                                        v.setVisibility(View.INVISIBLE);
                                    }
                                    else
                                    {
                                        infoWindow.setVisibility(View.VISIBLE);
                                    }
                                    double distance = newone.getDistance();
                                    if(distance > 1000) {
                                        distance = Math.round(distance / 10.0) / 100.0;
                                        infoWindowData.setHotel("Distance : " + distance + "km");
                                    } else
                                        infoWindowData.setHotel("Distance : " + distance + "m");



                                    int imageId = MainActivity.this.getResources().getIdentifier(infoWindowData.getImage().toLowerCase(),"drawable", MainActivity.this.getPackageName());
                                    //  img.setImageResource(imageId);

                                    infoWindowData.setFood(newone.getOPEN_TM_INFO());
                                    infoWindowData.setTransport(newone.getMANAGE_INST_TELNO());



                                    hotel_tv.setText(infoWindowData.getHotel());
                                    food_tv.setText(infoWindowData.getFood());
                                    transport_tv.setText(infoWindowData.getTransport());

                                    mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);

                                    return infoWindow;
                                }
                            });
                            Marker m = googleMap.addMarker(markerOptions1);
                            m.setTag(publtolt_temp);
                            previous_marker.add(m);
                        }
                    }
                    if(previous_marker.size()==0)
                        Toast.makeText(MainActivity.this, "반경안에 화장실이 없습니다.", Toast.LENGTH_SHORT).show();

                    return true;
                }
            });

            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Log.d("LOC","MY_LOC");
                    currentMarker.remove();
                    BeforeMarker = null;
                    LatLng currentLatLng = marker.getPosition();
                    CurrentLng = currentLatLng.longitude;
                    CurrentLat = currentLatLng.latitude;

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(currentLatLng);
                    markerOptions.title("내 위치");
                    markerOptions.snippet(getAddress(currentLatLng));
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    markerOptions.draggable(true);

                    currentMarker = googleMap.addMarker(markerOptions);
                    currentMarker.setTag(new Publtolt("기준 위치",getAddress(currentLatLng),CurrentLat,CurrentLng,0.0,"010 - 1234 - 5678", "Mon - Fri, 9AM - 6PM"));

                    if(circle != null)
                        circle.remove();
                    circle = googleMap.addCircle(new CircleOptions()
                            .center(currentLatLng)
                            .radius(radius)
                            .strokeColor(Color.parseColor("#884169e1"))
                            .fillColor(Color.parseColor("#5587cefa")));

                    if(publtoltArrayList==null) {
                        return;
                    }
                    for(int i = 0; i < publtoltArrayList.size(); i++) {

                        Double distance_temp, lat_temp, lng_temp;
                        Publtolt publtolt_temp = publtoltArrayList.get(i);
                        Log.d("DRAGMARKER",publtolt_temp.getREFINE_WGS84_LAT().toString());
                        lat_temp = publtolt_temp.getREFINE_WGS84_LAT();
                        lng_temp = publtolt_temp.getREFINE_WGS84_LOGT();
                        distance_temp = Math.round(calDistance(CurrentLat,CurrentLng,lat_temp,lng_temp)*100)/100.0;
                        publtolt_temp.setDistance(distance_temp);
                    }
                    if (previous_marker != null) {
                        for (int i = 0; i < previous_marker.size(); i++) {
                            previous_marker.get(i).remove();
                        }
                        previous_marker.clear();
                    }
                    for(int i = 0 ; i < publtoltArrayList.size(); i++) {
                        Publtolt publtolt_temp = publtoltArrayList.get(i);
                        if(publtolt_temp.getDistance() < radius) {

                            MarkerOptions markerOptions1 = new MarkerOptions();
                            LatLng latLng = new LatLng(publtolt_temp.getREFINE_WGS84_LAT(),publtolt_temp.getREFINE_WGS84_LOGT());
                            markerOptions1.position(latLng)
                                    .title(publtolt_temp.getPBCTLT_PLC_NM())
                                    .snippet(publtolt_temp.getREFINE_ROADNM_ADDR())
                                    .icon(BitmapDescriptorFactory.defaultMarker(204f));

                            InfoWindowData info = new InfoWindowData();
                            info.setImage("@drawable/ic_wc_black_48dp");
                            Double distance = publtolt_temp.getDistance();
                            if(distance > 1000) {
                                distance = Math.round(distance / 10.0) / 100.0;
                                info.setHotel("Distance : " + distance + "km");
                            } else
                                info.setHotel("Distance : " + distance + "m");

                            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                @Override
                                public View getInfoWindow(Marker marker) {
                                    return null;
                                }

                                @Override
                                public View getInfoContents(Marker marker) {
                                    infoButtonListener.setMarker(marker);
                                    infobuttonlistener2.setMarker(marker);

                                    infoWindow.setLayoutParams(new RelativeLayout.LayoutParams(900,RelativeLayout.LayoutParams.WRAP_CONTENT));


                                    TextView name_tv = infoWindow.findViewById(R.id.name);
                                    TextView details_tv = infoWindow.findViewById(R.id.details);
                                    // ImageView img = view.findViewById(R.id.pic);

                                    TextView hotel_tv = infoWindow.findViewById(R.id.hotels);
                                    TextView food_tv = infoWindow.findViewById(R.id.food);
                                    TextView transport_tv = infoWindow.findViewById(R.id.transport);

                                    name_tv.setText(marker.getTitle());
                                    details_tv.setText(marker.getSnippet());

                                    InfoWindowData infoWindowData = new InfoWindowData();
                                    Publtolt newone = new Publtolt();
                                    infoWindowData.setImage("@drawable/ic_wc_black_48dp");

                                    RatingBar ratBar = (RatingBar) infoWindow.findViewById(R.id.ratingBar);
                                    ratBar.setRating((float)2.5);
                                    TextView tv = (TextView) infoWindow.findViewById(R.id.tv);
                                    tv.setText("2.5");

                                    newone = (Publtolt)marker.getTag();

                                    if(newone.getMANAGE_INST_TELNO().equals(""))
                                    {
                                        ImageView v = infoWindow.findViewById(R.id.telephone);
                                        v.setVisibility(View.INVISIBLE);
                                    }
                                    else
                                    {
                                        infoWindow.setVisibility(View.VISIBLE);
                                    }
                                    double distance = newone.getDistance();
                                    if(distance > 1000) {
                                        distance = Math.round(distance / 10.0) / 100.0;
                                        infoWindowData.setHotel("Distance : " + distance + "km");
                                    } else
                                        infoWindowData.setHotel("Distance : " + distance + "m");



                                    int imageId = MainActivity.this.getResources().getIdentifier(infoWindowData.getImage().toLowerCase(),"drawable", MainActivity.this.getPackageName());
                                    //  img.setImageResource(imageId);

                                    infoWindowData.setFood(newone.getOPEN_TM_INFO());
                                    infoWindowData.setTransport(newone.getMANAGE_INST_TELNO());



                                    hotel_tv.setText(infoWindowData.getHotel());
                                    food_tv.setText(infoWindowData.getFood());
                                    transport_tv.setText(infoWindowData.getTransport());

                                    mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);

                                    return infoWindow;
                                }
                            });
                            Marker m = googleMap.addMarker(markerOptions1);
                            m.setTag(publtolt_temp);
                            previous_marker.add(m);
                        }
                    }
                    if(previous_marker.size()==0)
                        Toast.makeText(MainActivity.this, "반경안에 화장실이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            });

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() { // 맵 클릭시
                @Override
                public void onMapClick(LatLng latLng) {

//                    currentMarker.remove();
//                    MarkerOptions markerOptions = new MarkerOptions();
//
//                    markerOptions.position(latLng);
//                    markerOptions.title("기준 위치");
//                    markerOptions.snippet(getAddress(latLng));
//                    markerOptions.draggable(true);
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//
//                    currentMarker = googleMap.addMarker(markerOptions);
                    if(BeforeMarker != null) {
                        if (BeforeMarker.getTitle().equals(currentMarker.getTitle()))
                            BeforeMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                        else
                            BeforeMarker.setIcon(BitmapDescriptorFactory.defaultMarker(204f));
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        Location myloc = googleMap.getMyLocation();
        setCurrentLoc(myloc);
        googleMap.setOnMarkerClickListener(this);

        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton1) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Log.d("log", "buttonclick");
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Publtolt temp = (Publtolt) marker.getTag();
                intent.putExtra("Publtolt",temp);
                intent.putExtra("user",currentUser);
                intent.putExtra("starpoint",starpointList.get(temp.getPBCTLT_PLC_NM()));
                startActivity(intent);
            }
        };
        this.infoButton1.setOnTouchListener(infoButtonListener);

        this.infobuttonlistener2 = new OnInfoWindowElemTouchListener(infoButton2) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Log.d("log", "길찾기 구현 버튼");
                pathThread.initiate(CurrentLng.toString(),CurrentLat.toString(),String.valueOf(marker.getPosition().longitude),String.valueOf(marker.getPosition().latitude),marker.getTitle());
                pathThread.getFgHandler().sendEmptyMessage(0);
            }
        };
        this.infoButton2.setOnTouchListener(infobuttonlistener2);
        this.googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                infoButtonListener.setMarker(marker);
                infobuttonlistener2.setMarker(marker);

                infoWindow.setLayoutParams(new RelativeLayout.LayoutParams(900,RelativeLayout.LayoutParams.WRAP_CONTENT));


                TextView name_tv = infoWindow.findViewById(R.id.name);
                TextView details_tv = infoWindow.findViewById(R.id.details);
                // ImageView img = view.findViewById(R.id.pic);

                TextView hotel_tv = infoWindow.findViewById(R.id.hotels);
                TextView food_tv = infoWindow.findViewById(R.id.food);
                TextView transport_tv = infoWindow.findViewById(R.id.transport);

                name_tv.setText(marker.getTitle());
                details_tv.setText(marker.getSnippet());

                InfoWindowData infoWindowData = new InfoWindowData();
                Publtolt newone = new Publtolt();
                infoWindowData.setImage("@drawable/ic_wc_black_48dp");

                RatingBar ratBar = (RatingBar) infoWindow.findViewById(R.id.ratingBar);
                ratBar.setRating((float)2.5);
                TextView tv = (TextView) infoWindow.findViewById(R.id.tv);
                if(starpointList.get(marker.getTitle()) != null)
                    tv.setText(starpointList.get(marker.getTitle()));
                else
                    tv.setText("2.5");

                newone = (Publtolt)marker.getTag();

                if(newone.getMANAGE_INST_TELNO().equals(""))
                {
                    ImageView v = infoWindow.findViewById(R.id.telephone);
                    v.setVisibility(View.INVISIBLE);
                }
                else
                {
                    infoWindow.setVisibility(View.VISIBLE);
                }
                double distance = newone.getDistance();
                if(distance > 1000) {
                    distance = Math.round(distance / 10.0) / 100.0;
                    infoWindowData.setHotel("Distance : " + distance + "km");
                } else
                    infoWindowData.setHotel("Distance : " + distance + "m");



                int imageId = MainActivity.this.getResources().getIdentifier(infoWindowData.getImage().toLowerCase(),"drawable", MainActivity.this.getPackageName());
                //  img.setImageResource(imageId);

                infoWindowData.setFood(newone.getOPEN_TM_INFO());
                infoWindowData.setTransport(newone.getMANAGE_INST_TELNO());



                hotel_tv.setText(infoWindowData.getHotel());
                food_tv.setText(infoWindowData.getFood());
                transport_tv.setText(infoWindowData.getTransport());

                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);

                return infoWindow;
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        serverThread.setGetReview(marker.getTitle());
        serverThread.getFgHandler().sendEmptyMessage(5);

        if(BeforeMarker != null) {
            if (BeforeMarker.getTitle().equals(currentMarker.getTitle()))
                BeforeMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            else
                BeforeMarker.setIcon(BitmapDescriptorFactory.defaultMarker(204f));
        }

        marker.setIcon(BitmapDescriptorFactory.defaultMarker(285f));
        BeforeMarker = marker;

        CurrentPubltolt = new Publtolt();
        Publtolt publtolt = (Publtolt) marker.getTag();
        CurrentPubltolt = publtolt;
        Double distance = publtolt.getDistance();
        String s_distance;
        if(distance > 1000) {
            s_distance = (Math.round(distance / 10.0) / 100.0) + "km";
        } else {
            s_distance = distance + "m";
        }

        //==================================================================================================================
        try {
            Thread.sleep(100);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        for(int i = 0 ; i < publtoltArrayList.size(); i++) {
            Publtolt publtolt_temp = publtoltArrayList.get(i);
            if(publtolt_temp.getDistance() < radius) {

                MarkerOptions markerOptions2 = new MarkerOptions();
                LatLng latLng = new LatLng(publtolt_temp.getREFINE_WGS84_LAT(),publtolt_temp.getREFINE_WGS84_LOGT());
                markerOptions2.position(latLng)
                        .title(publtolt_temp.getPBCTLT_PLC_NM())
                        .snippet(publtolt_temp.getREFINE_ROADNM_ADDR())
                        .icon(BitmapDescriptorFactory.defaultMarker(204f));

                InfoWindowData info = new InfoWindowData();
                info.setImage("@drawable/ic_wc_black_48dp");
                Double Distance = publtolt_temp.getDistance();
                if(Distance > 1000) {
                    Distance = Math.round(Distance / 10.0) / 100.0;
                    info.setHotel("Distance : " + Distance + "km");
                } else
                    info.setHotel("Distance : " + Distance + "m");

                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        infoButtonListener.setMarker(marker);
                        infobuttonlistener2.setMarker(marker);

                        infoWindow.setLayoutParams(new RelativeLayout.LayoutParams(900,RelativeLayout.LayoutParams.WRAP_CONTENT));


                        TextView name_tv = infoWindow.findViewById(R.id.name);
                        TextView details_tv = infoWindow.findViewById(R.id.details);
                        // ImageView img = view.findViewById(R.id.pic);

                        TextView hotel_tv = infoWindow.findViewById(R.id.hotels);
                        TextView food_tv = infoWindow.findViewById(R.id.food);
                        TextView transport_tv = infoWindow.findViewById(R.id.transport);

                        name_tv.setText(marker.getTitle());
                        details_tv.setText(marker.getSnippet());

                        InfoWindowData infoWindowData = new InfoWindowData();
                        Publtolt newone = new Publtolt();
                        infoWindowData.setImage("@drawable/ic_wc_black_48dp");

                        RatingBar ratBar = (RatingBar) infoWindow.findViewById(R.id.ratingBar);
                        if(starpointList.get(marker.getTitle()) != null)
                            ratBar.setRating(Float.valueOf(starpointList.get(marker.getTitle())));
                        else
                            ratBar.setRating((float) 5.0);
                        TextView tv = (TextView) infoWindow.findViewById(R.id.tv);
                        if(starpointList.get(marker.getTitle()) != null)
                            tv.setText(starpointList.get(marker.getTitle()));
                        else
                            tv.setText("5.0");

                        newone = (Publtolt)marker.getTag();

                        if(newone.getMANAGE_INST_TELNO().equals(""))
                        {
                            ImageView v = infoWindow.findViewById(R.id.telephone);
                            v.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            infoWindow.setVisibility(View.VISIBLE);
                        }
                        double distance = newone.getDistance();
                        if(distance > 1000) {
                            distance = Math.round(distance / 10.0) / 100.0;
                            infoWindowData.setHotel("Distance : " + distance + "km");
                        } else
                            infoWindowData.setHotel("Distance : " + distance + "m");



                        int imageId = MainActivity.this.getResources().getIdentifier(infoWindowData.getImage().toLowerCase(),"drawable", MainActivity.this.getPackageName());
                        //  img.setImageResource(imageId);

                        infoWindowData.setFood(newone.getOPEN_TM_INFO());
                        infoWindowData.setTransport(newone.getMANAGE_INST_TELNO());



                        hotel_tv.setText(infoWindowData.getHotel());
                        food_tv.setText(infoWindowData.getFood());
                        transport_tv.setText(infoWindowData.getTransport());

                        mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);

                        return infoWindow;
                    }
                });

                Marker m = googleMap.addMarker(markerOptions2);
                m.setTag(publtolt_temp);
                previous_marker.add(m);
            }
        }

        return false;
    }

    public void setCurrentLoc(Location location) { // 위치 지정

        if (currentMarker != null) {
            currentMarker.remove();
        }

        if (location != null) {
            Log.d("LOC","MY_LOC");
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            CurrentLng = location.getLongitude();
            CurrentLat = location.getLatitude();

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLatLng);
            markerOptions.title("내 위치");
            markerOptions.snippet(getAddress(currentLatLng));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            markerOptions.draggable(true);

            currentMarker = this.googleMap.addMarker(markerOptions);
            currentMarker.setTag(new Publtolt("내 위치",getAddress(currentLatLng),CurrentLat,CurrentLng,0.0,"010 - 1234 - 5678", "Mon - Fri, 9AM - 6PM"));

            this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14));
            if(circle != null)
                circle.remove();
            circle = this.googleMap.addCircle(new CircleOptions()
                                                        .center(currentLatLng)
                                                        .radius(radius)
                                                        .strokeColor(Color.parseColor("#884169e1"))
                                                        .fillColor(Color.parseColor("#5587cefa")));
            return;
        }

        // 위치를 찾을 수 없는 경우
        Log.d("LOC","Default");
        LatLng SEOUL = new LatLng(37.55, 126.99);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet(getAddress(SEOUL));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        currentMarker = googleMap.addMarker(markerOptions);
        currentMarker.setTag(new Publtolt("서울",getAddress(SEOUL),37.55,126.99,0.0,"010 - 1234 - 5678", "NONE"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 10));
    }

    public String getAddress(LatLng latLng) { // 좌표 -> 주소 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {

            addresses = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    1
            );
        } catch (IOException e) {
            return "주소 변환 불가";
        } catch (IllegalArgumentException e) {
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            return "주소 식별 불가";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString();
    }

    public double calDistance(double lat1, double lon1, double lat2, double lon2){

        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return dist;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }


    public void GetMyGPS() {

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // GPS 프로바이더 사용가능여부
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 프로바이더 사용가능여부
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.d("Main", "isGPSEnabled="+ isGPSEnabled);
        Log.d("Main", "isNetworkEnabled="+ isNetworkEnabled);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                Toast.makeText(MainActivity.this, "latitude: "+ lat +", longitude: "+ lng, Toast.LENGTH_SHORT).show();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Toast.makeText(MainActivity.this, "onStatusChanged", Toast.LENGTH_SHORT).show();
            }

            public void onProviderEnabled(String provider) {
                Toast.makeText(MainActivity.this, "onProviderEnabled", Toast.LENGTH_SHORT).show();
            }

            public void onProviderDisabled(String provider) {
                Toast.makeText(MainActivity.this, "onProviderDisabled", Toast.LENGTH_SHORT).show();
            }
        };

        // Register the listener with the Location Manager to receive location updates
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // 수동으로 위치 구하기
        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        CurrentLoc = lastKnownLocation;
        if (lastKnownLocation != null) {
            CurrentLat = lastKnownLocation.getLatitude();
            CurrentLng = lastKnownLocation.getLongitude();
            Log.d("GPS", "longtitude=" + CurrentLng + ", latitude=" + CurrentLat);
        }

        locationManager.removeUpdates(locationListener);

    }
    public void drawpath() {
        if(polyline != null)
            polyline.remove();

        polyline = googleMap.addPolyline(new PolylineOptions()
                .addAll(mapPoints)
                .width(5)
                .color(Color.parseColor("#FFFF4081")));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(LatLng elem : mapPoints) {
            builder.include(elem);
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),300));
    }


    //===================================================================================================

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                // 전에 마크해두었던 응급실 마커 삭제
                if (previous_marker != null) {
                    for (int i = 0; i < previous_marker.size(); i++) {
                        previous_marker.get(i).remove();
                    }
                    previous_marker.clear();
                }

                t_is.setCharacterStream(new StringReader(msg.obj.toString()));
                t_doc = t_db.parse(t_is);
                t_nodes = t_doc.getElementsByTagName("row");

                ArrayList<LatLng> points = new ArrayList<>();

                for (int i = 0; i < t_nodes.getLength(); i++) {
                    t_node = t_nodes.item(i);
                    t_element = (Element) t_node;

                    String dutyName = t_element.getElementsByTagName("PBCTLT_PLC_NM").item(0).getTextContent();
                    String dutyAddr = t_element.getElementsByTagName("REFINE_ROADNM_ADDR").item(0).getTextContent();
                    String Lat_s = t_element.getElementsByTagName("REFINE_WGS84_LAT").item(0).getTextContent();
                    String Lng_s = t_element.getElementsByTagName("REFINE_WGS84_LOGT").item(0).getTextContent();
                    String number = t_element.getElementsByTagName("MANAGE_INST_TELNO").item(0).getTextContent();
                    String time = t_element.getElementsByTagName("OPEN_TM_INFO").item(0).getTextContent();

                    Double Lat, Lng;
                    if (Lat_s.isEmpty() || Lng_s.isEmpty())
                        continue;
                    else {
                        Lat = Double.parseDouble(Lat_s);
                        Lng = Double.parseDouble(Lng_s);
                    }

                    double distance = Math.round(calDistance(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude, Lat, Lng) * 100) / 100.0;
                    Log.d("Name : ", dutyName);
                    Log.d("Addr: ", dutyAddr);

                    // 화장실 모델 리스트 초기화
                    Publtolt newone = new Publtolt(dutyName, dutyAddr, Lat, Lng, distance, number, time);
                    publtoltArrayList.add(newone);

                    if (distance < radius || msg.what == 2) { //마커 추가
                        LatLng latLng = new LatLng(Lat, Lng);
                        points.add(latLng);
                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.position(latLng)
                                .title(dutyName)
                                .snippet(dutyAddr)
                                .icon(BitmapDescriptorFactory.defaultMarker(204f));

                        Marker m = googleMap.addMarker(markerOptions);
                        m.setTag(newone);
                        previous_marker.add(m);
                    }
                }
                if(previous_marker.size()==0)
                    Toast.makeText(MainActivity.this, "반경안에 화장실이 없습니다.", Toast.LENGTH_SHORT).show();

                if(msg.what == 2) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for(LatLng elem : points) {
                        builder.include(elem);
                    }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),300));
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.d("THREAD_ERR", "handler_error1");
            }
        }
    };

    //===================================================================================================
    private Handler pathHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(!trackModels.isEmpty())
                trackModels.clear();
            if(!mapPoints.isEmpty())
                mapPoints.clear();

            try {
                JSONObject jAr = new JSONObject(msg.obj.toString());
                JSONArray features = jAr.getJSONArray("features");
                TrackModel trackModel = new TrackModel();

                for(int i=0; i<features.length(); i++) {

                    JSONObject test2 = features.getJSONObject(i);
                    if(i == 0) {
                        JSONObject property = test2.getJSONObject("properties");
                        totalDistance += property.getInt("totalDistance");
                    }

                    JSONObject geometry = test2.getJSONObject("geometry");
                    JSONArray coordinates = geometry.getJSONArray("coordinates");

                    String geoType = geometry.getString("type");
                    if(geoType.equals("Point")) {
                        double lonJson = coordinates.getDouble(0);
                        double latJson = coordinates.getDouble(1);

                        lonJson = Math.round(lonJson*10000000d) / 10000000d;
                        latJson = Math.round(latJson*10000000d) / 10000000d;

                        Log.d("JSON_Point", latJson + "," + lonJson + "\n");
                        LatLng point = new LatLng(latJson, lonJson);
                        mapPoints.add(point);
                        trackModel.addLatLng(point);
                    }

                    if(geoType.equals("LineString")) {
                        for(int j=0; j<coordinates.length(); j++) {
                            JSONArray JLinePoint = coordinates.getJSONArray(j);
                            double lonJson = JLinePoint.getDouble(0);
                            double latJson = JLinePoint.getDouble(1);

                            lonJson = Math.round(lonJson*10000000d) / 10000000d;
                            latJson = Math.round(latJson*10000000d) / 10000000d;

                            Log.d("JSON_LineString", latJson + "," + lonJson + "\n");
                            LatLng point = new LatLng(latJson, lonJson);
                            mapPoints.add(point);
                            trackModel.addLatLng(point);
                        }
                    }

                    JSONObject properties = test2.getJSONObject("properties");
                    trackModel.setDescription(properties.getString("description"));
                    trackModel.setDistance(properties.optDouble("distance",0));
                    trackModel.setTime(properties.optDouble("time",0));
                    trackModel.setTurnType(properties.optInt("turnType",0));
                    trackModels.add(trackModel);
                    Log.d("Description",properties.getString("description"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("THREAD_ERR","handler_error2");
            }
            drawpath();
            Log.d("Description","find path end");
            Log.d("Description","" + CurrentLat + " , " + CurrentLng + " / " + CurrentPubltolt.getREFINE_WGS84_LAT() + " , " + CurrentPubltolt.getREFINE_WGS84_LOGT());
        }
    };
    //===================================================================================================
    private Handler serverHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 3) {
                try {
                    t_is.setCharacterStream(new StringReader(msg.obj.toString().trim()));
                    try {
                        t_doc = t_db.parse(t_is);
                        t_nodes = t_doc.getElementsByTagName("toilet"); // review
                        for (int i = 0; i < t_nodes.getLength(); i++) {
                            t_node = t_nodes.item(i);
                            t_element = (Element) t_node;

                            String name = t_element.getElementsByTagName("PBCTLT_PLC_NM").item(0).getTextContent();
                            String point = t_element.getElementsByTagName("AVERAGE_POINT").item(0).getTextContent();
                            Log.d("STARPOINT", name);
                            Log.d("STARPOINT", point);

                            if(starpointList.get(name) == null)
                                starpointList.put(name,point);
                            Log.d("STARPOINT", starpointList.get(name));
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
            }
        }
    };
}
