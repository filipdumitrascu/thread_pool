package org.apd.priority;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Writer1Priority extends Priority {
    private final List<Integer> readers;
    private final List<Integer> writers;

    private final List<Integer> waitingReaders;
    private final List<Integer> waitingWriters;

    private final List<Semaphore> mutexReadersCount;
    private final List<Semaphore> mutexWritersCount;
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
    public void startRead(int index) {
        try {
            sharedVars.get(index).acquire();
            
            if (writers.get(index) > 0 || waitingWriters.get(index) > 0) {
                waitingReaders.set(index, waitingReaders.get(index) + 1);
                sharedVars.get(index).release();
                mutexReadersCount.get(index).acquire();
            }

            readers.set(index, readers.get(index) + 1);

            if (waitingReaders.get(index) > 0) {
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
    public void endRead(int index) {
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
    public void startWrite(int index) {
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
    public void endWrite(int index) {
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
