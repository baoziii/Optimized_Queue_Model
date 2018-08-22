package OptimizedQueueModel;

import java.util.ArrayList;

public class QueueType1 implements MyQueueType {
    /* Appointment patient queue and general patient queue
    appointment patient insert into the head of total queue (Total Queue like Appointment Queue | General Patient Queue)
    the number of appointment patient per hour = the number of total appointment patient / time(min) * 60
    */
    // Appointment patient queue
    ArrayList<Patient> appQueue;
    // General patient queue
    ArrayList<Patient> comQueue;

    int appQueueCount = -1;
    int comQueueCount = -1;

    int maxCount = 0;

    QueueType1() {
        appQueue = new ArrayList<Patient>();
        comQueue = new ArrayList<Patient>();
    }

    @Override
    // add to general queue
    public synchronized void addComReg(Patient patient) {
        comQueue.add(patient);
        comQueueCount++;
    }

    // add to appointment queue
    @Override
    public synchronized void addAppReg(Patient patient) {
        appQueue.add(patient);
        appQueueCount++;
    }

    // the first patient leave
    @Override
    public synchronized Patient leaveQueue() {

        // update the max queue length
        if (maxCount < this.size()) {
            maxCount = this.size();
        }

        Patient tmp = null;// If the queue is empty, return null
        if (appQueueCount < 0) // If the Appointment queue is empty, genral patient can be served
        {
            if (comQueueCount >= 0) {
                tmp = comQueue.get(0);
                comQueue.remove(0);
                comQueueCount--;
            }
        } else {
            tmp = appQueue.get(0);
            appQueue.remove(0);
            appQueueCount--;
        }
        return tmp;
    }

    // get the length of queue
    @Override
    public int size() {
        return appQueueCount + comQueueCount + 2;
    }

    @Override
    public int maxSize() {
        return maxCount;
    }
}
