package org.apd.pool;

import java.util.concurrent.BlockingQueue;

import org.apd.executor.StorageTask;
import org.apd.priority.Priority;
import org.apd.storage.EntryResult;
import org.apd.storage.SharedDatabase;

/**
 * Operation type. (read/write)
 */
public class Operation implements Runnable {
    private final StorageTask task;
    private final Priority priority;
    private final SharedDatabase sharedDatabase;
    private final BlockingQueue<EntryResult> resultQueue;
    
    public Operation(StorageTask task, Priority priority,
                     SharedDatabase sharedDatabase,
                     BlockingQueue<EntryResult> resultQueue) {
        this.task = task;
        this.priority = priority;
        this.sharedDatabase = sharedDatabase;
        this.resultQueue = resultQueue;
    }

    public void run() { 
        /* Depending on the priority and task type,
        puts in a queue the result of the operation */
        try {
            if (task.isWrite()) {
                priority.beforeWrite(task.index());
                resultQueue.put(sharedDatabase.addData(task.index(), task.data()));
                priority.afterWrite(task.index());
            
            } else {
                priority.beforeRead(task.index());
                resultQueue.put(sharedDatabase.getData(task.index()));
                priority.afterRead(task.index());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
