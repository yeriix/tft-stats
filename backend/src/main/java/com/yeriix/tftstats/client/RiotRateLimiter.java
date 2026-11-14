package com.yeriix.tftstats.client;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sliding window limiter for the Riot development key limits
 * (20 requests per second and 100 requests per 2 minutes), with a safety margin.
 */
@Component
public class RiotRateLimiter {

    private static final int MAX_SHORT = 18;
    private static final long WINDOW_SHORT_MS = 1_000;
    private static final int MAX_LONG = 95;
    private static final long WINDOW_LONG_MS = 120_000;

    private final List<Long> timestamps = new ArrayList<>();

    public synchronized void acquire() {
        while (true) {
            long now = System.currentTimeMillis();
            timestamps.removeIf(t -> now - t >= WINDOW_LONG_MS);

            long wait = 0;
            if (timestamps.size() >= MAX_LONG) {
                wait = Math.max(wait, WINDOW_LONG_MS - (now - timestamps.get(timestamps.size() - MAX_LONG)));
            }
            if (timestamps.size() >= MAX_SHORT) {
                wait = Math.max(wait, WINDOW_SHORT_MS - (now - timestamps.get(timestamps.size() - MAX_SHORT)));
            }

            if (wait <= 0) {
                timestamps.add(now);
                return;
            }

            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for the Riot rate limit", e);
            }
        }
    }
}