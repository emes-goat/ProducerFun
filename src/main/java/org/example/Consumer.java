package org.example;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Runnable {

    private final BlockingQueue<Event> queue;
    private final AtomicInteger eventsProcessed;

    public Consumer(ArrayBlockingQueue<Event> queue, AtomicInteger eventsProcessed) {
        this.queue = queue;
        this.eventsProcessed = eventsProcessed;
    }

    @Override
    public void run() {
        try {
            while (true) {
                var event = queue.take();
                Thread.sleep(event.sleepTime());
                eventsProcessed.incrementAndGet();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
