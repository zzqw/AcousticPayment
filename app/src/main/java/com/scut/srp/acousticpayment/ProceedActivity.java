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

public class ProceedActivity extends AppCompatActivity {

    private Button back;
    private Button send;
    private Button receive;
    private EditText count_edt;
    public int count;
    public Date now;
    public String message;
    private void init(){
        back=(Button)findViewById(R.id.back2);
        send=(Button)findViewById(R.id.send_voice2);
        receive=(Button)findViewById(R.id.rcv_voice2);
        count_edt=(EditText)findViewById(R.id.count_edt2);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proceed);
        init();
        //count=Integer.valueOf(count_edt.getText().toString());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ProceedActivity.this,IndexActivity.class);
                startActivity(intent);
                ProceedActivity.this.finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now = new Date();
                message=count_edt.getText().toString()+" "+now;
                new AlertDialog.Builder(ProceedActivity.this)
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
                intent.setClass(ProceedActivity.this,TradeActivity.class);
                startActivity(intent);
                ProceedActivity.this.finish();
            }
        });
    }
}
