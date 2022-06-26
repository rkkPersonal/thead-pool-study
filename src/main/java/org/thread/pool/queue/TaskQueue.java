package org.thread.pool.queue;


import lombok.Getter;
import lombok.Setter;
import org.thread.pool.Task;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class TaskQueue implements Delayed {
    public long time;
    private String name;
    private Thread thread;
    private Task task;
    private long startTime;

    public TaskQueue(String name, long time, TimeUnit unit) {
        this.name = name;
        this.time = System.currentTimeMillis() + (time > 0 ? unit.toMillis(time) : 0);
    }

    public TaskQueue(String name, long time, TimeUnit unit, Thread thread) {
        this.name = name;
        this.time = System.currentTimeMillis() + (time > 0 ? unit.toMillis(time) : 0);
        this.thread = thread;
    }

    public TaskQueue(String name, long time, TimeUnit unit, Task task, Thread thread,long startTime) {
        this.name = name;
        this.time = System.currentTimeMillis() + (time > 0 ? unit.toMillis(time) : 0);
        this.task = task;
        this.thread = thread;
        this.startTime = startTime;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return time - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        TaskQueue taskQueue = (TaskQueue) o;
        long diff = this.time - taskQueue.time;
        if (diff <= 0) {
            return -1;
        } else {
            return 1;
        }
    }
}
