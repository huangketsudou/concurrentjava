package com.jiedong.atomic;

import com.jiedong.annotations.ThreadSafe;

/**
 * @author 19411
 * @date 2020/06/29 16:16
 **/
@ThreadSafe
public class CasCounter {
    private SimulatedCAS value;

    public int getValue() {
        return value.get();
    }

    public int increment() {
        int v;
        do {
            v = value.get();
        } while (v != value.compareAndSwap(v, v + 1));
        return v + 1;
    }
}