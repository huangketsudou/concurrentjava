package com.jiedong.ComposingObjects;

import com.jiedong.ThreadSafety.Widget;
import com.jiedong.annotations.*;

public class PrivateLock {
    private final Object myLock = new Object();
    @GuardedBy("myLock") Widget widget;

    void someMethod() {
        synchronized (myLock) {
            // Access or modify the state of widget
        }
    }
}
