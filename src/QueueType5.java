package OptimizedQueueModel;

import java.util.ArrayList;

public class QueueType5 implements MyQueueType {
	/* Appointment patient queue and general patient queue
    appointment patient insert after the second level queue and the header of rest of total queue on time, and insert into the total queue after punishment when he/she late
	appointment time distributed evenly(length of appointment = time / total appointment patient number)
	length of second level queue = the number of doctors * 2
	punishment:(X>= 2, Y>= 2)
	1. insert after the second level queue with X patients delay
	2. insert after the second level queue with Y% of total patients delay
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

    int xValue = 0;
    double yValue = 0.0;
    int number_d = 0;
    int maxCount = 0;

    public QueueType5(int xValue, int yValue, int number_d) {
        this.xValue = xValue;
        this.yValue = yValue / 100.0;
        this.number_d = number_d;
        appQueue = new ArrayList<Patient>();
        comQueue = new ArrayList<Patient>();
        secondLevelQueue = new ArrayList<Patient>();
    }

    @Override
    // add to general queue
    public synchronized void addComReg(Patient patient) {
        comQueue.add(patient);
        comQueueCount++;
        checkAndPutPatientInSecondLevel();
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

    // add to appointment queue
    @Override
    public synchronized void addAppReg(Patient patient) {
        // is late?
        if (patient.scheduleAt >= patient.arrivalAt)// on time
        {
            appQueue.add(patient);
            appQueueCount++;
        } else// late
        {
            int target = (int) (2 * number_d + xValue + yValue * comQueue.size());
            if (target >= comQueue.size()){
                comQueue.add(patient);
            }else {
                comQueue.add(target, patient);
            }
            comQueueCount++;
        }
        checkAndPutPatientInSecondLevel();

        /*if (patient.scheduleAt >= patient.arrivalAt)// on time
        {
            int target = 2 * number_d;
            if (target < currentQueue.size()) {
                currentQueue.add(target, patient);//insert after the second level queue but the header of rest queue when second level is full
            } else {
                currentQueue.add(patient);// insert into the bottom of second level queue when second level is not full
                target = currentQueue.size();
            }
            currentQueueCount++;
        } else// late
        {
            int target = (int) (2 * number_d + xValue + yValue * currentQueue.size());
            if (target >= currentQueue.size()) {
                currentQueue.add(patient);//insert into the bottom of total queue when second level is not full or second level is full with fewer patients
                target = currentQueue.size();
            } else {
                currentQueue.add(target, patient);//insert into the punishment position when second level is full with lots of patients
            }
            currentQueueCount++;
        }*/
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
