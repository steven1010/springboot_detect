package com.skspruce.detect.utils;

/**
 *
 */
public class MacUtil {

    public static void main(String[] args) {
        System.out.println(formatMac("aabbcceeff"));
    }

    /**
     * 将不带':'的mac转为正常mac地址
     *
     * @param mac
     * @return
     */
    public static String formatMac(String mac) {
        if(mac.length() != 12){
            return "00:00:00:00:00:00";
        }
        StringBuilder fmac = new StringBuilder();
        char[] chars = mac.toCharArray();
        int length = chars.length;
        int limit = length - 1;
        for (int i = 0; i < length; i++) {
            fmac.append(chars[i]);
            if (i % 2 == 1 && i < limit && i > 0) {
                fmac.append(":");
            }
        }

        return fmac.toString();
    }
}
