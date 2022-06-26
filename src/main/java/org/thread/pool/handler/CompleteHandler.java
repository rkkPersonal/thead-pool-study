package org.thread.pool.handler;

import org.thread.pool.Result;

/**
 * @author Steven
 */
public interface CompleteHandler<E> {


    public void onComplete(long times, String taskId, Result<E> results) throws Exception;
}
