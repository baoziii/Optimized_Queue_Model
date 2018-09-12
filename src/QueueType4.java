package OptimizedQueueModel;

import java.util.ArrayList;

public class QueueType4 implements MyQueueType {
    /* Appointment patient queue and general patient queue
    appointment patient insert into the head of total queue (Total Queue like Appointment Queue | General Patient Queue)
    the number of appointment patient per hour = the number of total appointment patient / time(min) * 60
    length of second level queue = the number of doctors * 2
    Model 4 similar to Model 1, but with second level queue
    */
    // Appointment patient queue
    ArrayList<Patient> appQueue;
    // General patient queue
    ArrayList<Patient> comQueue;
    // Second level queue
    ArrayList<Patient> secondLevelQueue;

    int appQueueCount = -1;
    int comQueueCount = -1;
    int secondLevelQueueCount = -1;

    int number_d = 0;

    int maxCount = 0;

    public QueueType4(int number_d) {
        this.number_d = number_d;
        appQueue = new ArrayList<Patient>();
        comQueue = new ArrayList<Patient>();
        secondLevelQueue = new ArrayList<Patient>();
    }

    private void checkAndPutPatientInSecondLevel() {
        if (secondLevelQueue.size() < number_d * 2)// if second level queue is not full load
        {
            Patient tmp = null;//if no patient in the queue，return null
            if (appQueueCount < 0)//catch patient from regular patient queue when there are no appointment patient
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
            if(tmp!=null)
            {
                secondLevelQueue.add(tmp);
                secondLevelQueueCount++;
            }
        }
    }

    @Override
    // add to general queue
    public synchronized void addComReg(Patient patient) {
        comQueue.add(patient);
        comQueueCount++;
        checkAndPutPatientInSecondLevel();
    }

    // add to appointment queue
    @Override
    public synchronized void addAppReg(Patient patient) {
        appQueue.add(patient);
        appQueueCount++;
        checkAndPutPatientInSecondLevel();
    }

    // the first patient leave
    @Override
    public synchronized Patient leaveQueue() {

        // update the max queue length
        if (maxCount < this.size()) {
            maxCount = this.size();
        }

        Patient tmp = null;// If the queue is empty, return null
        if (secondLevelQueueCount >= 0)// If the Appointment queue is empty, genral patient can be served
        {
            tmp = secondLevelQueue.get(0);
            secondLevelQueue.remove(0);
            secondLevelQueueCount--;
        }
        checkAndPutPatientInSecondLevel();
        return tmp;
    }

    // get the length of queue
    @Override
    public int size() {
        return appQueueCount + comQueueCount + secondLevelQueueCount + 3;
    }

    @Override
    public int maxSize() {
        return maxCount;
    }
}
