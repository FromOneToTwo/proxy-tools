package com.secrething.tools.common.utils;

import java.security.MessageDigest;

/**
 * Created by liuzengzeng on 2017/12/22.
 */
public class MD5Util {
    private static MD5Util md5Util = new MD5Util();
    MessageDigest md5 = null;

    private MD5Util() {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            md5 = null;
        }
    }

    public static MD5Util getOneInstance() {
        return md5Util;
    }

    public synchronized String md5Gen(String origin) {
        byte[] byteArray = new byte[0];
        try {
            byteArray = origin.getBytes("UTF-8");
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public synchronized String md5Gen16B(String origin) {
        byte[] byteArray = new byte[0];
        try {
            byteArray = origin.getBytes("UTF-8");
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString().substring(8, 24);

        } catch (Exception e) {
            return null;
        }
    }
}
