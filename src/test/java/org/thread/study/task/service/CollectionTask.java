package org.thread.study.task.service;

import org.thread.study.task.Result;
import org.thread.study.task.future.Task;
import org.thread.study.task.handler.CancelPipeline;
import org.thread.study.task.handler.CompletedPipeline;

import java.util.Random;

/**
 * @author Steven
 * @date 2022年11月09日 0:20
 */
public class CollectionTask extends Task implements CancelPipeline, CompletedPipeline {

    public CollectionTask(long startTime, int jobId) {
        super(startTime, jobId);
    }

    @Override
    public Result call() {
        int jobId = getJobId();
        Result<String> build = Result.<String>builder().startTime(getStartTime()).build();
        int i = new Random().nextInt(10000);
        try {
            Thread.sleep(i);

        build.setContent("我正在打篮球。。。。。。");
        if (!Thread.currentThread().isInterrupted()) {
            build.setRemark("我被中断了...，【【【不进行job更新操作..】】】");
        }
        } catch (InterruptedException exception) {
            System.err.println("被取消任务----" + jobId);
        }
        return build;
    }

    @Override
    public void cancel(int jobId, long startTime, long costTime) {
        System.out.println("JobId:" + jobId + ", 任务开始时间:" + startTime + ", 任务已经取消，消耗时间：" + costTime + ",进行任务更新 TimeOut ");
    }

    @Override
    public void completed(Result result) {
        System.out.println("任务完成了:" + result.getContent());
    }
}
