package org.apd.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {
    private final Worker[] threads;
    private final BlockingQueue<Runnable> operationsQueue;
    private volatile boolean isShutdown = false;

    public ThreadPool(int numberOfThreads) {
        operationsQueue = new LinkedBlockingQueue<>();
        threads = new Worker[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Worker();
            threads[i].start();
        }
    }

    public void submit(Runnable operation) {
        if (!isShutdown) {
            try {
                operationsQueue.put(operation);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void shutdown() {
        isShutdown = true;

        for (Worker thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public class Worker extends Thread {
        public void run() {
            while (!isShutdown) {
                try {
                    Runnable operation = operationsQueue.poll();
                    if (operation != null) {
                        operation.run();
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            while (!operationsQueue.isEmpty()) {
                Runnable operation = operationsQueue.poll();
                if (operation != null) {
                    operation.run();
                }
            }
        }
    }
}
