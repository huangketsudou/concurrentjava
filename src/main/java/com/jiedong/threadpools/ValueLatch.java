package com.jiedong.threadpools;

import com.jiedong.annotations.*;

import java.util.concurrent.CountDownLatch;

/**
 * @author 19411
 * @date 2020/06/25 23:28
 **/
@ThreadSafe
public class ValueLatch <T> {
    @GuardedBy("this") private T value = null;
    private final CountDownLatch done = new CountDownLatch(1);

    public boolean isSet() {
        return (done.getCount() == 0);
    }

    public synchronized void setValue(T newValue) {
        if (!isSet()) {
            value = newValue;
            done.countDown();
        }
    }

    public T getValue() throws InterruptedException {
        done.await();
        synchronized (this) {
            return value;
        }
    }
}
