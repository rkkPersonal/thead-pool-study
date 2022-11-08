package org.thread.study.task.future;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author Steven
 * @date 2022年11月08日 22:48
 */

public class TimeFuture<Result> extends FutureTask<Result> {

    private Task task;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TimeFuture(Callable callable) {
        super(callable);
        task = (Task) callable;
    }


}
