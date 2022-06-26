package org.thread.pool.test;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;

public class PoolTet {

    public static void main(String[] args) {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "test-pool-job-" + UUID.randomUUID().toString());
            }
        }, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.out.println("================" + new Thread(r).getName());
            }
        });


        Future<TimeTask> submit = poolExecutor.submit(new TimeTask(3000));

        int i = 0;
        while (true) {
            if (!submit.isDone()) {
                try {
                    System.out.println("Start ========" + LocalDateTime.now());
                    TimeTask timeTask = submit.get();
                    System.out.println("End  ========" + LocalDateTime.now());
                    long startTime = timeTask.getStartTime();
                    long timeout = timeTask.getTimeout();
                    long costTime = System.currentTimeMillis() - timeout;
                    System.out.println("jobid " + (i++) + "------>>>>>" + timeTask);
                    if (costTime > timeout) {
                        submit.cancel(true);
                        System.out.println("cancel " + costTime);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    static class TimeTask implements Callable<TimeTask> {

        private long timeout;

        private long startTime;

        private long endTime;

        private long costTime;

        public TimeTask() {
        }

        public TimeTask(long timeout) {
            this.timeout = timeout;
        }

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public long getCostTime() {
            return costTime;
        }

        public void setCostTime(long costTime) {
            this.costTime = costTime;
        }

        @Override
        public String toString() {
            return "TimeTask{" +
                    "timeout=" + timeout +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", costTime=" + costTime +
                    '}';
        }

        @Override
        public TimeTask call() throws Exception {
            this.startTime = System.currentTimeMillis();
            long l = new Random().nextInt(10000);
            Thread.sleep(l);
            this.endTime = System.currentTimeMillis() - this.startTime;
            System.out.println("costTime :" + l);
            return this;
        }
    }

    static class Result {

    }
}
