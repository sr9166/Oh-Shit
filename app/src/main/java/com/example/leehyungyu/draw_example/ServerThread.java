package com.example.leehyungyu.draw_example;

import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by LeeHyunGyu on 2018-06-08.
 */

public class ServerThread extends Thread {
    private String idText;
    private String passwordText;
    private String nameText;
    private String emailText;
    private String toltname;
    private String starpoint;
    private String linereview;
    private String myResult; // 서버로 부터 받아온 값
    private Handler fgHandler=null; //프래그먼트 핸들러
    private Handler thHandler=null; //쓰레드 핸들러

    public ServerThread(Handler mHandler){ fgHandler=mHandler;} // 생성자
    public Handler getFgHandler() {
        return thHandler;
    }

    final private String callback_url1 = "http://javaoh.iptime.org:8900/toilet/toiletReview.jsp";
    final private String callback_url2 = "http://javaoh.iptime.org:8900/toilet/eachtltAP.jsp";
    private String xml = "";
    private URL url = null;
    private BufferedReader in = null;
    private String inLine = "";

    public void setLogin(String idText,String passwordText,String myResult){
        this.idText = idText;
        this.passwordText = passwordText;
        this.myResult = myResult;
    }

    public void setRegister(String idText,String passwordText,String nameText,String emailText,String myResult){
        this.idText = idText;
        this.passwordText = passwordText;
        this.nameText = nameText;
        this.emailText = emailText;
        this.myResult = myResult;
    }

    public void setAddReview(String idText, String toltname, String starpoint, String linereview, String myResult) {
        this.idText = idText;
        this.toltname = toltname;
        this.starpoint = starpoint;
        this.linereview = linereview;
        this.myResult = myResult;
    }

    public void setGetReview(String toltname) {
        this.toltname = toltname;
    }

    public void run(){

        Looper.prepare();
        thHandler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                Message retMsg = new Message();

                switch (msg.what){
                    case 1: // Login
                        try {
                            URL url = new URL("http://javaoh.iptime.org:8900/user/login.jsp");
                            HttpURLConnection http = (HttpURLConnection) url.openConnection();

                            http.setDefaultUseCaches(false);
                            http.setDoInput(true);
                            http.setDoOutput(true);
                            http.setRequestMethod("POST");

                            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                            StringBuffer buffer = new StringBuffer();
                            buffer.append("id").append("=").append(idText).append("&");
                            buffer.append("pwd").append("=").append(passwordText);

                            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
                            PrintWriter writer = new PrintWriter(outStream);
                            writer.write(buffer.toString());
                            writer.flush();

                            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
                            BufferedReader reader = new BufferedReader(tmp);
                            StringBuilder builder = new StringBuilder();
                            String str;
                            while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                                builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
                            }
                            myResult = builder.toString();
                            retMsg.obj = myResult;
                            fgHandler.sendMessage(retMsg);
                        }
                        catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2: // Register
                        try {
                            URL url = new URL("http://javaoh.iptime.org:8900/user/join.jsp");
                            HttpURLConnection http = (HttpURLConnection) url.openConnection();

                            http.setDefaultUseCaches(false);
                            http.setDoInput(true);
                            http.setDoOutput(true);
                            http.setRequestMethod("POST");

                            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                            StringBuffer buffer = new StringBuffer();
                            buffer.append("id").append("=").append(idText).append("&");
                            buffer.append("name").append("=").append(nameText).append("&");
                            buffer.append("pwd").append("=").append(passwordText).append("&");
                            buffer.append("email").append("=").append(emailText);

                            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
                            PrintWriter writer = new PrintWriter(outStream);
                            writer.write(buffer.toString());
                            writer.flush();

                            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
                            BufferedReader reader = new BufferedReader(tmp);
                            StringBuilder builder = new StringBuilder();
                            String str;
                            while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                                builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
                            }
                            myResult = builder.toString();
                            retMsg.obj = myResult;
                            fgHandler.sendMessage(retMsg);
                        }
                        catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3: // add Review
                        Log.d("SERVER_THREAD","ADD REVIEW START");
                        try {
                            URL url = new URL("http://javaoh.iptime.org:8900/toilet/review2.jsp");
                            HttpURLConnection http = (HttpURLConnection) url.openConnection();

                            http.setDefaultUseCaches(false);
                            http.setDoInput(true);
                            http.setDoOutput(true);
                            http.setRequestMethod("POST");

                            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                            StringBuffer buffer = new StringBuffer();
                            buffer.append("PBCTLT_PLC_NM").append("=").append(toltname).append("&");
                            buffer.append("id").append("=").append(idText).append("&");
                            buffer.append("STAR_POINT").append("=").append(starpoint).append("&");
                            buffer.append("LINE_REVIEW").append("=").append(linereview);

                            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
                            PrintWriter writer = new PrintWriter(outStream);
                            writer.write(buffer.toString());
                            writer.flush();

                            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
                            BufferedReader reader = new BufferedReader(tmp);
                            StringBuilder builder = new StringBuilder();
                            String str;
                            while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                                builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
                            }
                            myResult = builder.toString();
                            retMsg.what = 1;
                            retMsg.obj = myResult;
                            fgHandler.sendMessage(retMsg);
                            Log.d("SERVER_THREAD",myResult);
                        }
                        catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4: // get Reviews
                        Log.d("SERVER_THREAD","THREAD_START");

                        try {
                            url = new URL(callback_url1 + "?PBCTLT_PLC_NM=" + toltname);
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
                    case 5:
                        Log.d("SERVER_THREAD","GET EACH STARPOINT START");
                        try {
                            URL url = new URL("http://javaoh.iptime.org:8900/toilet/eachtltAP.jsp");
                            HttpURLConnection http = (HttpURLConnection) url.openConnection();

                            http.setDefaultUseCaches(false);
                            http.setDoInput(true);
                            http.setDoOutput(true);
                            http.setRequestMethod("POST");

                            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                            StringBuffer buffer = new StringBuffer();
                            buffer.append("PBCTLT_PLC_NM").append("=").append(toltname);

                            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
                            PrintWriter writer = new PrintWriter(outStream);
                            writer.write(buffer.toString());
                            writer.flush();

                            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
                            BufferedReader reader = new BufferedReader(tmp);
                            StringBuilder builder = new StringBuilder();
                            String str;
                            while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                                builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
                            }
                            myResult = builder.toString();
                            retMsg.what = 3;
                            retMsg.obj = myResult;
                            fgHandler.sendMessage(retMsg);
                            Log.d("SERVER_THREAD",myResult);
                        }
                        catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
        Looper.loop();
    }
}
