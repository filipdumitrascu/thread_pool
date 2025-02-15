package org.apd.priority;

public abstract class Priority {
    /* the operations done before reading to ensure the priority */
    public abstract void readerLock(int index);
    
    /* the operations done after reading to ensure the priority */
    public abstract void readerUnlock(int index);
    
    /* the operations done before writing to ensure the priority */
    public abstract void writerLock(int index);
    
    /* the operations done before writing to ensure the priority */
    public abstract void writerUnlock(int index);
}
