package OptimizedQueueModel;

public class Patient {

    // patient id
    int pid = 0;
    // arrival at
    int arrivalAt = 0;
    // served at
    int takeTime = 2;

    // is appointment patient
    boolean isAppointment = false;

    // schedule arrived at
    int scheduleAt = 0;

    // is late?
    boolean isLate = false;

    public Patient(int pid, int arrivalAt, int takeTime, boolean isAppointment, int scheduleAt) {
        this.pid = pid;
        this.arrivalAt = arrivalAt;
        this.takeTime = takeTime;
        this.isAppointment = isAppointment;
        this.scheduleAt = scheduleAt;

    }

    public String toString() {
        return "[" + System.identityHashCode(this) + "] pid: " + pid + " arrivalAt: " + arrivalAt + " takeTime: " + takeTime + " scheduleAt: " + scheduleAt;
    }

}
