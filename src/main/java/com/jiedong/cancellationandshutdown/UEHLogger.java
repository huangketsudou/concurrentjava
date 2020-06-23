package com.jiedong.cancellationandshutdown;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author 19411
 * @date 2020/06/23 17:20
 **/
public class UEHLogger implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Logger logger = Logger.getAnonymousLogger();
        logger.log(Level.SEVERE, "Thread terminated with exception: " + t.getName(), e);
    }
}
