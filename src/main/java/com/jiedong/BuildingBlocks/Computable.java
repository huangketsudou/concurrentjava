package com.jiedong.BuildingBlocks;

public interface Computable<A,V> {
    V compute(A arg) throws InterruptedException;
}
