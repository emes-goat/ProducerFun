package org.example;

import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer implements Runnable {

    private final PriorityBlockingQueue<Event> queue;
    private final AtomicInteger eventsProduced;
    private final AtomicInteger eventsDropped;
    private final AtomicInteger lowPriorityEventsCount;
    private final AtomicInteger highPriorityEventsCount;
    private volatile boolean shutdown = false;

    public Producer(PriorityBlockingQueue<Event> blockingQueue, AtomicInteger eventsProduced, AtomicInteger eventsDropped,
                    AtomicInteger lowPriorityEventsCount, AtomicInteger highPriorityEventsCount) {
        queue = blockingQueue;
        this.eventsProduced = eventsProduced;
        this.eventsDropped = eventsDropped;
        this.lowPriorityEventsCount = lowPriorityEventsCount;
        this.highPriorityEventsCount = highPriorityEventsCount;
    }

    @Override
    public void run() {
        while (!shutdown && !Thread.currentThread().isInterrupted()) {
            var delay = ThreadLocalRandom.current().nextInt(1000) + 300;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            var event = new Event(UUID.randomUUID(), "ABC", ThreadLocalRandom.current().nextInt(2000) + 500, ThreadLocalRandom.current().nextInt(2));
            eventsProduced.incrementAndGet();
            boolean insertionResult = queue.offer(event);
            int i = event.priority() == 0 ? lowPriorityEventsCount.incrementAndGet() : highPriorityEventsCount.incrementAndGet();

            if (!insertionResult) {
                eventsDropped.incrementAndGet();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("Killing producer");
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }
}
