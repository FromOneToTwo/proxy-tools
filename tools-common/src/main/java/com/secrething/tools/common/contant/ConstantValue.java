package com.secrething.tools.common.contant;

/**
 * @author liuzz
 * @create 2018/3/14
 */
public interface ConstantValue {
    int HEAD_DATA = 3411234;
    String TEXT_PLAIN = "text/plain; charset=UTF-8";
    String TYPE_JSON = "application/json; charset=UTF-8";
    boolean IS_LINUX = System.getProperty("os.name").equals("Linux");
}
