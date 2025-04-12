package org.example;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Runnable {

    private final PriorityBlockingQueue<Event> queue;
    private final AtomicInteger eventsProcessed;
    private final AtomicInteger lowPriorityEventsCount;
    private final AtomicInteger highPriorityEventsCount;
    private volatile boolean shutdown = false;

    public Consumer(PriorityBlockingQueue<Event> queue, AtomicInteger eventsProcessed, AtomicInteger lowPriorityEventsCount,
                    AtomicInteger highPriorityEventsCount) {
        this.queue = queue;
        this.eventsProcessed = eventsProcessed;
        this.lowPriorityEventsCount = lowPriorityEventsCount;
        this.highPriorityEventsCount = highPriorityEventsCount;
    }

    @Override
    public void run() {
        try {
            while (!shutdown && !Thread.currentThread().isInterrupted()) {
                var event = queue.take();
                int i = event.priority() == 0 ? lowPriorityEventsCount.decrementAndGet() :
                        highPriorityEventsCount.decrementAndGet();
                Thread.sleep(event.sleepTime());
                eventsProcessed.incrementAndGet();
            }
            System.out.println("Killing consumer");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }
}
