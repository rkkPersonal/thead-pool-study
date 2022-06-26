package org.thread.pool;

import lombok.SneakyThrows;
import org.reflections.Reflections;
import org.thread.pool.handler.CompleteHandler;
import org.thread.pool.handler.TaskCancelHandler;
import org.thread.pool.queue.DelayQueueManager;
import org.thread.pool.queue.TaskQueue;
import org.thread.pool.thread.ThreadPoolBuilder;

import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Steven
 */
public class TheadPoolExecutorManger<E> {

    private DelayQueueManager delayQueueManager = new DelayQueueManager();
    private static ThreadPoolExecutor poolExecutor = ThreadPoolBuilder.build(2);
    private static ThreadPoolExecutor wortPool = ThreadPoolBuilder.build(5);


    public static final Map<String, PoolConfig> initMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("org.thread");
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(PoolConfig.class);
        for (Class<?> aClass : typesAnnotatedWith) {
            String name = aClass.getName();
            PoolConfig annotation = aClass.getAnnotation(PoolConfig.class);
            initMap.put(name, annotation);
        }
    }

    public List<Result<E>> submit(List<Task<E>> taskList) throws ExecutionException, InterruptedException {

        List<Future<Result<E>>> list = new ArrayList<>();
        Task<E> task = taskList.get(0);
        for (Task<E> eTask : taskList) {
            if (!initMap.containsKey(eTask.getClass().getName())) {
                break;
            }
            PoolConfig poolConfig = initMap.get(eTask.getClass().getName());
            int timeout = poolConfig.timeout();
            boolean b = poolConfig.enabledCompleted();
            eTask.setDelayQueueManager(delayQueueManager);
            eTask.setTimeout(timeout);
            eTask.setStartTime(System.currentTimeMillis());
            eTask.setId(String.valueOf(new Random().nextInt(1000)));
            Future<Result<E>> submit = poolExecutor.submit(eTask);
            eTask.setResultFuture(submit);
            if (poolConfig.enabledCompleted()) {
                list.add(submit);
            }

        }

        TaskResultThread taskResultThread = new TaskResultThread("workPoolDetector", list, task);
        taskResultThread.setDaemon(true);
        taskResultThread.start();
        return null;
    }


    class TaskResultThread extends Thread {
        private List<Future<Result<E>>> list;
        private Task task;


        public TaskResultThread(String name, List<Future<Result<E>>> list, Task<E> task) {
            super(name);
            this.list = list;
            this.task = task;
        }

        public TaskResultThread(List<Future<Result<E>>> list, Task<E> task) {
            this.list = list;
            this.task = task;
        }

        public TaskResultThread(List<Future<Result<E>>> list) {
            this.list = list;
        }


        @SneakyThrows
        @Override
        public void run() {
            wortPool.execute(() -> {
                try {
                    timeoutTaskClear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            handleResult(list);
        }

        private void timeoutTaskClear() throws Exception {
            while (true) {
                if (delayQueueManager.size() == 0) {
                    Thread.yield();
                    continue;
                }
                TaskQueue task = delayQueueManager.poll();
                if (task != null) {
                    long costTime = System.currentTimeMillis() - task.getStartTime();
                    Future resultFuture = task.getTask().getResultFuture();
                    if (resultFuture.isDone()) {
                        continue;
                    }

                    if (resultFuture.isCancelled()) {
                        handleCancel(costTime, task.getTask().getId());
                        continue;
                    }

                    resultFuture.cancel(true);
                    /*System.out.format("任务:{%s}被取消, 取消时间:{%s}  中断状态:{%s}\n",
                            task.getName(),
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            resultFuture.isCancelled());*/
                    handleCancel(costTime, task.getTask().getId());
                }
            }
        }

        private void handleResult(List<Future<Result<E>>> list) throws Exception {
            while (list.size() > 0) {
                Iterator<Future<Result<E>>> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Future<Result<E>> next = iterator.next();
                    if (next.isDone()) {
                        Result<E> eResult = null;
                        try {
                            eResult = next.get();
                        } catch (java.util.concurrent.CancellationException e) {
                            iterator.remove();
                            continue;
                        }
                        iterator.remove();
                        String taskId = eResult.getTaskId();
                        handleCompleted(eResult.getCostTime(), taskId, eResult);
                    } else if (next.isCancelled()) {
                        iterator.remove();
                    }
                }
            }
            System.out.println("finished........");
        }


        private void handleCompleted(long costTime, String taskId, Result<E> eResult) throws Exception {
            CompleteHandler completeHandler = (CompleteHandler) this.task;
            completeHandler.onComplete(costTime, taskId, eResult);
        }

        private void handleCancel(long costTime, String taskId) throws Exception {
            TaskCancelHandler completeHandler = (TaskCancelHandler) this.task;
            completeHandler.handleCancel(taskId, costTime);
        }
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        /*threadMonitor();*/


        TheadPoolExecutorManger theadPoolExecutorManger = new TheadPoolExecutorManger();
        List<Task> taskList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            taskList.add(new AnalyzeLogTask());
        }
        theadPoolExecutorManger.submit(taskList);


    }

    private static void threadMonitor() {
        new Thread(() -> {
            while (true) {

                int activeCount = poolExecutor.getActiveCount();
                System.out.println("active count " + activeCount);
                System.out.println(poolExecutor.getCorePoolSize());
                System.out.println(poolExecutor.getCompletedTaskCount());
                System.out.println(poolExecutor.getPoolSize());
                System.out.println(poolExecutor.getMaximumPoolSize());
                System.out.println(poolExecutor.getTaskCount());
                System.out.println(poolExecutor.getLargestPoolSize());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (activeCount == 0) {
                    break;
                }

            }
        }).start();
    }
}
