package org.apd.priority;

import java.util.ArrayList;
import java.util.List;

public class Writer2Priority extends Priority {
    /* The readers from the memory zone */
    private final List<Integer> readers;

    /* The writers in the memory zone */
    private final List<Integer> writers;
    
    /* Writers waiting to enter the memory zone */
    private final List<Integer> waitingWriters;
    
    public Writer2Priority(int zonesCount) {
        this.readers = new ArrayList<>();
        this.writers = new ArrayList<>();
        this.waitingWriters = new ArrayList<>();

        for (int i = 0; i < zonesCount; i++) {
            readers.add(0);
            writers.add(0);
            waitingWriters.add(0);
        }
    }

    @Override
    public void readerLock(int index) {
        synchronized (this) {
            /* Waits if there are writers on wait or active. */
            while (writers.get(index) > 0 || waitingWriters.get(index) > 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            /* Increases the number of readers. */
            readers.set(index, readers.get(index) + 1);
        }
    }

    @Override
    public void readerUnlock(int index) {
        synchronized (this) {
            /* Decreases the number of readers. */
            readers.set(index, readers.get(index) - 1);
            
            /* If there are no more readers, notify the writers. */
            if (readers.get(index) == 0) {
                notifyAll();
            }
        }
    }

    @Override
    public void writerLock(int index) {
        synchronized (this) {
            /* Increases the number of writers on wait. */
            waitingWriters.set(index, waitingWriters.get(index) + 1);

            while (readers.get(index) > 0 || writers.get(index) > 0) {
                /* Waits till the shared resource becomes available */
                try {
                    wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // The shared resource is available, so decreases the waiting writers.
            waitingWriters.set(index, waitingWriters.get(index) - 1);
            
            // Increases the number of writers.
            writers.set(index, writers.get(index) + 1);
        }
    }

    @Override
    public void writerUnlock(int index) {
        synchronized (this) {
            /* Decreases the writers. */
            writers.set(index, writers.get(index) - 1);
            
            /* Notify the readers that the writers are done. */
            notifyAll();
        }
    }
}
