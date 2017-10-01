package com.scut.srp.acousticpayment;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

public class PaymentActivity extends AppCompatActivity {

    private Button back;
    private Button send;
    private Button receive;
    private EditText count_edt;
    private EditText pay_psd_edt;
    public int count;
    public String pay_password;
    public Date now;
    public String message;
    private void init(){
        back=(Button)findViewById(R.id.back);
        send=(Button)findViewById(R.id.send_voice);
        receive=(Button)findViewById(R.id.rcv_voice);
        count_edt=(EditText)findViewById(R.id.count_edt);
        pay_psd_edt=(EditText)findViewById(R.id.pay_psd_edt);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        init();
       // count=Integer.valueOf(count_edt.getText().toString());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PaymentActivity.this,IndexActivity.class);
                startActivity(intent);
                PaymentActivity.this.finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay_password=pay_psd_edt.getText().toString();
                now = new Date();
                message=count_edt.getText().toString()+" "+now+" "+pay_password;
                new AlertDialog.Builder(PaymentActivity.this)
                .setTitle("通知")
                        .setMessage(message)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PaymentActivity.this,TradeActivity.class);
                startActivity(intent);
                PaymentActivity.this.finish();
            }
        });
    }
}
