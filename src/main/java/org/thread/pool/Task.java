package org.thread.pool;

import lombok.Getter;
import lombok.Setter;
import org.thread.pool.queue.DelayQueueManager;
import org.thread.pool.queue.TaskQueue;

import java.util.concurrent.Callable;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Steven
 */
@Getter
@Setter
public abstract class Task<E> implements Callable<Result<E>> {

    private DelayQueueManager delayQueueManager;

    protected String id;

    private long timeout;

    private volatile long startTime;

    private Future<Result<E>> resultFuture;

    protected Task() {
        super();
    }

    @Override
    public Result<E> call() {
        Result<E> exec = null;
        long startTime = System.currentTimeMillis();
        try {
            setDelayQueue(startTime);
            exec = exec();
            exec.setSuccess(true);
            exec.setCostTime(getCostTime(startTime));
            exec.setName(Thread.currentThread().getName());
            /*
            System.out.println("我完成了<<<<<--->>>>>>" + Thread.currentThread().getName() + "。。。。。。。。。。id [" + this.id + "], costTime [" + costTime + "]");
            */
            return exec;
        } catch (InterruptedException exception) {
            /*System.out.println("我被取消了--->" + Thread.currentThread().getName() + "。。。。。。。。。。id [" + this.id + "], costTime [" + costTime + "]");*/
            exec.setThrowable(exception);
            long costTime = getCostTime(startTime);
            exec.setSuccess(false);
            exec.setCostTime(costTime);
            throw new RuntimeException(exception.getMessage());
        }
    }

    private void setDelayQueue(long startTime) {
        String theadId = Thread.currentThread().getName();
        if (delayQueueManager.contains(theadId)) {
            System.out.println("我中断了，有冲突。。。。");
            Thread.currentThread().interrupt();
        } else {
            TaskQueue taskQueue = new TaskQueue(theadId, timeout, TimeUnit.MILLISECONDS, this, Thread.currentThread(), startTime);
            delayQueueManager.put(taskQueue);
        }


    }

    private long getCostTime(long startTime) {
        long costTime = System.currentTimeMillis() - startTime;
        return costTime;
    }

    public Result<E> exec() throws InterruptedException {
        return new Result<E>(String.valueOf(this.id), this.timeout, this.startTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", timeout=" + timeout +
                ", startTime=" + startTime +
                '}';
    }
}
