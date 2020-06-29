package com.jiedong.explicitlocks;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.*;

/**
 * @author 19411
 * @date 2020/06/28 20:41
 **/
public class InterruptibleLocking {
    private Lock lock = new ReentrantLock();
    public boolean sendOnSharedLine(String message)
            throws InterruptedException {
        lock.lockInterruptibly();
        try {
            return cancellableSendOnSharedLine(message);
        } finally {
            lock.unlock();
        }
    }

    private boolean cancellableSendOnSharedLine(String message) throws InterruptedException {
        /* send something */
        return true;
    }

}