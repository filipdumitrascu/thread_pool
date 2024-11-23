package org.apd.pool;

import java.util.concurrent.BlockingQueue;

import org.apd.executor.StorageTask;
import org.apd.priority.Priority;
import org.apd.storage.EntryResult;
import org.apd.storage.SharedDatabase;

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
        try {
            if (task.isWrite()) {
                priority.startWrite(task.index());
                resultQueue.put(sharedDatabase.addData(task.index(), task.data()));
                priority.endWrite(task.index());
            
            } else {
                priority.startRead(task.index());
                resultQueue.put(sharedDatabase.getData(task.index()));
                priority.endRead(task.index());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
