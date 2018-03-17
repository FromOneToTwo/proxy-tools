package com.secrething.tools.common.utils;

import java.math.BigInteger;

/**
 * Created by liuzengzeng on 2017/12/11.
 */
public class IDFactory {
    private static class SingletonHolder {
        private static BigInteger INSTANCE = new BigInteger("0");
    }

    private IDFactory() {
        if (null != SingletonHolder.INSTANCE)
            throw new RuntimeException("singleton instance already exist");
    }

    public static final String getMessageUID() {
        synchronized (SingletonHolder.INSTANCE){
            try {
                return IDGenUtil.getFullInstance().toSeriaString64(SingletonHolder.INSTANCE.toString(),16);
            }finally {
                SingletonHolder.INSTANCE = SingletonHolder.INSTANCE.add(new BigInteger("1"));
            }
        }
    }

    private Object readResolve() {
        return SingletonHolder.INSTANCE;
    }
}
