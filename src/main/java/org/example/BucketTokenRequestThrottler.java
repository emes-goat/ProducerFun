package org.example;

import java.util.concurrent.ConcurrentHashMap;

public class BucketTokenRequestThrottler implements RequestThrottler {

    private final static Integer REQUESTS_PER_USER = 3;
    private final ConcurrentHashMap<String, Integer> buckets;

    public BucketTokenRequestThrottler(ConcurrentHashMap<String, Integer> buckets) {
        this.buckets = buckets;
    }

    @Override
    public boolean allowRequest(String userId) {
        synchronized (this) {
            Integer numberOfRequests = buckets.getOrDefault(userId, -1);
            if (numberOfRequests < REQUESTS_PER_USER) {
                buckets.compute(userId, (_, value) -> value == null ? 1 : value + 1);
                return true;
            } else {
                return false;
            }
        }
    }
}
