package com.jiedong.cancellationandshutdown;

import com.jiedong.annotations.*;
import com.sun.tools.classfile.RuntimeAnnotations_attribute;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;

import static com.jiedong.BuildingBlocks.LaunderThrowable.launderThrowable;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author 19411
 * @date 2020/06/23 14:42
 **/
@ThreadSafe
public class PrimeGenerator implements Runnable {
    private static ExecutorService exec = Executors.newCachedThreadPool();

    @GuardedBy("this") private final List<BigInteger> primes
            = new ArrayList<BigInteger>();
    private volatile boolean cancelled;

    @Override
    public void run() {
        BigInteger p = BigInteger.ONE;
        while (!cancelled) {
            p = p.nextProbablePrime();
            synchronized (this) {
                primes.add(p);
            }
        }
    }

    public void cancel() {
        cancelled = true;
    }

    public synchronized List<BigInteger> get() {
        return new ArrayList<BigInteger>(primes);
    }

    static List<BigInteger> aSecondOfPrimes() throws InterruptedException {
        PrimeGenerator generator = new PrimeGenerator();
        exec.execute(generator);
        try {
            SECONDS.sleep(1);
        } finally {
            generator.cancel();
        }
        return generator.get();
    }

}
