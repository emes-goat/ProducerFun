package org.example;

public interface RequestThrottler {

    boolean allowRequest(String userId);
}
