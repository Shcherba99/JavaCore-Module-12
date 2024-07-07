package com.goit.javacore.module12.pshcherba;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Task1 {
    private final int stopwatchDuration;
    private static final long ONE_SECOND = 1000;
    private static final long FIVE_SECONDS = 5000;
    private final AtomicInteger count = new AtomicInteger(0);

    public Task1(int stopwatchDuration) {
        this.stopwatchDuration = stopwatchDuration;

    }

    public void runStopwatch() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(() -> {
            while (isActive()) {
                try {
                    count.incrementAndGet();
                    Thread.sleep(ONE_SECOND);
                    if (count.get() % 5 != 0) {
                        System.out.println(count);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        });

        executorService.submit(() -> {
            while (isActive()) {
                try {
                    Thread.sleep(FIVE_SECONDS);
                    System.out.println("5 seconds have passed");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        });

        executorService.shutdown();
    }

    private boolean isActive() {
        return count.get() < stopwatchDuration;
    }
}
