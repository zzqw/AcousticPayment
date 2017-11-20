package com.scut.srp.acousticpayment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static com.scut.srp.acousticpayment.LoginActivity.PORT;
import static com.scut.srp.acousticpayment.LoginActivity.ServerIP;

public class IndexActivity extends AppCompatActivity {

    private Button pay;
    private Button receive;
    private Button check;
    private Button test;
    private Button back;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    String return_message;

    public void init(){
        pay=(Button)findViewById(R.id.pay);
        back=(Button)findViewById(R.id.backtologin);
        receive=(Button)findViewById(R.id.receive);
        check=(Button)findViewById(R.id.check);
        test=(Button)findViewById(R.id.test);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        init();
        Intent intent = getIntent();
        final Handler handler=new Handler();
        final Bundle bundle = intent.getExtras();
        final Bundle bundle1=new Bundle();
        bundle1.putCharSequence("RSAKey",bundle.getString("RSAKey"));
        bundle1.putCharSequence("userID",bundle.getString("userID"));
        bundle1.putCharSequence("login_password",bundle.getString("login_password"));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IndexActivity.this,LoginActivity.class);
                startActivity(intent);
                IndexActivity.this.finish();
            }
        });
        pay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IndexActivity.this,PaymentActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });
        test.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IndexActivity.this,Test.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });
        receive.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IndexActivity.this,ProceedActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            socket=new Socket(ServerIP,PORT);
                            String message="queryBalance"+" "+bundle.getString("userID")+" "+bundle.getString("login_password");
                            writer = new PrintWriter(socket.getOutputStream(),true);
                            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            writer.println(message);
                            while(true){
                                return_message=reader.readLine();
                                if(return_message!=null)
                                    break;
                            }
                            if(return_message.equals("queryFailed")){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(IndexActivity.this)
                                                .setTitle("系统消息")
                                                .setMessage("查询失败，请重试！")
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                }).show();
                                    }
                                });
                            }else{
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(IndexActivity.this)
                                                .setTitle("系统消息")
                                                .setMessage("当前账户余额为："+return_message)
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

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
    }
}
