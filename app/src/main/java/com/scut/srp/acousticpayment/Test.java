package com.scut.srp.acousticpayment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.scut.srp.acousticpayment.sinvoice.LogHelper;
import com.scut.srp.acousticpayment.sinvoice.SinVoicePlayer;
import com.scut.srp.acousticpayment.sinvoice.SinVoiceRecognition;

import java.util.Random;

public class Test extends AppCompatActivity implements SinVoicePlayer.Listener, VoiceInHelper.Listener {
    private final static String TAG = "TestActivity";

    private final static String CODEBOOK = "1234567";

    private SinVoicePlayer mSinVoicePlayer;
    private SinVoiceRecognition mRecognition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mSinVoicePlayer = new SinVoicePlayer(CODEBOOK);
        mSinVoicePlayer.setListener(this);

        mRecognition = new SinVoiceRecognition(CODEBOOK);
        mRecognition.setListener(new VoiceInHelper.RecognitionListener(this));

        final TextView playTextView = (TextView) findViewById(R.id.playtext);

        Button playStart = (Button) this.findViewById(R.id.start_play);
        playStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String text = genText(7);
                playTextView.setText(text);
                mSinVoicePlayer.play(VoiceOutHelper.modify(text), true, 1000);
            }
        });

        Button playStop = (Button) this.findViewById(R.id.stop_play);
        playStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mSinVoicePlayer.stop();
            }
        });

        Button recognitionStart = (Button) this.findViewById(R.id.start_reg);
        recognitionStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mRecognition.start();
            }
        });

        Button recognitionStop = (Button) this.findViewById(R.id.stop_reg);
        recognitionStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mRecognition.stop();
            }
        });
    }
    private String genText(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(new Random().nextInt(9));
        }

        return sb.toString();
    }

    @Override
    public void onRegStart() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView recognisedTextView = (TextView) findViewById(R.id.regtext);
                recognisedTextView.setText("");
            }
        });
        Log.v(TAG, "start");
    }

    @Override
    public void onResult(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView recognisedTextView = (TextView) findViewById(R.id.regtext);
                recognisedTextView.setText(s);
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

}
