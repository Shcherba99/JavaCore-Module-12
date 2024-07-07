package com.goit.javacore.module12.pshcherba;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Task2 {
    private final int n;
    private static final int FIRST_DIVISOR = 3;
    private static final int SECOND_DIVISOR = 5;

    private final AtomicInteger count = new AtomicInteger(1);
    private final Queue<String> queue = new ConcurrentLinkedQueue<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public Task2(int n) {
        this.n = n;
    }

    public void runTask() {
        Thread threadA = new Thread(this::fizz);
        Thread threadB = new Thread(this::buzz);
        Thread threadC = new Thread(this::fizzBuzz);
        Thread threadD = new Thread(this::number);

        threadA.start();
        threadB.start();
        threadC.start();
        threadD.start();

        try {
            threadA.join();
            threadB.join();
            threadC.join();
            threadD.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void fizz() {
        while (true) {
            lock.lock();
            try {
                while (!(isDivisibleByFirst(count.get()) && !isDivisibleBySecond(count.get()))) {
                    if (isReturn()) {
                        return;
                    }
                    condition.await();
                }
                if (isReturn()) {
                    return;
                }
                queue.add("fizz");
                count.incrementAndGet();
                condition.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    private void buzz() {
        while (true) {
            lock.lock();
            try {
                while (!(isDivisibleBySecond(count.get()) && !isDivisibleByFirst(count.get()))) {
                    if (isReturn()) {
                        return;
                    }
                    condition.await();
                }
                if (isReturn()) {
                    return;
                }
                queue.add("buzz");
                count.incrementAndGet();
                condition.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    private void fizzBuzz() {
        while (true) {
            lock.lock();
            try {
                while (!isDivisibleByFirstAndSecond(count.get())) {
                    if (isReturn()) {
                        return;
                    }
                    condition.await();
                }
                if (isReturn()) {
                    return;
                }
                queue.add("fizzbuzz");
                count.incrementAndGet();
                condition.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    private void number() {
        while (true) {
            lock.lock();
            try {
                while (isDivisibleByFirst(count.get()) || isDivisibleBySecond(count.get())) {
                    if (isReturn()) {
                        System.out.println(queue);
                        return;
                    }
                    condition.await();
                }
                if (isReturn()) {
                    System.out.println(queue);
                    return;
                }
                queue.add(String.valueOf(count.get()));
                count.incrementAndGet();
                condition.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    private boolean isDivisibleByFirst(int number) {
        return number % FIRST_DIVISOR == 0;
    }

    private boolean isDivisibleBySecond(int number) {
        return number % SECOND_DIVISOR == 0;
    }

    private boolean isDivisibleByFirstAndSecond(int number) {
        return isDivisibleByFirst(number) && isDivisibleBySecond(number);
    }

    private boolean isReturn() {
        return count.get() > n;
    }
}
