package com.scut.srp.acousticpayment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tuivan on 2017/11/13.
 */

public class VoiceOutHelper {
    private static final Map<Character, String> outMap = new HashMap<>();

    static {
        outMap.put('0', "123");
        outMap.put('1', "124");
        outMap.put('2', "125");
        outMap.put('3', "132");
        outMap.put('4', "134");
        outMap.put('5', "135");
        outMap.put('6', "142");
        outMap.put('7', "143");
        outMap.put('8', "145");
        outMap.put('9', "152");
    }

    public static String modify(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            sb.append(outMap.get(s.charAt(i)));
        }
        return sb.toString();
    }
}
