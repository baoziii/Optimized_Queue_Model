package OptimizedQueueModel;

import java.util.ArrayList;

public class QueueType3 implements MyQueueType {
	/* Appointment patient queue and general patient queue
    appointment patient insert into the head of total queue on time, and insert into the bottom of total queue when he/she late
	appointment time distributed evenly(length of appointment = time / total appointment patient number)
	*/

    // Appointment patient queue
    ArrayList<Patient> appQueue;
    // General patient queue
    ArrayList<Patient> comQueue;

    int appQueueCount=-1;
    int comQueueCount=-1;

    int currentQueueCount=-1;

    int maxCount = 0;

    QueueType3()
    {
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
        // is late?
        if (patient.scheduleAt >= patient.arrivalAt) // on time
        {
            appQueue.add(patient); // insert to the bottom of appointment queue
            appQueueCount++;
        } else // late
        {
            comQueue.add(patient);// insert to the bottom
            comQueueCount++;
        }
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
        return currentQueueCount + 1;
    }

    @Override
    public int maxSize() {
        return maxCount;
    }
}
