package com.example.leehyungyu.draw_example;

import android.icu.text.LocaleDisplayNames;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.skt.Tmap.util.HttpConnect;

import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * Created by pc on 2018-05-19.
 */

public class PathThread extends Thread {

    private final String call_url = "https://api2.sktelecom.com/tmap/";
    private final String type = "routes/pedestrian?version=1";  // 타입 = 도보
    /* *    Another type
     *       Car = routes?version=1
     *       Bicyle = routes/bicycle?version=1
     * */
    private final String code = "reqCoordType=WGS84GEO&resCoordType=WGS84GEO";
    private final String key = "f2cb4b59-32a1-4293-967f-03f42c3d845a";
    //Server key f2cb4b59-32a1-4293-967f-03f42c3d845a
    //Browser key a51a52d7-69c0-43b2-ae33-239133da849a
    String xml = null;
    private URL url = null;
    private BufferedReader in = null;
    private String inLine = "";

    private Handler thHandler = null; // 쓰레드 핸들러
    private Handler fgHandler = null; // 프래그먼트 핸들러

    public Handler getFgHandler() {
        return thHandler;
    }
    public PathThread(Handler h) {
        fgHandler = h;
    }

    //====================================================================================================

    private String startX;  // 출발 X좌표
    private String startY;  // 출발 Y좌표
    private String endX;    // 도착 X좌표
    private String endY;    // 도착 Y좌표
    private String startName = "현위치";
    private String endName; // 도착지 이름

    //====================================================================================================

    public void initiate(String startX, String startY, String endX, String endY, String endName) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.endName = endName;
    }

    @Override
    public void run() {
        Looper.prepare();
        thHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Message retMsg = new Message();
                StringBuilder uri = new StringBuilder();
                uri.append(url);
                uri.append(type);
                uri.append("&appKey=" + key);
                StringBuilder content = new StringBuilder();
                content.append("&startY=" + startY);
                content.append("&startX=" + startX);
                content.append("&endY=" + endY);
                content.append("&endX=" + endX);
                content.append("&" + code);

                try {
                    startName = URLEncoder.encode(startName, "UTF-8");
                    endName = URLEncoder.encode(endName, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.d("THREAD_ERR", "thread error1");
                }

                try {
                    url = new URL(call_url + type + "&appKey=" + key + "&startX=" + startX + "&startY=" + startY
                            + "&endX=" + endX + "&endY=" + endY + "&" + code + "&startName=" + startName + "&endName=" + endName);
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
                        xml = inLine;
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
                retMsg.obj=xml;
                fgHandler.sendMessage(retMsg);
            }
        };
        Looper.loop();
    }
}
