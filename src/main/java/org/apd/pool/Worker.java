package org.apd.pool;

import java.util.concurrent.BlockingQueue;

import org.apd.executor.StorageTask;
import org.apd.priority.Priority;
import org.apd.storage.EntryResult;
import org.apd.storage.SharedDatabase;

public class Worker implements Runnable{
    private final BlockingQueue<StorageTask> taskQueue;
    private final BlockingQueue<EntryResult> resultQueue;
    private final SharedDatabase sharedDatabase;
    private final Priority priority;

    public Worker(BlockingQueue<StorageTask> taskQueue, 
                  BlockingQueue<EntryResult> resultQueue, 
                  SharedDatabase sharedDatabase,
                  Priority priority
    ) {
        this.taskQueue = taskQueue;
        this.resultQueue = resultQueue;
        this.sharedDatabase = sharedDatabase;
        this.priority = priority;
    }
    
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                StorageTask task = taskQueue.take();
                
                if (task.isWrite()) {
                    priority.startWrite(task.index());
                    resultQueue.put(sharedDatabase.addData(task.index(), task.data()));
                    priority.endWrite(task.index());
                
                } else {
                    priority.startRead(task.index());
                    resultQueue.put(sharedDatabase.getData(task.index()));
                    priority.endWrite(task.index());
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
