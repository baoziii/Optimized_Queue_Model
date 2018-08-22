package OptimizedQueueModel;

import java.util.ArrayList;

public class QueueType2 implements MyQueueType {
    /*Appointment patient queue and general patient queue
    Y appointment patient(s) insert into the head of total queue after X general patients served (X>=2, X>=Y>=1)
    Appointment patient can come at anytime
    */

    // Actual Queue
    ArrayList<Patient> currentQueue;

    int xValue = 0;
    int yValue = 0;

    int appQueueCount = -1;
    int comQueueCount = -1;
    int currentQueueCount = -1;

    int maxCount = 0;

    QueueType2(int xValue, int yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
        currentQueue = new ArrayList<Patient>();
    }

    @Override
    // add to general queue
    public synchronized void addComReg(Patient patient) {
        currentQueue.add(patient);
        currentQueueCount++;
        comQueueCount++;
    }

    // add to appointment queue
    @Override
    public synchronized void addAppReg(Patient patient) {
        int queueNum = 0;
        if (comQueueCount == -1)// if general patient queue is empty
        {
            currentQueue.add(patient);
            queueNum = currentQueueCount + 1;
        } else// if general patient queue is not empty
        {
            //seats=comQueueCount/xValue
            int avaHole = (comQueueCount + 1) / xValue;
            //occupied seats=appQueueCount/yValue
            int occHole = (appQueueCount + 1) / yValue;

            if (avaHole <= occHole)// seats is not enough
            {
                currentQueue.add(patient);
                queueNum = currentQueueCount + 1;
            } else// seats is enough
            {
                queueNum = (int) ((appQueueCount + 1) / yValue + 1) * xValue;
                currentQueue.add(queueNum, patient);
            }
        }
        currentQueueCount++;
        appQueueCount++;
    }

    // the first patient leave the queue
    @Override
    public synchronized Patient leaveQueue() {

        // update the max queue length
        if (maxCount < this.size()) {
            maxCount = this.size();
        }

        Patient tmp = null;// If the queue is empty, return null
        if (currentQueueCount >= 0)//If the Appointment queue is empty, genral patient can be served
        {
            tmp = currentQueue.get(0);
            currentQueue.remove(0);
            currentQueueCount--;
            if (tmp.isAppointment)// check the type of patient
            {
                appQueueCount--;
            } else {
                comQueueCount--;
            }
        }
        return tmp;
    }

    //get the length of queue
    @Override
    public int size() {
        return currentQueueCount + 1;
    }

    @Override
    public int maxSize() {
        return maxCount;
    }
}
