package com.scut.srp.acousticpayment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class PaymentActivity extends AppCompatActivity implements SinVoicePlayer.Listener, VoiceInHelper.Listener {

    private Button back;
    private Button send;
    private Button receive;
    private TextView count_edt;
    private EditText pay_psd_edt;
    private TextView proceed_id;
    private String count;
    private String pay_password;
    private long now;
    private String message;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String stateCode;
    private String[] receive_message;
    private final static String TAG = "PaymentActivity";
    private final static String CODEBOOK = "1234567";
    private String proceedID = "";

    private void init(){
        back=(Button)findViewById(R.id.back);
        send=(Button)findViewById(R.id.send_voice);
        receive=(Button)findViewById(R.id.rcv_voice);
        count_edt=(TextView)findViewById(R.id.count_edt);
        pay_psd_edt=(EditText)findViewById(R.id.pay_psd_edt);
        proceed_id=(TextView)findViewById(R.id.proceed_id);
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
        bundle1.putCharSequence("RSAKey",bundle.getString("RSAKey"));
        bundle1.putCharSequence("userID",bundle.getString("userID"));
        bundle1.putCharSequence("login_password",bundle.getString("login_password"));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSinVoicePlayer.stop();
                mRecognition.stop();
                Intent intent = new Intent();
                intent.setClass(PaymentActivity.this,IndexActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
                PaymentActivity.this.finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
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
                    String md5_pay_password = MD5.getMD5(pay_password);
                    now = System.currentTimeMillis();
                    String md5_message = MD5.getMD5(bundle.getString("userID")+bundle.getString("login_password") + md5_pay_password + String.valueOf(now));
                    message ="tradeFromPayer"+" "+receive_message[0]+" "+  bundle.getString("userID") + " " + String.valueOf(now)+" "+receive_message[1]+ " " +md5_message;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                        try {
                            socket = new Socket(ServerIP, PORT);
                            writer = new PrintWriter(socket.getOutputStream(), true);
                            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            writer.println(message);
                            writer.flush();
                            while ((stateCode = reader.readLine())!= null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PaymentActivity.this, stateCode, Toast.LENGTH_LONG).show();
                                    }
                                });
                                if (stateCode.equals("tradeFromPayerSuccess")) {
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
                                    break;
                                } else if (stateCode.equals("balanceNotEnough")){
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            new AlertDialog.Builder(PaymentActivity.this)
                                                    .setTitle("系统消息")
                                                    .setMessage("余额不足！")
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
                                    break;
                                } else{
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
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSinVoicePlayer.play(VoiceOutHelper.modify(bundle.getString("userID")), true, 1000);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mSinVoicePlayer.stop();
                    }
                }.start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket = new Socket(ServerIP, PORT);
                            writer = new PrintWriter(socket.getOutputStream(), true);
                            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            writer.println("readyForTrade"+" "+bundle.getString("userID"));
                            writer.flush();
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
                                    proceed_id.setText(receive_message[0]);
                                    count_edt.setText(receive_message[1]);
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
                proceedID="";
            }
        });
        Log.v(TAG, "start");
    }

    @Override
    public void onResult(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                proceedID=result;
            }
        });
    }

}
