package org.example;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer implements Runnable {

    private final BlockingQueue<Event> queue;
    private final AtomicInteger eventsProduced;
    private final AtomicInteger eventsDropped;

    public Producer(ArrayBlockingQueue<Event> blockingQueue, AtomicInteger eventsProduced,
                    AtomicInteger eventsDropped) {
        queue = blockingQueue;
        this.eventsProduced = eventsProduced;
        this.eventsDropped = eventsDropped;
    }

    @Override
    public void run() {
        while (true) {
            var delay = ThreadLocalRandom.current().nextInt(1000) + 300;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            var event = new Event(UUID.randomUUID(), "ABC",
                    ThreadLocalRandom.current().nextInt(3000 + 500));
            eventsProduced.incrementAndGet();
            boolean insertionResult = queue.offer(event);

            if (!insertionResult) {
                eventsDropped.incrementAndGet();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
