package org.thread.pool.thread;

import org.thread.pool.queue.TaskQueue;

import java.sql.Time;
import java.util.concurrent.*;

public class ThreadPoolBuilder {

    public static ThreadPoolExecutor build(int corePoolSize) {
        return new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "test-pool-job-" + r.hashCode());
            }
        }, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.out.println("================" + new Thread(r).getName());
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "test-pool-job-" + r.hashCode());
            }
        }, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.out.println("================" + new Thread(r).getName());
            }
        });


        DelayQueue<TaskQueue> delayQueue = new DelayQueue<>();

        poolExecutor.submit(() -> {
            TaskQueue taskQueue = new TaskQueue("null", 1000, TimeUnit.MILLISECONDS, Thread.currentThread());
            taskQueue.setName(Thread.currentThread().getName());
            delayQueue.add(taskQueue);
            try {
                Thread.sleep(500);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        });

        poolExecutor.submit(() -> {
            TaskQueue taskQueue = new TaskQueue("null", 2000, TimeUnit.MILLISECONDS, Thread.currentThread());
            taskQueue.setName(Thread.currentThread().getName());
            delayQueue.add(taskQueue);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        });

        Thread.sleep(500);
        int i = 0;
        while (delayQueue.size() > 0) {
            TaskQueue poll = delayQueue.poll();
            if (poll != null) {
                Thread thread = poll.getThread();
                String name = thread.getState().name();
                System.out.println(thread.getName() + "-----------" + name);
                i++;
                if (i == 2) {
                    thread.interrupt();
                }

            }
        }


    }


}


