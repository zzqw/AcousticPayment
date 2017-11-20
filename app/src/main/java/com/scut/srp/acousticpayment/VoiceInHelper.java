package com.scut.srp.acousticpayment;

import com.scut.srp.acousticpayment.sinvoice.SinVoiceRecognition;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tuivan on 2017/11/13.
 */

public class VoiceInHelper {
    private static final Map<String, String> inMap = new HashMap<>();

    static {
        inMap.put("14", "0");
        inMap.put("15", "1");
        inMap.put("16", "2");
        inMap.put("17", "3");
        inMap.put("24", "4");
        inMap.put("25", "5");
        inMap.put("26", "6");
        inMap.put("27", "7");
        inMap.put("34", "8");
        inMap.put("35", "9");
    }

    public interface Listener {
        void onRegStart();
        void onResult(String s);
    }

    public static final class RecognitionListener implements SinVoiceRecognition.Listener {
        Listener mListener;
        StringBuilder sb1, sb2;
        int i = 0;
        boolean isError = false;

        RecognitionListener(Listener listener) {
            mListener = listener;
        }

        @Override
        public void onRecognitionStart() {
            sb1 = new StringBuilder();
            sb2 = new StringBuilder();
            i = 0;
            isError = false;
            mListener.onRegStart();
        }

        @Override
        public void onRecognition(char ch) {
            if (!isError && null != sb1 && null != sb2) {
                sb2.append(ch);
                i++;
                if (i == 2) {
                    String s = inMap.get(sb2.toString());
                    sb2 = new StringBuilder();
                    if (null != s) {
                        sb1.append(s);
                    } else {
                        isError = true;
                    }
                    i = 0;
                }
            }
        }

        @Override
        public void onRecognitionEnd() {
            if (i == 0 && !isError) {
                mListener.onResult(sb1.toString());
            }
        }
    }
}
