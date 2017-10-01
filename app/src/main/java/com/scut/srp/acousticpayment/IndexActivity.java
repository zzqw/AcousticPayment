package com.scut.srp.acousticpayment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class IndexActivity extends AppCompatActivity {

    private Button pay;
    private Button receive;
    public void init(){
        pay=(Button)findViewById(R.id.pay);
        receive=(Button)findViewById(R.id.receive);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        init();
        pay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IndexActivity.this,PaymentActivity.class);
                startActivity(intent);
               // IndexActivity.this.finish();
            }
        });
        receive.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IndexActivity.this,ProceedActivity.class);
                startActivity(intent);
             //   IndexActivity.this.finish();
            }
        });
    }
}
