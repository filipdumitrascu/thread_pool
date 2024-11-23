package org.apd.priority;

public abstract class Priority {
    public abstract void startRead(int index);
    public abstract void endRead(int index);
    public abstract void startWrite(int index);
    public abstract void endWrite(int index);
}
