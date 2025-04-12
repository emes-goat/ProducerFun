package org.example;

import java.util.UUID;

public record Event(
        UUID id,
        String type,
        Integer sleepTime
) {
}
