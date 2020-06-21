package com.jiedong.ComposingObjects;

import com.jiedong.annotations.*;

@ThreadSafe
public class SafePoint {
    @GuardedBy("this") private int x, y;

    private SafePoint(int[] a) {
        this(a[0], a[1]);//禁止外部通过该方法实现实例
    }

    public SafePoint(SafePoint p) {
        this(p.get());//这里不可以写为this(p.x,p.y)原因在于p可能被其他的线程调用了set，且刚好就修改了x，y没有修改完成然后被该线程捕获，导致x，y的结果不正确。
    }

    public SafePoint(int x, int y) {
        this.set(x, y);
    }

    public synchronized int[] get() {
        return new int[]{x, y};
    }

    public synchronized void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
