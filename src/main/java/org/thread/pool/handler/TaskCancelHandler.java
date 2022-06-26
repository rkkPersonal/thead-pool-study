package org.thread.pool.handler;

/**
 * @author Steven
 */
public interface TaskCancelHandler {


    void handleCancel(String taskId, long costTime) throws Exception;
}
