package org.example;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello, World!");

        var threadPool = Executors.newFixedThreadPool(9);
        var blockingQueue = new ArrayBlockingQueue<Event>(30);
        var eventsProduced = new AtomicInteger();
        var eventsProcessed = new AtomicInteger();
        var eventsDropped = new AtomicInteger();

        var consumers = IntStream.range(0, 3).mapToObj(_ -> new Consumer(blockingQueue
                , eventsProcessed));
        consumers.forEach(threadPool::execute);

        var producers = IntStream.range(0, 6).mapToObj(_ -> new Producer(blockingQueue,
                eventsProduced, eventsDropped));
        producers.forEach(threadPool::execute);

        while (true) {
            System.out.println("Queue size: " + blockingQueue.size());
            System.out.println("Queue remainingCapacity: " + blockingQueue.remainingCapacity());
            System.out.println("Events produced: " + eventsProduced.get());
            System.out.println("Events processed: " + eventsProcessed.get());
            System.out.println("Events dropped: " + eventsDropped.get());
            Thread.sleep(1000);
        }

//        try {
//            var terminationResult = threadPool.awaitTermination(10, TimeUnit.SECONDS);
//            System.out.println("TerminationResult: " + terminationResult);
//        } catch (InterruptedException e) {
//            threadPool.shutdownNow();
//        }
    }
}
