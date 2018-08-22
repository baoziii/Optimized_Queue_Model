package OptimizedQueueModel;

import sim.engine.SimState;
import sim.engine.Steppable;

public class PatientArriveList implements Steppable {

    double[][] inputList;
    int currentTime = 0;
    int currentPatientCount = 0;
    //QueueType4 queueType4;

    public PatientArriveList(double[][] inputList) {
        this.inputList = inputList;
    }

    @Override
    public void step(SimState state) {
        MyQueues myQueues = (MyQueues) state;
        // add patient into queue when it is patients' arrival time
        if (currentPatientCount < inputList.length) {
            while (currentTime == (int) (inputList[currentPatientCount][1] * 100)) {
                boolean isAppointment = false;
                if (inputList[currentPatientCount][3] == 1) {
                    isAppointment = true;
                }
                Patient patient = new Patient(
                        currentPatientCount,
                        (int) (inputList[currentPatientCount][1] * 100),
                        (int) ((myQueues.random.nextDouble(true, true) * myQueues.theta + 0.5 * myQueues.theta) * 100),
                        isAppointment,
                        (int) (inputList[currentPatientCount][0] * 100));

                // add patients to specific queue
                if (patient.isAppointment) {
                    myQueues.queueType1.addAppReg(patient);
                    myQueues.queueType2.addAppReg(patient);
                    myQueues.queueType3.addAppReg(patient);
                    myQueues.queueType4.addAppReg(patient);
                } else {
                    myQueues.queueType1.addComReg(patient);
                    myQueues.queueType2.addComReg(patient);
                    myQueues.queueType3.addComReg(patient);
                    myQueues.queueType4.addComReg(patient);
                }

                if (currentPatientCount < inputList.length - 1) {
                    currentPatientCount++;
                } else {
                    break;
                }
            }
        }

        currentTime++;

        if (currentTime < (myQueues.time * 100)) {
            myQueues.schedule.scheduleOnce(this);
        }
    }
}
