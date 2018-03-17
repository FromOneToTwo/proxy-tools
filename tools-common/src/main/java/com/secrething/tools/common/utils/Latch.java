package com.secrething.tools.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liuzengzeng on 2017/12/11.
 */
public class Latch {

    private static final Logger logger = LoggerFactory.getLogger(Latch.class);
    private Lock latch = new ReentrantLock();
    private Condition perone = latch.newCondition();
    public void await() {
        try {
            latch.lock();
            perone.await();
        } catch (InterruptedException e) {
            logger.error("lock  fail ", e);
        } finally {
            latch.unlock();
        }

    }
    public void await(long time, TimeUnit unit) {
        try {
            latch.lock();
            perone.await(time, unit);
        } catch (InterruptedException e) {
            logger.error("lock fail ", e);
        } finally {
            latch.unlock();
        }

    }
    public void signal() {
        try {
            latch.lock();
            perone.signal();
        } catch (Exception e) {
            logger.error("notify fail ", e);
        } finally {
            latch.unlock();
        }
    }
}
