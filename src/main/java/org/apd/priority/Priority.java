package org.apd.priority;

public abstract class Priority {
    /* the operations done before reading to ensure the priority */
    public abstract void beforeRead(int index);
    
    /* the operations done after reading to ensure the priority */
    public abstract void afterRead(int index);
    
    /* the operations done before writing to ensure the priority */
    public abstract void beforeWrite(int index);
    
    /* the operations done before writing to ensure the priority */
    public abstract void afterWrite(int index);
}
