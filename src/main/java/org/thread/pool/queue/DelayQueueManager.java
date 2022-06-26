package org.thread.pool.queue;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.DelayQueue;

public class DelayQueueManager {

    private static DelayQueue<TaskQueue> delayQueue = new DelayQueue<>();

    private List<String> threadIdList = new CopyOnWriteArrayList<>();

    public DelayQueue<TaskQueue> getDelayQueue() {
        return delayQueue;
    }

    public int size() {
        return delayQueue.size();
    }


    public void put(TaskQueue taskQueue) {
        if (Optional.ofNullable(taskQueue).isPresent()) {
            delayQueue.put(taskQueue);
            threadIdList.add(taskQueue.getName());
        }
    }

    public TaskQueue poll() {
        TaskQueue poll = delayQueue.poll();
        if (poll!=null){
            String threadId = poll.getName();
            if (threadIdList.contains(threadId)) {
                threadIdList.remove(threadId);
            }
        }

        return poll;
    }

    public boolean contains(String threadId) {
        return threadIdList.contains(threadId);
    }


}
