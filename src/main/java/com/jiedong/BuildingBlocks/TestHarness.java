package com.jiedong.BuildingBlocks;

import java.util.concurrent.CountDownLatch;

public class TestHarness {
    public long timeTasks(int nThreads, final Runnable task)
            throws InterruptedException {
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(nThreads);//这个地方+1会导致endGate处阻塞，线程无法继续执行。

        for (int i = 0; i < nThreads; i++) {
            Thread t = new Thread() {
                public void run() {
                    try {
                        startGate.await();//阻塞，下面的try没法执行
                        try {
                            task.run();
                        } finally {
                            endGate.countDown();
                        }
                    } catch (InterruptedException ignored) {
                    }
                }
            };
            t.start();
        }

        long start = System.nanoTime();
        startGate.countDown();
        endGate.await();
        long end = System.nanoTime();
        return end - start;
    }

    public static void main(String[] args) throws InterruptedException {
        TestHarness testHarness = new TestHarness();
        long time = testHarness.timeTasks(3, new Runnable() {
            @Override
            public void run() {
                System.out.println("Anonymous");
            }
        });
        System.out.println(time);
    }
}
