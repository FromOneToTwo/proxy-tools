package com.secrething.tools.common.utils;

import com.secrething.tools.common.async.AbstractAdapterFuture;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * 性能应该不好
 * @author liuzengzeng
 * @create 2018/1/13
 */
public final class ConcurrentNoLockMap<K, V> {
    final HashMap<K, V> map;
    private volatile boolean inited = false;
    private Adapter adapter;

    private ConcurrentNoLockMap() {
        Thread invoker = Thread.currentThread();
        if (invoker != BOSS)
            throw new UnsupportedOperationException("not suport thread");
        CALCOUNT.incrementAndGet();
        this.map = new HashMap<>();
    }

    private void checkInit() {
        Thread invoker = Thread.currentThread();
        if (invoker != BOSS)
            throw new UnsupportedOperationException("not suport thread");
        if (null == adapter) {
            adapter = new Adapter();
        } else if (adapter.isAlive()) {
            adapter.shut();
            adapter = new Adapter();
        } else if (!adapter.isAlive()) {
            adapter = new Adapter();
        }
        adapter.start();
        inited = true;
    }

    private boolean isInited() {
        return inited;
    }
    private void shutdown() {
        Thread invoker = Thread.currentThread();
        if (invoker != BOSS)
            throw new UnsupportedOperationException("not suport thread");
        if (null != adapter && adapter.isAlive()) {
            adapter.shut();
            adapter = null;
        }
        inited = false;
    }

    public void destory() {
        InitFuture future = new InitFuture(1);
        FUTURE_DEQUE.offer(future);
        ParkUtil.unpark(BOSS);
    }
    public V get(Object key) {
        if (null == key)
            throw new NullPointerException("key can not be null");
        K k = null;
        try {
            k = (K) key;
        } catch (Exception e) {
            throw e;
        }
        Node<K, V> node = new Node<>(k, null, this.map, GET);
        adapter.queue.offer(node);
        while (!node.isCompleted())
            ParkUtil.park();
        if (null != node.getThrowable())
            throw new UnsupportedOperationException(node.getThrowable());
        return node.v;
    }

    public V put(K key, V val) {
        if (null == key)
            throw new NullPointerException("key can not be null");
        Node<K, V> node = new Node<>(key, null, this.map, PUT);
        adapter.queue.offer(node);
        while (!node.isCompleted())
            ParkUtil.park();
        if (null != node.getThrowable())
            throw new UnsupportedOperationException(node.getThrowable());
        return node.v;
    }

    public V remove(Object key) {
        if (null == key)
            throw new NullPointerException("key can not be null");
        K k = null;
        try {
            k = (K) key;
        } catch (Exception e) {
            throw e;
        }
        Node<K, V> node = new Node<>(k, null, this.map, REM);
        adapter.queue.offer(node);
        while (!node.isCompleted())
            ParkUtil.park();
        if (null != node.getThrowable())
            throw new UnsupportedOperationException(node.getThrowable());
        return node.v;
    }

    @Override
    protected void finalize() throws Throwable {
        if (null != adapter && adapter.isAlive()) {
            adapter.shut();
            CALCOUNT.decrementAndGet();
            adapter = null;
        }

    }

    static class InitFuture extends AbstractAdapterFuture<ConcurrentNoLockMap> {
        private static final int INIT = 0;
        private static final int SHUT = 1;
        private int operation;//init 0, shut 1

        public InitFuture(int operation) {
            this.operation = operation;
        }

        private void buildFail() {
            this.operation = 1;
            FUTURE_DEQUE.offer(this);
            ParkUtil.unpark(BOSS);
        }

        private int getOperation() {
            return operation;
        }
    }

    static class Node<K, V> {
        K key;
        V v;
        HashMap<K, V> map;
        int operation;
        volatile boolean completed = false;
        final Thread invoker = Thread.currentThread();
        private Throwable throwable;

        public Node(K key, V v, HashMap<K, V> map, int operation) {
            this.key = key;
            this.v = v;
            this.map = map;
            this.operation = operation;
        }

        Node<K, V> handle() {
            switch (operation) {
                case GET:
                    this.v = map.get(key);
                    break;
                case PUT:
                    this.v = map.put(key, v);
                    break;
                case REM:
                    this.v = map.remove(key);
                    break;
                default:
                    throw new UnsupportedOperationException("unsupport operation");
            }
            this.completed = true;
            LockSupport.unpark(invoker);
            return this;
        }

        boolean isCompleted() {
            return completed;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
        }
    }

    static class Adapter extends Thread {
        volatile boolean run = true;
        ConcurrentLinkedQueue<Node> queue = new ConcurrentLinkedQueue<>();

        @Override
        public void run() {
            while (isRun()) {
                Node node = queue.poll();
                for (; null == node && isRun(); node = queue.poll()) {
                    ParkUtil.park();
                }
                if (null != node) {
                    try {
                        node.handle();
                    } catch (Throwable e) {
                        node.setThrowable(e);
                        node.completed = true;
                        LockSupport.unpark(node.invoker);
                    }
                }

            }
        }

        boolean isRun() {
            return run;
        }

        void shut() {
            this.run = false;
            LockSupport.unpark(this);
        }
    }

    public static <K, V> ConcurrentNoLockMap<K, V> newInstance() {
        if (CALCOUNT.get() > MAX_INSTANCE_COUNT)
            throw new UnsupportedOperationException("too many instances");
        InitFuture future = new InitFuture(0);
        FUTURE_DEQUE.offer(future);
        ParkUtil.unpark(BOSS);
        try {
            ConcurrentNoLockMap<K, V> instance = future.get(1, TimeUnit.SECONDS);
            return instance;
        } catch (Exception e) {
            future.buildFail();
        }
        return null;
    }

    private static final int GET = 0x1;
    private static final int PUT = 0x2;
    private static final int REM = 0x3;
    private static final int MAX_INSTANCE_COUNT = 100;
    private static final AtomicInteger CALCOUNT = new AtomicInteger(0);
    private static final ConcurrentLinkedDeque<InitFuture> FUTURE_DEQUE = new ConcurrentLinkedDeque<>();
    private static final Thread BOSS = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                InitFuture future = FUTURE_DEQUE.poll();
                for (; null == future; future = FUTURE_DEQUE.poll())
                    ParkUtil.park(10, TimeUnit.SECONDS);
                if (null != future) {
                    switch (future.getOperation()) {
                        case InitFuture.INIT: {
                            ConcurrentNoLockMap concurrentNoLockMap = new ConcurrentNoLockMap();
                            concurrentNoLockMap.checkInit();
                            future.done(concurrentNoLockMap);
                            break;
                        }
                        case InitFuture.SHUT: {
                            ConcurrentNoLockMap concurrentNoLockMap = future.currResult();
                            if (null != concurrentNoLockMap)
                                concurrentNoLockMap.shutdown();
                        }
                    }
                }
            }

        }
    });

    static {
        BOSS.start();
    }

    public static void main(String[] args) {
        ConcurrentNoLockMap<String, String> noLockMap = null;
        for (int i = 0; i < 3000; i++) {
            try {
                System.out.println(i);
                noLockMap = newInstance();
            } catch (Exception e) {
                System.gc();
            }

            ParkUtil.park(2, TimeUnit.MILLISECONDS);
        }
        noLockMap = null;
        System.out.println("10000次跑完了");
        System.out.println(CALCOUNT.get());
        while (0 < CALCOUNT.get()) {
            System.out.println(CALCOUNT.get());
            System.gc();
            ParkUtil.park(1, TimeUnit.SECONDS);
        }
    }
}
