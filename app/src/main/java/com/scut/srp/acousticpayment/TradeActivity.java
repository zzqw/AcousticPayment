package com.scut.srp.acousticpayment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TradeActivity extends AppCompatActivity {

    private Button back;
    private void init(){
        back=(Button)findViewById(R.id.back3);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        init();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(TradeActivity.this,IndexActivity.class);
                startActivity(intent);
                TradeActivity.this.finish();
            }
        });
    }
}
