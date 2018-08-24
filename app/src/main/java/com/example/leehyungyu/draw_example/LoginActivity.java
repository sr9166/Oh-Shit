package com.example.leehyungyu.draw_example;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by LeeHyunGyu on 2018-06-07.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private int i = -1;

    EditText _idText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;

    private ServerThread serverThread;
    private String myResult;
    private InputSource t_is = null;
    private Document t_doc = null;
    private DocumentBuilder t_db = null;
    private DocumentBuilderFactory t_dbf = null;
    private NodeList t_nodes = null;
    private Node t_node = null;
    private Element t_element = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        serverThread = new ServerThread(mHandler);
        serverThread.setDaemon(true);
        serverThread.start();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _idText = findViewById(R.id.input_id);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);
        _signupLink = findViewById(R.id.link_signup);
        ImageView login_background =  findViewById(R.id.login_background);
        login_background.setAlpha(0.2f);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        try {
            t_dbf = DocumentBuilderFactory.newInstance();
            t_db = t_dbf.newDocumentBuilder();
            t_is = new InputSource();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        //TODO : ON LOGIN PROCESS

        String id = _idText.getText().toString();
        String password = _passwordText.getText().toString();

        serverThread.setLogin(id,password,myResult);
        serverThread.getFgHandler().sendEmptyMessage(1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful signup logic here
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Success!");
                builder.setMessage("회원가입 된 아이디로 로그인 해주세요")
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

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        boolean valid = true;

        String email = _idText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _idText.setError("enter a valid ID");
            valid = false;
        } else {
            _idText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
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

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (msg.obj.toString().equals("\n\n\n\nWrong Password!\n\n\n")) {       //비밀번호를 잘못 입력했을시
                    Toast.makeText(LoginActivity.this, "잘못된 비밀번호를 입력했습니다.", Toast.LENGTH_SHORT).show();
                } else if (msg.obj.toString().equals("\n\n\n\njava.sql.SQLException: Illegal operation on empty result set.\n\n\n"))   //아이디가 없을때
                {
                    Toast.makeText(LoginActivity.this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    t_is.setCharacterStream(new StringReader(msg.obj.toString()));
                    try {
                        t_doc = t_db.parse(t_is);
                        t_nodes = t_doc.getElementsByTagName("user");// user
                        t_node = t_nodes.item(0);
                        t_element = (Element) t_node;

                        String userid = t_element.getElementsByTagName("id").item(0).getTextContent();
                        String username = t_element.getElementsByTagName("name").item(0).getTextContent();
                        String useremail = t_element.getElementsByTagName("email").item(0).getTextContent();
                        User user=new User(userid,username,useremail);

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("userid",userid);
                        returnIntent.putExtra("username",username);
                        returnIntent.putExtra("useremail",useremail);
                        setResult(RESULT_OK,returnIntent);
                        finish();

                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
                Log.d("THREAD_ERR", "handler_error1");
            }

        }
    };
}
