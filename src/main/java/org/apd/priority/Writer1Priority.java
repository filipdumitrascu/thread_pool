package org.apd.priority;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Writer1Priority extends Priority {
    /* Number of readers reading simultaneously from the shared resource */
    private final List<Integer> readers;

    /* Writers writing in the memory zone (there will be only one,
    there cannot be more than one writer writing at the same time) */
    private final List<Integer> writers;

    /* Readers waiting to enter the memory zone */
    private final List<Integer> waitingReaders;

    /* Writers waiting to enter the memory zone */
    private final List<Integer> waitingWriters;

    /* Semaphore used to put writers on hold if there is a writer
    or more readers in the memory zone (critical zone) */
    private final List<Semaphore> mutexReadersCount;

    /* Semaphore used to put readers on hold if there is a writer
    writing in the memory zone or if there is writers on hold
    (because they have priority over readers) */
    private final List<Semaphore> mutexWritersCount;

    /* Semaphore used to protect the common resource */
    private final List<Semaphore> sharedVars;

    public Writer1Priority(int zonesCount) {
        this.readers = new ArrayList<>();
        this.writers = new ArrayList<>();

        this.waitingReaders = new ArrayList<>();
        this.waitingWriters = new ArrayList<>();

        this.mutexReadersCount = new ArrayList<>();
        this.mutexWritersCount = new ArrayList<>();
        this.sharedVars = new ArrayList<>();

        for (int i = 0; i < zonesCount; i++) {
            readers.add(0);
            writers.add(0);
            waitingReaders.add(0);
            waitingWriters.add(0);

            mutexReadersCount.add(new Semaphore(0));
            mutexWritersCount.add(new Semaphore(0));
            sharedVars.add(new Semaphore(1));
        }
    }

    @Override
    public void beforeRead(int index) {
        try {
            sharedVars.get(index).acquire();
            
            /* If there is at least one writer writing in the shared resource
            or if there is a writer in waiting, the reader is waiting */
            if (writers.get(index) > 0 || waitingWriters.get(index) > 0) {
                waitingReaders.set(index, waitingReaders.get(index) + 1);
                sharedVars.get(index).release();
                mutexReadersCount.get(index).acquire();
            }

            readers.set(index, readers.get(index) + 1);

            if (waitingReaders.get(index) > 0) {
                /* Another reader has joined the shared resource,
                coming out of the waiting state */
                waitingReaders.set(index, waitingReaders.get(index) - 1);
                mutexReadersCount.get(index).release();
            
            } else if (waitingReaders.get(index) == 0) {
                sharedVars.get(index).release();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterRead(int index) {
        try {
            sharedVars.get(index).acquire();
            readers.set(index, readers.get(index) - 1);

            if (readers.get(index) == 0 && waitingWriters.get(index) > 0) {
                waitingWriters.set(index, waitingWriters.get(index) - 1);
                mutexWritersCount.get(index).release();
            
            } else if (readers.get(index) > 0 || waitingWriters.get(index) == 0) {
                sharedVars.get(index).release();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeWrite(int index) {
        try {
            sharedVars.get(index).acquire();

            if (readers.get(index) > 0 || writers.get(index) > 0) {
                waitingWriters.set(index, waitingWriters.get(index) + 1);
                sharedVars.get(index).release();
                mutexWritersCount.get(index).acquire();
            }

            writers.set(index, writers.get(index) + 1);
            
            sharedVars.get(index).release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterWrite(int index) {
        try {
            sharedVars.get(index).acquire();

            writers.set(index, writers.get(index) - 1);
            
            if (waitingReaders.get(index) > 0 && waitingWriters.get(index) == 0) {
                waitingReaders.set(index, waitingReaders.get(index) - 1);
                mutexReadersCount.get(index).release();
            
            } else if (waitingWriters.get(index) > 0) {
                waitingWriters.set(index, waitingWriters.get(index) - 1);
                mutexWritersCount.get(index).release();
            
            } else if (waitingReaders.get(index) == 0 && waitingWriters.get(index) == 0) {
                sharedVars.get(index).release();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
