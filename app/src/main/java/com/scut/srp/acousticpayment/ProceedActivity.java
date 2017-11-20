package com.scut.srp.acousticpayment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.scut.srp.acousticpayment.sinvoice.LogHelper;
import com.scut.srp.acousticpayment.sinvoice.SinVoicePlayer;
import com.scut.srp.acousticpayment.sinvoice.SinVoiceRecognition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static com.scut.srp.acousticpayment.LoginActivity.PORT;
import static com.scut.srp.acousticpayment.LoginActivity.ServerIP;

public class ProceedActivity extends Activity implements SinVoicePlayer.Listener, VoiceInHelper.Listener {

    private Button back;
    private Button send;
    private Button receive;
    private EditText count_edt;
    private TextView payID_txt;
    private long now;
    private String message;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String stateCode;
    private String proceedCode;
    private String RSA_message;
    private String payID="";
    private String string="等待接收付款方ID";

    private final static String TAG = "ProceedActivity";
    private final static String CODEBOOK = "1234567";


    private void init(){
        back=(Button)findViewById(R.id.back2);
        send=(Button)findViewById(R.id.send_voice2);
        receive=(Button)findViewById(R.id.rcv_voice2);
        count_edt=(EditText)findViewById(R.id.count_edt2);
        payID_txt=(TextView)findViewById(R.id.payID);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proceed);
        init();

        final SinVoicePlayer mSinVoicePlayer = new SinVoicePlayer(CODEBOOK);
        mSinVoicePlayer.setListener(this);

        final SinVoiceRecognition mRecognition = new SinVoiceRecognition(CODEBOOK);
        mRecognition.setListener(new VoiceInHelper.RecognitionListener(this));

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        final Bundle bundle1 = new Bundle();
        final Handler handler = new Handler();
        bundle1.putCharSequence("RSAKey",bundle.getString("RSAKey"));
        bundle1.putCharSequence("userID",bundle.getString("userID"));
        bundle1.putCharSequence("login_password",bundle.getString("login_password"));
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSinVoicePlayer.stop();
                mRecognition.stop();
                Intent intent = new Intent();
                intent.setClass(ProceedActivity.this,IndexActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
                ProceedActivity.this.finish();
            }
        });
//        send.setEnabled(true);
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                payID_txt.setText("124");
                if (count_edt.getText().toString().trim().equals("")){
                    new AlertDialog.Builder(ProceedActivity.this)
                            .setTitle("系统消息")
                            .setMessage("金额不能为空！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    count_edt.requestFocus();
                                }
                            }).show();
                }else {
                    now = System.currentTimeMillis();
                    RSA_message = bundle.getString("userID") + " " + payID_txt.getText().toString().trim() + " "+now+ " " + count_edt.getText().toString().trim()+ " "+"content";
                    try {
                        message = "tradeFromPayee" +" " + bundle.getString("userID") + " " + RSA.encrypt(RSA_message,RSA.getPublicKey(bundle.getString("RSAKey")));
                    } catch (Exception e) {
                        Log.e(TAG,"RSA encrypt error!");
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                socket = new Socket(ServerIP, PORT);
                                writer = new PrintWriter(socket.getOutputStream(), true);
                                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                writer.println(message);
                                writer.flush();
                                while ((proceedCode = reader.readLine())!=null) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ProceedActivity.this, proceedCode, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    if (proceedCode.equals("insert success")) {
                                        while ((stateCode = reader.readLine())!=null) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(ProceedActivity.this, stateCode, Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            if (stateCode.equals("tradeFromPayerSuccess")) {
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(ProceedActivity.this, stateCode, Toast.LENGTH_LONG).show();
                                                        new AlertDialog.Builder(ProceedActivity.this)
                                                                .setTitle("系统消息")
                                                                .setMessage("交易成功！")
                                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        Intent intent = new Intent();
                                                                        intent.setClass(ProceedActivity.this, IndexActivity.class);
                                                                        intent.putExtras(bundle1);
                                                                        startActivity(intent);
                                                                        ProceedActivity.this.finish();
                                                                    }
                                                                }).show();
                                                    }
                                                });
                                                break;
                                            }else if (stateCode.equals("balanceNotEnough")){
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        new android.support.v7.app.AlertDialog.Builder(ProceedActivity.this)
                                                                .setTitle("系统消息")
                                                                .setMessage("付款方余额不足！")
                                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        Intent intent = new Intent();
                                                                        intent.setClass(ProceedActivity.this, IndexActivity.class);
                                                                        intent.putExtras(bundle1);
                                                                        startActivity(intent);
                                                                        ProceedActivity.this.finish();
                                                                    }
                                                                }).show();
                                                    }
                                                });
                                                break;
                                            } else {
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(ProceedActivity.this, stateCode, Toast.LENGTH_LONG).show();
                                                        new AlertDialog.Builder(ProceedActivity.this)
                                                                .setTitle("系统消息")
                                                                .setMessage("交易失败！")
                                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        Intent intent = new Intent();
                                                                        intent.setClass(ProceedActivity.this, IndexActivity.class);
                                                                        intent.putExtras(bundle1);
                                                                        startActivity(intent);
                                                                        ProceedActivity.this.finish();
                                                                    }
                                                                }).show();
                                                    }
                                                });
                                                break;
                                            }
                                        }
                                        break;
                                    }
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
        receive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecognition.start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if (!payID.equals("")) {
                                mRecognition.stop();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        send.setEnabled(true);
                                    }
                                });
                                break;
                            }
                        }
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                payID_txt=(TextView)findViewById(R.id.payID);
                payID_txt.setText(string);
                payID="";
            }
        });
        Log.v(TAG, "start");
    }

    @Override
    public void onResult(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                payID_txt=(TextView)findViewById(R.id.payID);
                payID_txt.setText(result);
                payID=result;
            }
        });
    }
}
