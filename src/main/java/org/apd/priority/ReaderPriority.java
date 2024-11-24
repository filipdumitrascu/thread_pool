package org.apd.priority;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ReaderPriority extends Priority{
    /* Number of readers reading simultaneously from the shared resource */
    private final List<Integer> readers;
    
    /* Semaphore used to change the number of readers */
    private final List<Semaphore> mutexReadersCount;

    /* Semaphore used to protect the common resource */
    private final List<Semaphore> sharedRes;

    public ReaderPriority(int zonesCount) {
        this.readers = new ArrayList<>(zonesCount);
        this.mutexReadersCount = new ArrayList<>(zonesCount);
        this.sharedRes = new ArrayList<>(zonesCount);

        for (int i = 0; i < zonesCount; i++) {
            readers.add(0);
            mutexReadersCount.add(new Semaphore(1));
            sharedRes.add(new Semaphore(1));
        }
    }

    @Override
    public void beforeRead(int index) {
        try {
            mutexReadersCount.get(index).acquire();
            readers.set(index, readers.get(index) + 1);

            /* If it's the first reader, then reserves
            the memory area so that no writer enters */
            if (readers.get(index) == 1) {
                sharedRes.get(index).acquire();
            }

            mutexReadersCount.get(index).release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterRead(int index) {
        try {
            mutexReadersCount.get(index).acquire();
            readers.set(index, readers.get(index) - 1);

            /* If it is the last reader, clears the
            memory area from which it was read */
            if (readers.get(index) == 0) {
                sharedRes.get(index).release();
            }
    
            mutexReadersCount.get(index).release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeWrite(int index) {
        try {
            /* The writer enters the common resource */
            sharedRes.get(index).acquire();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterWrite(int index) {
        /* The writer releases the resource */
        sharedRes.get(index).release();
    }
}
