package org.example;

import java.util.UUID;

public record Event(UUID id, String type, Integer sleepTime, Integer priority) implements Comparable<Event> {
    @Override
    public int compareTo(Event o) {
        return o.priority.compareTo(this.priority);
    }
}
