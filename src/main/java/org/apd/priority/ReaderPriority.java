package org.apd.priority;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ReaderPriority extends Priority{
    private final List<Integer> readers;
    private final List<Semaphore> mutexReadersCount;
    private final List<Semaphore> sharedVars;

    public ReaderPriority(int zonesCount) {
        this.readers = new ArrayList<>(zonesCount);
        this.mutexReadersCount = new ArrayList<>(zonesCount);
        this.sharedVars = new ArrayList<>(zonesCount);

        for (int i = 0; i < zonesCount; i++) {
            readers.add(0);
            mutexReadersCount.add(new Semaphore(1));
            sharedVars.add(new Semaphore(1));
        }
    }

    @Override
    public void startRead(int index) {
        try {
            mutexReadersCount.get(index).acquire();
            
            readers.set(index, readers.get(index) + 1);
            if (readers.get(index) == 1) {
                sharedVars.get(index).acquire();
            }

            mutexReadersCount.get(index).release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endRead(int index) {
        try {
            mutexReadersCount.get(index).acquire();

            readers.set(index, readers.get(index) - 1);
            if (readers.get(index) == 0) {
                sharedVars.get(index).release();
            }
    
            mutexReadersCount.get(index).release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startWrite(int index) {
        try {
            sharedVars.get(index).acquire();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endWrite(int index) {
        sharedVars.get(index).release();
    }
}
