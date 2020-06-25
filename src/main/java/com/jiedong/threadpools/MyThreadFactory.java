package com.jiedong.threadpools;

import java.util.concurrent.ThreadFactory;

/**
 * @author 19411
 * @date 2020/06/25 22:10
 **/
public class MyThreadFactory implements ThreadFactory {
    private final String poolName;

    public MyThreadFactory(String poolName) {
        this.poolName = poolName;
    }

    public Thread newThread(Runnable runnable) {
        return new MyAppThread(runnable, poolName);
    }
}
