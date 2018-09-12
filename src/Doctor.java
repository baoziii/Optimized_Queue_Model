package OptimizedQueueModel;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Doctor implements Steppable {

    int onWorkCount1 = 0;
    int onWorkCount2 = 0;
    int onWorkCount3 = 0;
    int onWorkCount4 = 0;
    int onWorkCount5 = 0;
    int testCount = 0;

    public Doctor() {
        // Constructor
    }

    @Override
    public void step(SimState state) {
        MyQueues myQueues = (MyQueues) state;

        if (onWorkCount1 == 0)// Queue 1
        {
            // get a patient
            Patient tmp1 = myQueues.queueType1.leaveQueue();

            if (tmp1 != null) {
                myQueues.resetResultArr1(tmp1.pid, 2, (myQueues.schedule.getTime() / 100));
                onWorkCount1 = tmp1.takeTime;
            }
        } else {
            // busy
            onWorkCount1--;
        }

        if (onWorkCount2 == 0) {
            Patient tmp2 = myQueues.queueType2.leaveQueue();
            if (tmp2 != null) {
                myQueues.resetResultArr2(tmp2.pid, 2, (myQueues.schedule.getTime() / 100));
                onWorkCount2 = tmp2.takeTime;
            }
        } else {
            onWorkCount2--;
        }

        if (onWorkCount3 == 0) {
            Patient tmp3 = myQueues.queueType3.leaveQueue();

            if (tmp3 != null) {
                myQueues.resetResultArr3(tmp3.pid, 2, (myQueues.schedule.getTime() / 100));
                onWorkCount3 = tmp3.takeTime;
            }
        } else {
            onWorkCount3--;
        }

        if (onWorkCount4 == 0) {
            Patient tmp4 = myQueues.queueType4.leaveQueue();
            if (tmp4 != null) {
                myQueues.resetResultArr4(tmp4.pid, 2, (myQueues.schedule.getTime() / 100));
                onWorkCount4 = tmp4.takeTime;
            }

        } else {
            onWorkCount4--;
        }

        if (onWorkCount5 == 0) {
            Patient tmp5 = myQueues.queueType5.leaveQueue();
            if (tmp5 != null) {
                myQueues.resetResultArr5(tmp5.pid, 2, (myQueues.schedule.getTime() / 100));
                onWorkCount5 = tmp5.takeTime;
            }

        } else {
            onWorkCount5--;
        }

        if (myQueues.schedule.getTime() < (myQueues.time * 100) || myQueues.queueType1.size() != 0 || myQueues.queueType2.size() != 0 || myQueues.queueType3.size() != 0 || myQueues.queueType4.size() != 0 || myQueues.queueType5.size() != 0) {
            myQueues.schedule.scheduleOnce(this);
            testCount++;
        } else {
            myQueues.afterSim();
        }
    }

    public String toString() {
        return "[" + System.identityHashCode(this) + "] onWorkCount1: " + onWorkCount1 + " onWorkCount2: " + onWorkCount2 + " onWorkCount3: " + onWorkCount3 + " onWorkCount4: " + onWorkCount4 + " onWorkCount5: " + onWorkCount5;
    }

}
