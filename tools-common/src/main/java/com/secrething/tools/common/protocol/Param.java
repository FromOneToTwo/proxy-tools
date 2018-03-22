package com.secrething.tools.common.protocol;

import com.secrething.tools.common.utils.TypeUtil;

/**
 * @author liuzz
 * @create 2018/3/22
 */
public final class Param {
    private final Object target;
    final Type type;

    private Param(Object target, Type type) {
        this.target = target;
        this.type = type;
    }

    public Object getTarget() {
        return target;
    }

    public enum Type {
        BASIC,
        REFERENCE;
    }

    public Class getParamType() {
        Class clzz = target.getClass();
        if (Type.BASIC.equals(this.type)) {
            return TypeUtil.getBasicType(clzz);
        }
        return clzz;
    }

    public static Param basicParam(Object p) {

        return new Param(p, Type.BASIC);
    }

    public static Param referenceParam(Object p) {
        return new Param(p, Type.REFERENCE);
    }
}
