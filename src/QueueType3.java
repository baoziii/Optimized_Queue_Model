package OptimizedQueueModel;

import java.util.ArrayList;

public class QueueType3 implements MyQueueType {
	/* Appointment patient queue and general patient queue
    appointment patient insert into the head of total queue on time, and insert into the bottom of total queue when he/she late
	appointment time distributed evenly(length of appointment = time / total appointment patient number)
	*/

    // Actual Queue
    ArrayList<Patient> currentQueue;

    int currentQueueCount = -1;

    int maxCount = 0;

    QueueType3() {
        currentQueue = new ArrayList<Patient>();
    }

    @Override
    // add to general queue
    public synchronized void addComReg(Patient patient) {
        currentQueue.add(patient);
        currentQueueCount++;
    }

    // add to appointment queue
    @Override
    public synchronized void addAppReg(Patient patient) {
        // is late?
        if (patient.scheduleAt >= patient.arrivalAt) // on time
        {
            currentQueue.add(0, patient); // insert to the header
            currentQueueCount++;
        } else // late
        {
            currentQueue.add(patient);// insert to the bottom
            currentQueueCount++;
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
        if (currentQueueCount >= 0)// If the Appointment queue is empty, genral patient can be served
        {
            tmp = currentQueue.get(0);
            currentQueue.remove(0);
            currentQueueCount--;
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
