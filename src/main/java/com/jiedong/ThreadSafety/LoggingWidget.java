package com.jiedong.ThreadSafety;

public class LoggingWidget extends Widget{
    public synchronized void dosomething(){
        System.out.println();
        super.dosomething();
    }
}
