package com.secrething.tools.common.utils;

/**
 * @author liuzz
 * @create 2018/3/14
 */
public class TypeUtil {
    public static Class getBasicType(Class clzz) {
        if (Integer.class.equals(clzz))
            return Integer.TYPE;
        if (Long.class.equals(clzz))
            return Long.TYPE;
        if (Double.class.equals(clzz))
            return Double.TYPE;
        if (Byte.class.equals(clzz))
            return Byte.TYPE;
        if (Short.class.equals(clzz))
            return Short.TYPE;
        if (Character.class.equals(clzz))
            return Character.TYPE;
        if (Float.class.equals(clzz))
            return Float.TYPE;
        if (Boolean.class.equals(clzz))
            return Boolean.TYPE;
        return clzz;

    }
}
