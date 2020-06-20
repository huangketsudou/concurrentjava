package com.jiedong.Introduction;

import com.jiedong.annotations.NotThreadSafe;

@NotThreadSafe
public class UnsafeSequence {
    private int value;
    public int getNext(){
        return value++;
    }
}
