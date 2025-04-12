package org.example;

import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello, World!");

        var threadPool = Executors.newFixedThreadPool(24);
        var blockingQueue = new PriorityBlockingQueue<Event>();
        var eventsProduced = new AtomicInteger();
        var eventsProcessed = new AtomicInteger();
        var eventsDropped = new AtomicInteger();
        var lowPriorityEventsCount = new AtomicInteger();
        var highPriorityEventsCount = new AtomicInteger();

        var consumers = IntStream.range(0, 3).mapToObj(_ -> new Consumer(blockingQueue, eventsProcessed,
                lowPriorityEventsCount, highPriorityEventsCount)).collect(Collectors.toList());
        consumers.forEach(threadPool::execute);

        var producers = IntStream.range(0, 5).mapToObj(_ -> new Producer(blockingQueue,
                eventsProduced, eventsDropped, lowPriorityEventsCount, highPriorityEventsCount)).toList();
        producers.forEach(threadPool::execute);

        for (int i = 0; i < 10; i++) {
            System.out.println("Queue size: " + blockingQueue.size());
            System.out.println("Events produced: " + eventsProduced.get());
            System.out.println("Events processed: " + eventsProcessed.get());
            System.out.println("Low priority events: " + lowPriorityEventsCount);
            System.out.println("High priority events: " + highPriorityEventsCount);
            System.out.println("Consumers: " + consumers.size());
            if (blockingQueue.size() > 50 && consumers.size() < 19) {
                var cons = new Consumer(blockingQueue, eventsProcessed,
                        lowPriorityEventsCount, highPriorityEventsCount);
                consumers.add(cons);
                threadPool.execute(cons);
            }
            if (blockingQueue.size() < 10 && consumers.size() > 1) {
                var cons = consumers.removeLast();
                cons.setShutdown(true);
            }
            Thread.sleep(1000);
        }

//          consumers.forEach(it -> it.setShutdown(true));
            producers.forEach(it -> it.setShutdown(true));
            threadPool.shutdown();
    }
}
