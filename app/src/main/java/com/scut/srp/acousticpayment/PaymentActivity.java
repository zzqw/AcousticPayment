package com.scut.srp.acousticpayment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.scut.srp.acousticpayment.sinvoice.SinVoicePlayer;
import com.scut.srp.acousticpayment.sinvoice.LogHelper;
import com.scut.srp.acousticpayment.sinvoice.SinVoiceRecognition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static com.scut.srp.acousticpayment.LoginActivity.PORT;
import static com.scut.srp.acousticpayment.LoginActivity.ServerIP;
import static com.scut.srp.acousticpayment.MD5Util.md5;

public class PaymentActivity extends AppCompatActivity implements SinVoicePlayer.Listener, VoiceInHelper.Listener {

    private Button back;
    private Button send;
    private Button receive;
    private TextView count_edt;
    private EditText pay_psd_edt;
    public String count;
    public String pay_password;
    public long now;
    public String message;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String stateCode;
    private String[] receive_message;
    private final static String TAG = "MainActivity";
    private final static String CODEBOOK = "12345";
    private String proceedID = "00000";

    private void init(){
        back=(Button)findViewById(R.id.back);
        send=(Button)findViewById(R.id.send_voice);
        receive=(Button)findViewById(R.id.rcv_voice);
        count_edt=(TextView)findViewById(R.id.count_edt);
        pay_psd_edt=(EditText)findViewById(R.id.pay_psd_edt);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        init();

        final SinVoicePlayer mSinVoicePlayer = new SinVoicePlayer(CODEBOOK);
        mSinVoicePlayer.setListener(this);

        final SinVoiceRecognition mRecognition = new SinVoiceRecognition(CODEBOOK);
        mRecognition.setListener(new VoiceInHelper.RecognitionListener(this));

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        final Bundle bundle1 = new Bundle();
        final Handler handler = new Handler();
        bundle1.putCharSequence("RSA",bundle.getString("RSA"));
        bundle1.putCharSequence("userID",bundle.getString("userID"));
        bundle1.putCharSequence("login_password",bundle.getString("login_password"));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PaymentActivity.this,IndexActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
                PaymentActivity.this.finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                pay_password=pay_psd_edt.getText().toString();
                if(pay_password.equals("")){
                    new AlertDialog.Builder(PaymentActivity.this)
                            .setTitle("系统消息")
                            .setMessage("密码不能为空！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pay_psd_edt.requestFocus();
                                }
                            }).show();
                }else {
                    String md5_pay_password = md5(pay_password);
                    now = System.currentTimeMillis();
                    String md5_message = md5(bundle.getString("userID") + " " + bundle.getString("login_password") + " " + now + " " + md5_pay_password);
                    message =md5_message + " " + bundle.getString("userID") + " " + now+" "+receive_message[1]+" "+receive_message[0];
                    Toast.makeText(PaymentActivity.this, message, Toast.LENGTH_LONG).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                        try {
                            socket = new Socket(ServerIP, PORT);
                            writer = new PrintWriter(socket.getOutputStream(), true);
                            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            writer.println(message);
                            writer.flush();
                            while (true) {
                                stateCode = reader.readLine();
                                if (stateCode != null) break;
                            }
                            if (stateCode.equals("loginFailed")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(PaymentActivity.this)
                                                .setTitle("系统消息")
                                                .setMessage("交易失败！")
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent();
                                                        intent.setClass(PaymentActivity.this, IndexActivity.class);
                                                        intent.putExtras(bundle1);
                                                        startActivity(intent);
                                                        PaymentActivity.this.finish();
                                                    }
                                                }).show();
                                    }
                                });
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(PaymentActivity.this)
                                                .setTitle("系统消息")
                                                .setMessage("交易成功！")
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent();
                                                        intent.setClass(PaymentActivity.this, IndexActivity.class);
                                                        intent.putExtras(bundle1);
                                                        startActivity(intent);
                                                        PaymentActivity.this.finish();
                                                    }
                                                }).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
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
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSinVoicePlayer.play(VoiceOutHelper.modify("111"), true, 1000);
                mRecognition.start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true){
                            if (proceedID.equals("11111")){
                                mSinVoicePlayer.stop();
                                mRecognition.stop();
                                break;
                            }
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    socket = new Socket(ServerIP, PORT);
                                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                    while (true) {
                                        count = reader.readLine();
                                        if (count != null) {
                                            break;
                                        }
                                    }
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            receive_message = count.split(" ");
                                            count_edt.setText(receive_message[0]);
                                            send.setEnabled(true);
                                        }
                                    });
                                }catch (IOException e) {
                                    e.printStackTrace();
                                }finally {
                                    try {
                                        socket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                    }
                }).start();
            }
        });
    }

    @Override
    public void onPlayStart() {
        LogHelper.d(TAG, "start play");
    }

    @Override
    public void onPlayEnd() {
        LogHelper.d(TAG, "stop play");
    }
    @Override
    public void onRegStart() {
        proceedID = "00000";
        Log.v(TAG, "start");
    }

    @Override
    public void onResult(final String result) {
        proceedID = result;
    }

}
