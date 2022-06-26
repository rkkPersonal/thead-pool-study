package org.thread.pool;

import lombok.Data;

/**
 *
 * Task执行结果
 *
 * @param <E> 结果数据
 *
 * @author shanhm1991@163.com
 *
 */
@Data
public class Result<E> implements Cloneable{

    private final String taskId;

    private final long timeout;

    private final long startTime;

    private boolean success = true;

    private long costTime;

    private String name;

    private E content;

    private Throwable throwable;

    Result(String sourceUri, long timeout, long startTime) {
        this.taskId = sourceUri;
        this.timeout = timeout;
        this.startTime = startTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public long getTimeout() {
        return timeout;
    }

    public long getStartTime() {
        return startTime;
    }

    void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    void setCostTime(long costTime) {
        this.costTime = costTime;
    }

    public long getCostTime() {
        return costTime;
    }

    void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    void setContent(E content) {
        this.content = content;
    }

    public E getContent() {
        return content;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<E> clone() throws CloneNotSupportedException {
        return (Result<E>)super.clone();
    }

}

