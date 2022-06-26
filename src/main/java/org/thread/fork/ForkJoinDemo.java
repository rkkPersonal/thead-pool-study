package org.thread.fork;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @author Steven
 */
public class ForkJoinDemo {


    static List<String> urls = new ArrayList<>();

    static {
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");
        urls.add("https://www.baidu.com");

    }

    public static String doRequest(String url) {
        return "Steven ....resolved the " + url;
    }


    static class Job extends RecursiveTask<String> {

        private List<String> urls;

        private int start;

        private int end;

        private static final int DEFAULT = 10;

        public Job(List<String> urls, int start, int end) {
            this.urls = urls;
            this.start = start;
            this.end = end;
        }

        @Override
        protected String compute() {

            StringBuffer buffer = new StringBuffer();
            int count = (end - start) / 2;
            if (count < DEFAULT) {
                for (int i = 0; i < count; i++) {
                    String s = doRequest(urls.get(i));
                    buffer.append(s).append("\r\n");
                }
                return buffer.toString();
            }
            int x = (start+end) / 2;
            Job job1 = new Job(urls, start, x);
            job1.fork();
            Job job2 = new Job(urls, x, end);
            job2.fork();

            String join1 = job1.join();
            String join2 = job2.join();
            return buffer.append(join1).append(join2).toString();

        }
    }

    static ForkJoinPool pool = new ForkJoinPool(5, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);

    public static void main(String[] args) {

        try {
            ForkJoinTask<String> submit = pool.submit(new Job(urls, 0, urls.size()));
            String forkResult = submit.get();
            System.out.println(forkResult);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}
