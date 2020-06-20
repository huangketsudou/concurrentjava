package com.jiedong.Introduction;

import com.jiedong.annotations.*;

@ThreadSafe
public class Sequence {
    @GuardedBy("this")
    private int Value;
    public synchronized int getNext(){
        return Value++;
    }
}
