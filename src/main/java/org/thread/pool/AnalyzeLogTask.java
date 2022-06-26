package org.thread.pool;

import org.thread.pool.handler.CompleteHandler;
import org.thread.pool.handler.TaskCancelHandler;

import java.util.Random;

/**
 * @author Steven
 */
@PoolConfig(timeout = 2000, enabledCompleted = true)
public class AnalyzeLogTask extends Task implements CompleteHandler, TaskCancelHandler {

    public AnalyzeLogTask() {
        super();
    }

    @Override
    public Result exec() throws InterruptedException {

        Result exec = super.exec();
        int i = new Random().nextInt(10000);
        for (long j = 0; j < 100; j++) {
        }
        Thread.sleep(i);

        return exec;
    }

    @Override
    public void onComplete(long times, String taskId, Result result) throws Exception {

        System.out.println("【completed task】-----> costTime : [ " + times + " ] taskId: [ " + taskId + " ] result: [ " + result + " ]");
    }

    @Override
    public void handleCancel(String taskId, long costTime) throws Exception {
        System.out.println("【canceled task】-----> costTime : [ " + costTime + " ] taskId :[ " + taskId + " ]");
    }
}
