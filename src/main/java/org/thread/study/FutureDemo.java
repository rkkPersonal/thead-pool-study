package org.thread.study;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

/**
 * @author Steven
 */
public class FutureDemo {

    public static void main(String [] args) throws InterruptedException, ExecutionException, TimeoutException, IOException {


        long start = System.currentTimeMillis();
        Stream.of(1, 2, 3, 4)
                .map(d -> CompletableFuture.supplyAsync(() -> d * FutureDemo.processor()))
                .parallel()
                .map(CompletableFuture::join).forEach(System.out::println);
        System.out.println("time costs -> " + (System.currentTimeMillis() - start) + "ms");

  /*      CompletableFuture<Double> future = new CompletableFuture<>();
        new Thread(() -> future.complete(FutureDemo.get())).start();
        System.out.println("main thread -> I am not blocked");
        future.whenComplete((v, t) -> {
            Optional.ofNullable(v).ifPresent(System.out::println);
            Optional.ofNullable(t).ifPresent(Throwable::printStackTrace);
        });
        System.out.println("main thread -> I am not blocked");*/
    }

    private static void complete4() throws IOException {
        CompletableFuture.supplyAsync(FutureDemo::processor)
                .whenComplete((v, t) -> System.out.println("whenComplete value is -> " + v))
                .thenCompose(i -> CompletableFuture.supplyAsync(() -> i + 10))//模拟 将结果加10处理
                .thenAccept((v) -> System.out.println("thenAccept value is -> " + v))
                .thenRun(() -> System.out.println("thenRun -> do some end task"));

        System.in.read();
    }

    private static void complete3() throws InterruptedException, ExecutionException {
        CompletableFuture<Double> doubleCompletableFuture = CompletableFuture.supplyAsync(FutureDemo::processor);
        Double aDouble = doubleCompletableFuture.get();
        System.out.println("b-"+aDouble);
    }

    private static void complete2() throws InterruptedException, ExecutionException {
        CompletableFuture<Double> doubleCompletableFuture = CompletableFuture.completedFuture(processor());

        Double aDouble = doubleCompletableFuture.get();

        System.out.println("double:"+aDouble);
    }

    private static void complete() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture objectCompletableFuture = new CompletableFuture<>();
        objectCompletableFuture.complete(FutureDemo.processor());
        Object o = objectCompletableFuture.get(3L,TimeUnit.SECONDS);
        System.out.println("oooo:"+o);
    }

    static double processor() {
        Random random = new Random();
        int i = random.nextInt(10);

        try {
            Thread.sleep(i*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return random.nextDouble();
    }
}
