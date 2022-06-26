package org.thread.pool.thread;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;
import org.thread.pool.AnalyzeLogTask;
import org.thread.pool.Task;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;

public class MyThreadTest {


    public static void main(String[] args) throws Exception {

      /*  System.out.println(VM.current().details());

        Task task = new AnalyzeLogTask();
        String layout=ClassLayout.parseInstance(task).toPrintable();
        System.out.println(layout);*/

        ThreadMXBean tmbean = ManagementFactory.getThreadMXBean();
        tmbean.setThreadContentionMonitoringEnabled(true);
        long[] allThread = tmbean.getAllThreadIds();
        System.out.println(Arrays.toString(allThread));

        while (true){

            Thread.sleep(2000);
            for (long l : allThread) {
                long threadCpuTime = tmbean.getThreadCpuTime(l);
                System.out.println(threadCpuTime);
            }

        }


     /*   ARunnable aRunnable1 = new ARunnable();
        Thread t1 = new Thread(aRunnable1);
        Thread t2 = new Thread(aRunnable1);
        t1.start();
        t2.start();

        Thread.sleep(2000);


        AThread aThread = new AThread();
        AThread aThread1 = new AThread();
        aThread.start();
        aThread1.start();*/
    }

}
