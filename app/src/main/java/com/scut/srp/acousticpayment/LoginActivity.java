package com.scut.srp.acousticpayment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginActivity extends AppCompatActivity {
    private EditText userID_edt;
    private EditText login_password_edt;
    private Button login;
    private Button register;
    protected static final int PORT=10248;
    protected static final String ServerIP="120.78.83.222";//221.4.34.178
    private Socket socket;
    private String userID;
    private String login_password;
    private PrintWriter writer;
    private BufferedReader reader;
    String return_message;

    private void init(){
        userID_edt=(EditText)findViewById(R.id.userID);
        login_password_edt=(EditText)findViewById(R.id.login_password);
        login=(Button)findViewById(R.id.login);
        register=(Button)findViewById(R.id.register);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        final Handler handler = new Handler();
        final Bundle bundle = new Bundle();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userID=userID_edt.getText().toString().trim();
                login_password=login_password_edt.getText().toString().trim();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            socket=new Socket(ServerIP,PORT);
                            String message="login"+" "+userID+" "+login_password;
                            writer = new PrintWriter(socket.getOutputStream(),true);
                            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            writer.println(message);
                            writer.flush();
                            while(true){
                                return_message=reader.readLine();
                                if(return_message!=null)break;
                            }
                            if(return_message.equals("loginFailed")){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle("系统消息")
                                                .setMessage("账号或密码错误！")
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        userID_edt.setText("");
                                                        userID_edt.requestFocus();
                                                    }
                                                }).show();
                                    }
                                });
                            } else{
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        bundle.putCharSequence("RSA",return_message);
                                        new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle("系统消息")
                                                .setMessage("登录成功！")
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        bundle.putCharSequence("userID",userID);
                                                        bundle.putCharSequence("login_password",login_password);
                                                        Intent intent = new Intent();
                                                        intent.setClass(LoginActivity.this,IndexActivity.class);
                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                        LoginActivity.this.finish();
                                                    }
                                                }).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            writer.close();
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
