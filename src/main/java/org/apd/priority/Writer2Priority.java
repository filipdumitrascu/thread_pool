package org.apd.priority;

import java.util.ArrayList;
import java.util.List;

public class Writer2Priority extends Priority {
    private final List<Integer> readers;
    private final List<Integer> writers;
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
    public void startRead(int index) {
        synchronized (this) {
            while (writers.get(index) > 0 || waitingWriters.get(index) > 0) {
                // Așteptăm dacă există scriitori activi sau în așteptare
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Re-interrupt thread-ul în caz de eroare
                }
            }
            // Creștem numărul de cititori activi
            readers.set(index, readers.get(index) + 1);
        }
    }

    @Override
    public void endRead(int index) {
        synchronized (this) {
            // Reducem numărul de cititori activi
            readers.set(index, readers.get(index) - 1);
            // Dacă nu mai sunt cititori, notificăm eventualii scriitori în așteptare
            if (readers.get(index) == 0) {
                notifyAll();
            }
        }
    }

    @Override
    public void startWrite(int index) {
        synchronized (this) {
            // Creștem numărul de scriitori în așteptare
            waitingWriters.set(index, waitingWriters.get(index) + 1);

            while (readers.get(index) > 0 || writers.get(index) > 0) {
                // Așteptăm până când zona devine disponibilă
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Re-interrupt thread-ul în caz de eroare
                }
            }

            // Zona este disponibilă, deci scădem numărul de scriitori în așteptare
            waitingWriters.set(index, waitingWriters.get(index) - 1);
            // Creștem numărul de scriitori activi
            writers.set(index, writers.get(index) + 1);
        }
    }

    @Override
    public void endWrite(int index) {
        synchronized (this) {
            // Reducem numărul de scriitori activi
            writers.set(index, writers.get(index) - 1);
            // Notificăm toți, începând cu scriitorii (prin ordinea priorității impusă de while-uri)
            notifyAll();
        }
    }
}
