package org.apd.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ThreadPool type.
 */
public class ThreadPool {
    private final Worker[] threads;
    private final BlockingQueue<Runnable> operationsQueue;
    private volatile boolean isRunning = true;

    public ThreadPool(int numberOfThreads) {
        operationsQueue = new LinkedBlockingQueue<>();
        threads = new Worker[numberOfThreads];
        
        /*  When instantiating the thread pool,
        all threads are started. */
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Worker();
            threads[i].start();
        }
    }

    public void submit(Runnable operation) {
        /* Adds a new task in the queue. */
        try {
            operationsQueue.put(operation);
        } catch (Exception e) {
            e.printStackTrace();    
        }
    }

    public void shutdown() {
        /* Waits for the threads to finish
        the tasks in the queue. */
        isRunning = false;

        for (Worker thread : threads) {
            try {
                thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class Worker extends Thread {
        public void run() {
            /* Extracts the first queued task and executes it */
            while (isRunning || !operationsQueue.isEmpty()) {
                Runnable operation = operationsQueue.poll();
                
                if (operation != null) {
                    operation.run();
                }
            }
        }
    }
}
