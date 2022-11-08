package org.thread.study.task.future;

import org.thread.study.task.Result;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * @author Steven
 * @date 2022年11月08日 22:49
 */
public class Task implements Callable<Result> {

    private long startTime;

    private int jobId;

    public Task(long startTime,int jobId) {
        this.startTime = startTime;
        this.jobId=jobId;
    }

    @Override
    public Result call()  {
        Result<String> build = Result.<String>builder().startTime(startTime).build();
        System.out.println("开始执行----"+jobId);
        int i = new Random().nextInt(10000);
        try {
            Thread.sleep(i);
        } catch (InterruptedException exception) {
            /*System.out.println("被取消任务----"+jobId);*/
        }
        System.out.println("执行结束----"+jobId);
        return build;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }
}
