package com.jiedong.atomic;

import com.jiedong.annotations.ThreadSafe;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 19411
 * @date 2020/06/29 17:49
 **/
@ThreadSafe
public class AtomicPseudoRandom extends PseudoRandom {
    private AtomicInteger seed;

    AtomicPseudoRandom(int seed) {
        this.seed = new AtomicInteger(seed);
    }

    public int nextInt(int n) {
        while (true) {
            int s = seed.get();
            int nextSeed = calculateNext(s);
            if (seed.compareAndSet(s, nextSeed)) {
                int remainder = s % n;
                return remainder > 0 ? remainder : remainder + n;
            }
        }
    }
}