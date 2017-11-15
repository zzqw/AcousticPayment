package com.scut.srp.acousticpayment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import static com.scut.srp.acousticpayment.LoginActivity.PORT;
import static com.scut.srp.acousticpayment.LoginActivity.ServerIP;
import static com.scut.srp.acousticpayment.MD5Util.md5;

public class RegisterActivity extends AppCompatActivity {

    private EditText userID_edt;
    private EditText login_password_edt1;
    private EditText login_password_edt2;
    private EditText password_edt1;
    private EditText password_edt2;
    private Button back;
    private Button register;
    private String userID;
    private String login_password1;
    private String login_password2;
    private String password1;
    private String password2;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    String return_message;

    private void init(){
        userID_edt=(EditText)findViewById(R.id.account2);
        login_password_edt1=(EditText)findViewById(R.id.login_password2);
        login_password_edt2=(EditText)findViewById(R.id.login_password3);
        password_edt1=(EditText)findViewById(R.id.password1);
        password_edt2=(EditText)findViewById(R.id.password2);
        back=(Button)findViewById(R.id.back3);
        register=(Button)findViewById(R.id.register2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        final Handler handler = new Handler();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                RegisterActivity.this.finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userID=userID_edt.getText().toString().trim();
                login_password1=login_password_edt1.getText().toString().trim();
                login_password2=login_password_edt2.getText().toString().trim();
                password1=password_edt1.getText().toString().trim();
                password2=password_edt2.getText().toString().trim();
                if(userID.equals("")||login_password1.equals("")||login_password2.equals("")||password1.equals("")||password2.equals("")){
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("系统消息")
                            .setMessage("输入不能为空！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    userID_edt.requestFocus();
                                }
                            }).show();
                }else if(!login_password1.equals(login_password2)){
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("系统消息")
                            .setMessage("登录密码不一致，请重新输入！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    login_password_edt1.setText("");
                                    login_password_edt2.setText("");
                                    login_password_edt1.requestFocus();
                                }
                            }).show();
                }
                else if(!password1.equals(password2)){
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("系统消息")
                            .setMessage("支付密码不一致，请重新输入！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    password_edt1.setText("");
                                    password_edt2.setText("");
                                    password_edt1.requestFocus();
                                }
                            }).show();
                }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                socket=new Socket(ServerIP,PORT);
                                String message="register"+" "+userID+" "+login_password1+" "+md5(password1);
                                writer = new PrintWriter(socket.getOutputStream(),true);
                                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                writer.println(message);
                                writer.flush();
                                while(true){
                                    return_message=reader.readLine();
                                    if(return_message!=null)break;
                                }
                                if(return_message.equals("userIDExisted")){
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            new AlertDialog.Builder(RegisterActivity.this)
                                                    .setTitle("系统消息")
                                                    .setMessage("userID已存在！")
                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            userID_edt.setText("");
                                                            userID_edt.requestFocus();
                                                        }
                                                    }).show();
                                        }
                                    });
                                }else{
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            //差保存RSA公钥的代码
                                            new AlertDialog.Builder(RegisterActivity.this)
                                                    .setTitle("系统消息")
                                                    .setMessage("注册成功！")
                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent();
                                                            intent.setClass(RegisterActivity.this,LoginActivity.class);
                                                            startActivity(intent);
                                                            RegisterActivity.this.finish();
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
            }
        });
    }
}
