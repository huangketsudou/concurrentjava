package com.jiedong.taskexecution;

import java.util.concurrent.Executor;

/**
 * @author 19411
 * @date 2020/06/22 21:16
 **/
public class WithinThreadExecutor implements Executor {
    @Override
    public void execute(Runnable r) {
        r.run();
    };
}
