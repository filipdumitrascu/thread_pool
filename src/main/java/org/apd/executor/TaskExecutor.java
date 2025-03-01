package org.apd.executor;

import org.apd.storage.EntryResult;
import org.apd.storage.SharedDatabase;

import org.apd.pool.Operation;
import org.apd.pool.ThreadPool;

import org.apd.priority.Priority;
import org.apd.priority.ReaderPriority;
import org.apd.priority.Writer1Priority;
import org.apd.priority.Writer2Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/* DO NOT MODIFY THE METHODS SIGNATURES */
public class TaskExecutor {
    private final SharedDatabase sharedDatabase;
    private final int storageSize;

    public TaskExecutor(int storageSize, int blockSize, long readDuration, long writeDuration) {
        sharedDatabase = new SharedDatabase(storageSize, blockSize, readDuration, writeDuration);
        this.storageSize = storageSize;
    }

    public List<EntryResult> ExecuteWork(int numberOfThreads, List<StorageTask> tasks, LockType lockType) {
        /* Depending on the lock type, the object takes
        over the read/write priority methods */
        Priority priority = switch (lockType) {
            case ReaderPreferred -> new ReaderPriority(storageSize);
            case WriterPreferred1 -> new Writer1Priority(storageSize);
            case WriterPreferred2 -> new Writer2Priority(storageSize);
        };

        /* Instantiates the thread pool and a synchronized
        queue for storing the changes to the database */
        ThreadPool tpe = new ThreadPool(numberOfThreads);
        BlockingQueue<EntryResult> results = new LinkedBlockingQueue<>(); 

        /* Put the tasks in the thread pool one at a
        time to be processed by the workers */
        for (StorageTask task : tasks) {
            tpe.submit(new Operation(task, priority, sharedDatabase, results));
        }

        tpe.shutdown();

        return new ArrayList<>(results);
    }

    public List<EntryResult> ExecuteWorkSerial(List<StorageTask> tasks) {
        var results = tasks.stream().map(task -> {
            try {
                if (task.isWrite()) {
                    return sharedDatabase.addData(task.index(), task.data());
                } else {
                    return sharedDatabase.getData(task.index());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();

        return results.stream().toList();
    }
}
