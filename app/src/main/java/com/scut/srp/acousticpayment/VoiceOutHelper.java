package com.scut.srp.acousticpayment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tuivan on 2017/11/13.
 */

public class VoiceOutHelper {
    private static final Map<Character, String> outMap = new HashMap<>();

    static {
        outMap.put('0', "14");
        outMap.put('1', "15");
        outMap.put('2', "16");
        outMap.put('3', "17");
        outMap.put('4', "24");
        outMap.put('5', "25");
        outMap.put('6', "26");
        outMap.put('7', "27");
        outMap.put('8', "34");
        outMap.put('9', "35");
    }

    public static String modify(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            sb.append(outMap.get(s.charAt(i)));
        }
        return sb.toString();
    }
}
