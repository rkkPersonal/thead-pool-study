package org.thread.pool;

import java.util.*;
import java.util.concurrent.*;

public class PoolExceptionTest {


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

        List<Future> futureList = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {

            Future<Object> submit = poolExecutor.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {

                    try {
                        Thread.sleep(new Random().nextInt(100000));
                        System.out.println(Thread.currentThread().getName());
                    } finally {
                        countDownLatch.countDown();
                    }
                    return "hello";
                }
            });

            futureList.add(submit);
        }

        try {
            countDownLatch.await(1, TimeUnit.SECONDS);


      /*      while (futureList.size() > 0) {

                Iterator<Future> iterator = futureList.iterator();
                while (iterator.hasNext()) {
                    Future next = iterator.next();
                    if (next.isDone()) {
                        System.out.println(Thread.currentThread().getName());
                        iterator.remove();
                    } else {
                        Thread.yield();

                    }
                }

            }*/

            foreach(futureList);
            System.out.println("finished");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private static void foreach(List<Future> futureList) {
        for (int i = 0; i < futureList.size(); i++) {
            Future future = null;
            try {
                future = futureList.get(i);
                future.get(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            } catch (ExecutionException e) {
                e.printStackTrace();
                continue;
            } catch (TimeoutException e) {
                e.printStackTrace();
                future.cancel(true);
                continue;
            }

        }
    }
}
