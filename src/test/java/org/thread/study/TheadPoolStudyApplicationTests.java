package org.thread.study;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.thread.study.task.*;
import org.thread.study.task.service.CollectionTask;
import org.thread.study.task.future.Task;
import org.thread.study.task.future.TimeFuture;
import org.thread.study.task.handler.CancelPipeline;
import org.thread.study.task.handler.CompletedPipeline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class TheadPoolStudyApplicationTests {

    static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(10, 100, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10));

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<TimeFuture<Result>> timeFutureList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TimeFuture<Result> timeFuture = new TimeFuture<>(new CollectionTask(System.currentTimeMillis(), i));
            poolExecutor.execute(timeFuture);
            timeFutureList.add(timeFuture);
        }

        while (timeFutureList.size() > 0) {
            Iterator<TimeFuture<Result>> iterator = timeFutureList.iterator();
            while (iterator.hasNext()) {
                TimeFuture<Result> timeFuture = iterator.next();
                boolean isDone = timeFuture.isDone();
                Task task = timeFuture.getTask();
                if (!isDone) {
                    timeout(iterator, timeFuture, task);
                } else {
                    completed(iterator, timeFuture, task);
                }
            }
        }
        System.out.println("完成.....");
        poolExecutor.shutdown();

    }

    private static void completed(Iterator<TimeFuture<Result>> iterator, TimeFuture<Result> timeFuture, Task task) throws InterruptedException, ExecutionException {
        iterator.remove();
        Result result = timeFuture.get();
        if (task instanceof CompletedPipeline) {
            CompletedPipeline completedPipeline = (CompletedPipeline) task;
            completedPipeline.completed(result);
        }
    }

    private static void timeout(Iterator<TimeFuture<Result>> iterator, TimeFuture<Result> timeFuture, Task task) {
        long startTime = task.getStartTime();
        int jobId = task.getJobId();
        long currentTime = System.currentTimeMillis();
        long costTime = System.currentTimeMillis() - startTime;
        if (costTime > 2000) {
            System.err.println("-- 任务超时 ,costTime : " + costTime + ", jobId:" + jobId + ",startTime:" + startTime + ",currentTime:" + currentTime);
            timeFuture.cancel(true);
            iterator.remove();
            if (task instanceof CompletedPipeline) {
                CancelPipeline completedPipeline = (CancelPipeline) task;
                completedPipeline.cancel(jobId, startTime, costTime);
            }
        } else {
            Thread.yield();
        }
    }

}
