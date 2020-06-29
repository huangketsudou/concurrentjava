package com.jiedong.explicitlocks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * @author 19411
 * @date 2020/06/28 20:27
 **/
public class TimedLocking {
    private Lock lock = new ReentrantLock();
    private ReadWriteLock lock1 = new ReentrantReadWriteLock();

    public boolean trySendOnSharedLine(String message,
                                       long timeout, TimeUnit unit)
            throws InterruptedException {
        long nanosToLock = unit.toNanos(timeout)
                - estimatedNanosToSend(message);
        if (!lock.tryLock(nanosToLock, NANOSECONDS))//等待的时间内不能拿到锁，就返回false
            return false;
        try {
            return sendOnSharedLine(message);
        } finally {
            lock.unlock();
        }
    }

    private boolean sendOnSharedLine(String message) {
        /* send something */
        return true;
    }

    long estimatedNanosToSend(String message) {
        return message.length();
    }
}