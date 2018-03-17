package com.secrething.tools.common.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Created by liuzengzeng on 2017/12/26.
 */
public abstract class AbstractAdapterFuture<R> implements Future<R> {

    private final Sync sync = new Sync();
    private volatile R result;
    private final List<IFutureCallback> syncCallbacks = new ArrayList<>();
    private final List<AsyncCallProxy> asyncCallbacks = new ArrayList<>();

    protected AbstractAdapterFuture() {
    }

    protected AbstractAdapterFuture(R defaultResult) {
        this.result = defaultResult;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException("cancel unsupport");
    }

    public boolean isCancelled() {
        throw new UnsupportedOperationException("isCancelled unsupport");
    }

    public boolean isDone() {
        return sync.isDone();
    }

    public void done(R result) {
        this.result = result;
        invokeSyncCall();
        sync.release(1);
        invokeAsyncCall();
    }

    private void invokeSyncCall() {
        synchronized (syncCallbacks) {
            for (IFutureCallback callback : syncCallbacks)
                callback.call();
        }
    }

    private void invokeAsyncCall() {
        synchronized (asyncCallbacks) {
            for (AsyncCallProxy proxy : asyncCallbacks) {
                proxy.execute();
            }
        }
    }

    protected void addSyncCallback(IFutureCallback callback) {
        if (null == callback)
            throw new NullPointerException("callback can not be null");
        synchronized (syncCallbacks) {
            if (isDone()) {
                callback.call();
                return;
            }
            syncCallbacks.add(callback);
        }
    }

    protected void addAsyncCallback(final Executor taskExecutor, final IFutureCallback callback) {
        if (null == taskExecutor || null == callback)
            throw new NullPointerException("executor or callback can not be null");
        synchronized (asyncCallbacks) {
            if (isDone()) {
                taskExecutor.execute(new Runnable() {
                    public void run() {
                        callback.call();
                    }
                });
                return;
            }
            asyncCallbacks.add(new AsyncCallProxy(taskExecutor, callback));
        }
    }

    public R get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
        return result;
    }

    public R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        return result;
    }

    public R currResult() {
        return result;
    }

    private final class Sync extends AbstractQueuedSynchronizer {
        private final int RUNNING = 0;
        private final int COMPLETED = 1;

        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == COMPLETED;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == RUNNING)
                if (compareAndSetState(RUNNING, COMPLETED))
                    return true;
            return false;
        }

        private boolean isDone() {
            return getState() == COMPLETED;
        }
    }

    private final class AsyncCallProxy {
        Executor executor;
        IFutureCallback callback;

        private AsyncCallProxy(Executor executor, IFutureCallback callback) {
            this.executor = executor;
            this.callback = callback;
        }

        private void execute() {
            executor.execute(new Runnable() {
                public void run() {
                    callback.call();
                }
            });
        }
    }

}
