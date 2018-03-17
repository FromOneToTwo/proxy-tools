package com.secrething.tools.common.utils;


import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by liuzengzeng on 2017/12/6.
 */
public class DataContainer<K, D> {
    private D data;
    private Map<K, DataContainer<K, D>> children;
    private final boolean concurrent;
    private Sync sync;

    private DataContainer(boolean concurrent) {
        this.concurrent = concurrent;
        if (concurrent)
            this.sync = new RealLock();
        else
            this.sync = new Sync() {
                @Override
                public void lockToRead() {

                }

                @Override
                public void unlockToRead() {

                }

                @Override
                public void lockToWrite() {

                }

                @Override
                public void unlockToWrite() {

                }
            };
        check();
    }

    private DataContainer(boolean concurrent, D data) {
        this.concurrent = concurrent;
        this.data = data;
    }


    private final static <K, D> DataContainer<K, D> createChild(boolean concurrent, D data) {
        DataContainer<K, D> node = new DataContainer<>(concurrent, data);
        return node;
    }

    private final static <K, D> DataContainer<K, D> createParent(boolean concurrent) {
        return new DataContainer<K, D>(concurrent);
    }


    public final D getData() {
        return data;
    }

    public final Map<K, DataContainer<K, D>> getNodes() {
        return children;
    }

    public final static <K, D> DataContainer<K, D> createInstance() {
        return new DataContainer<K, D>(false);
    }

    public final static <K, D> DataContainer<K, D> createConcurrentInstance() {
        return new DataContainer<K, D>(true);
    }

    public final DataContainer<K, D> getNode(K... keys) {
        if (keysCantGet(keys))
            return null;
        if (keys.length == 1)
            try {
                sync.lockToRead();
                return this.children.get(keys[0]);
            } finally {
                sync.unlockToRead();
            }
        K key = keys[keys.length - 1];
        K[] parentKeys = Arrays.copyOf(keys, keys.length - 1);
        return getNodeSplitKey(key, parentKeys);

    }

    public final D getNodeData(K... keys) {
        if (keysCantGet(keys))
            return null;
        if (keys.length == 1)
            return getNodeDataSplitKey(keys[0]);
        int newlen = keys.length - 1;
        K key = selectKey(newlen, keys);
        K[] parentKeys = selectParentKeys(newlen, keys);
        return getNodeDataSplitKey(key, parentKeys);

    }

    public final void put(D data, K... keys) {
        if (keysCantPut(keys))
            return;
        if (keys.length == 1) {
            putSplitKey(keys[0], data);
            return;
        }
        int newlen = keys.length - 1;
        K key = selectKey(newlen, keys);
        K[] parentKeys = selectParentKeys(newlen, keys);
        putSplitKey(key, data, parentKeys);

    }

    private K selectKey(int index, K... keys) {
        return keys[index];
    }

    private K[] selectParentKeys(int len, K... keys) {
        K[] parentKeys = (K[]) Array.newInstance(keys[0].getClass(), len);
        System.arraycopy(keys, 0, parentKeys, 0, len);
        return parentKeys;
    }

    public final DataContainer<K, D> remove(K... keys) {
        if (null == keys || keys.length < 1 || null == children)
            return null;
        else if (keys.length == 1) {
            return this.children.remove(keys[0]);
        }
        K[] parent = selectParentKeys(keys.length - 1, keys);
        DataContainer<K, D> node = getNode(parent);
        return node == null ? null : node.children.remove(keys[keys.length - 1]);

    }

    private void check() {
        if (null != this.children)
            return;
        this.children = new HashMap<K, DataContainer<K, D>>();
    }

    private void putBase(K key, DataContainer<K, D> node, K... parentKeys) {
        if (null == key || null == node)
            throw new NullPointerException("key or node can't be null");

        DataContainer<K, D> next = this;
        next.check();
        if (null != parentKeys && parentKeys.length > 0) {
            for (int i = 0; i < parentKeys.length; i++) {
                K parentKey = parentKeys[i];
                DataContainer curr = next.children.get(parentKey);
                if (null == curr) {
                    curr = createParent(next.concurrent);
                    next.children.put(parentKey, curr);
                }
                next = curr;
            }
        }
        next.children.put(key, node);
    }

    private boolean hasChildren() {
        return null != this.children && !this.children.isEmpty();
    }

    /**
     * 查找最后一个节点,返回最后一个叶子节点
     *
     * @param index
     * @param parentKeys
     * @return Node
     */

    private DataContainer<K, D> findNode(K... parentKeys) {
        if (!hasChildren())
            return this;
        DataContainer<K, D> next = this;
        for (int i = 0; i < parentKeys.length; i++) {
            if (next.hasChildren()) {
                next = next.children.get(parentKeys[i]);
                if (null == next)
                    return null;
            }
            if (i == (parentKeys.length - 1) || !next.hasChildren()) {
                return next;
            }
        }
        return null;

    }

    private void putSplitKey(K key, D data, K... parentKeys) {
        try {
            sync.lockToWrite();
            DataContainer<K, D> node = createChild(this.concurrent, data);
            putBase(key, node, parentKeys);
        } finally {
            sync.unlockToWrite();
        }

    }


    private DataContainer<K, D> getNodeSplitKey(K key, K... parentKeys) {
        try {
            sync.lockToRead();
            if (null != parentKeys && parentKeys.length > 0) {
                DataContainer<K, D> node = findNode(parentKeys); //循环方式
                if (null == node || null == node.children)
                    return null;
                return node.children.get(key);
            }
            return children.get(key);
        } finally {
            sync.unlockToRead();
        }


    }

    private D getNodeDataSplitKey(K key, K... parentKeys) {
        DataContainer<K, D> node = getNodeSplitKey(key, parentKeys);
        if (null == node)
            return null;
        return node.getData();
    }

    private boolean keysCantPut(K... keys) {
        return null == keys || keys.length < 1;
    }

    private boolean keysCantGet(K... keys) {
        return keysCantPut(keys) || null == this.children;
    }

    interface Sync {
        void lockToRead();

        void unlockToRead();

        void lockToWrite();

        void unlockToWrite();
    }

    static class RealLock extends ReentrantReadWriteLock implements Sync {
        ReadLock readLock = readLock();
        WriteLock writeLock = writeLock();

        @Override
        public void lockToRead() {
            readLock.lock();
        }

        @Override
        public void unlockToRead() {
            readLock.unlock();
        }

        @Override
        public void lockToWrite() {
            writeLock.lock();
        }

        @Override
        public void unlockToWrite() {
            writeLock.unlock();
        }
    }
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
