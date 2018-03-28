package com.secrething.tools.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by secret on 2018/3/27.
 */
public class Assert {
    private Assert() {
        throw new UnsupportedOperationException("should not new this instance");
    }

    public static void isBlank(String text, String message) {
        if (StringUtils.isNotBlank(text))
            throw new IllegalArgumentException(message);
    }
    public static void isBlank(String text) {
        if (StringUtils.isNotBlank(text))
            throw new IllegalArgumentException("text not blank");
    }

    public static void notBlank(String text, String message) {
        if (StringUtils.isBlank(text))
            throw new IllegalArgumentException(message);
    }
    public static void notBlank(String text) {
        if (StringUtils.isBlank(text))
            throw new IllegalArgumentException("text is blank");
    }
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNull(Object object) {
        isNull(object, "[Assertion failed] - the object argument must be null");
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

}
