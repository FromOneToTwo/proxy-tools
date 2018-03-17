package com.secrething.tools.common.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by liuzengzeng on 2017/12/10.
 */
public class Base64Util {
    /**
     * 将 strVal进行 BASE64 编码
     *
     * @param noneBase64Str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encode(String noneBase64Str)
            throws Exception {
        try {
            return new String(Base64.encodeBase64(noneBase64Str.getBytes()),"UTF-8");
        } catch (Exception e) {
            throw e;
        }
    }

    public static String encode2Str(byte[] noneBase64Str)
            throws Exception {
        try {
            return new String(Base64.encodeBase64(noneBase64Str),"UTF-8");
        } catch (Exception e) {
            throw e;
        }
    }

    public static byte[] encode2ByteArray(byte[] noneBase64Str)
            throws Exception {
        try {
            return Base64.encodeBase64(noneBase64Str);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 将BASE64字符串恢复为 BASE64编码前的字符串
     *
     * @param base64Str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decode2Str(String base64Str)
            throws UnsupportedEncodingException {
        try {
            return new String(Base64.decodeBase64(base64Str.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
    }

    public static byte[] decode2ByteArray(String base64Str)
            throws UnsupportedEncodingException {
        try {
            return Base64.decodeBase64(base64Str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
    }
}
