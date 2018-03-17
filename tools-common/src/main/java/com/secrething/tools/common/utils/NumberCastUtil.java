package com.secrething.tools.common.utils;

/**
 * Created by liuzengzeng on 2017/12/16.
 */
public final class NumberCastUtil {


    public static int intCast(String cast, int defVal) {
        try {
            Integer i = Integer.valueOf(cast);
            if (null != i)
                return i;
        } catch (Exception e) {

        }
        return defVal;
    }
}
