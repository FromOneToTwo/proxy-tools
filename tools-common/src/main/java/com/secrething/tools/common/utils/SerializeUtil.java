package com.secrething.tools.common.utils;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * Created by liuzengzeng on 2017/12/10.
 */
public class SerializeUtil {

    public static <T> byte[] serialize(T t) {
        try {

            if (null == t) {
                throw new NullPointerException();
            }
            Class<T> clzz = (Class<T>) t.getClass();
            RuntimeSchema<T> schema = RuntimeSchema.createFrom(clzz);
            byte[] bs = ProtostuffIOUtil.toByteArray(t, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            return bs;
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T deserialize(byte[] bs, Class<T> clzz) {
        try {
            RuntimeSchema<T> schema = RuntimeSchema.createFrom(clzz);
            T t = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(bs, t, schema);
            return t;
        } catch (Exception e) {
            return null;
        }
    }
}
