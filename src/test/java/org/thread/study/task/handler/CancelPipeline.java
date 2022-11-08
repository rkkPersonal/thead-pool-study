package org.thread.study.task.handler;

public interface CancelPipeline {

    public void cancel(int jobId,long startTime,long costTime);
}
