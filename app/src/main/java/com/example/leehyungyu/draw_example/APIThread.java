package com.example.leehyungyu.draw_example;

/**
 * Created by LeeHyunGyu on 2018-04-12.
 */

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class APIThread extends Thread {
    final private String callback_url1 = "https://openapi.gg.go.kr/Publtolt"; // 화장실 정보 for api
    final private String callback_url2 = "http://javaoh.iptime.org:8900/toilet/tltdata.jsp"; // 화장실 정보 for server
    final private String key = "?KEY=64913fa0df97443098d268700bc4f613"; // open api key
    final private String type = "xml";
    private String pindex = "11";
    final private String psize = "100";
    //====================================================================================================

    private Handler thHandler = null; // 쓰레드 핸들러
    private Handler fgHandler = null; // 프래그먼트 핸들러
    private String xml = "";
    private String hid = ""; // for CardContent (more detail information)
    private static String Q0 = "";
    private static String Q1 = "";
    private static double latitude = 0;
    private static double longitude = 0;
    private URL url = null;
    private BufferedReader in = null;
    private String inLine = "";

    public Handler getFgHandler() {
        return thHandler;
    }

    public APIThread(Handler h) {
        fgHandler = h;
    }

    public APIThread(Handler h, String hid) {
        fgHandler = h;
        this.hid = hid;
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

    //====================================================================================================

    @Override
    public void run() {

        if(hid.isEmpty()) {
            Looper.prepare();
            thHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Message retMsg = new Message();

                    switch (msg.what) {

                        //====================================================================================================
                        case 1: // 응급실 좌표 구하기
                            pindex="14";
                            Log.d("THREAD", "thread1 start");
                            xml = "";
//                            String[] split = msg.obj.toString().split("&");
//                            try {
//                                Q0 = URLEncoder.encode(split[0], "UTF-8");
//                                Q1 = URLEncoder.encode(split[1], "UTF-8");
//                                latitude = Double.parseDouble(split[2]);
//                                longitude = Double.parseDouble(split[3]);
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                                Log.d("THREAD_ERR", "thread error1");
//                            }
                            try {
                                url = new URL(callback_url1 + key + "&" + "Type=" + type + "&" + "pindex=" + pindex + "&" + "pSize=" + psize);
                                Log.d("URL", url + "<-");
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                Log.d("THREAD_ERR", "thread error2");

                            }

                            try {
                                in = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("THREAD_ERR", "thread error3");
                            }

                            try {
                                while ((inLine = in.readLine()) != null) {
                                    xml += inLine;
//                                    Log.d("THREAD",inLine);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("THEREAD_ERR", "thread_error4");
                            }

                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("THREAD_ERR", "thrread_error5");
                            }
                            Log.d("XMLRET", xml + "<-");
                            retMsg.obj = xml;
                            retMsg.what = 1;
                            fgHandler.sendMessage(retMsg);
                            break;
                        //====================================================================================================
                        case 2:
                            pindex = "1";
                            Log.d("THREAD", "thread1 start");
                            xml = "";

                            try {
                                url = new URL(callback_url1 + key + "&" + "Type=" + type + "&" + "pindex=" + pindex + "&" + "pSize=" + psize + "&" + "SIGUN_NM=" + msg.obj.toString());
                                Log.d("URL", url + "<-");
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                Log.d("THREAD_ERR", "thread error2");

                            }

                            try {
                                in = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("THREAD_ERR", "thread error3");
                            }

                            try {
                                while ((inLine = in.readLine()) != null) {
                                    xml += inLine;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("THEREAD_ERR", "thread_error4");
                            }

                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("THREAD_ERR", "thrread_error5");
                            }
                            Log.d("XMLRET", xml + "<-");
                            retMsg.obj = xml;
                            retMsg.what = 2;
                            fgHandler.sendMessage(retMsg);
                            break;

                            }

                    }
                };
            Looper.loop();
        }
    }
}
