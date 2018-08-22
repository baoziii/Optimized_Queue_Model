package OptimizedQueueModel;

public interface MyQueueType {
    //add to general patient queue
    public void addComReg(Patient patient);

    //add to appointment patient queue
    public void addAppReg(Patient patient);

    // the patient on the front leave the queue
    public Patient leaveQueue();

    public int size();

    public int maxSize();
}
