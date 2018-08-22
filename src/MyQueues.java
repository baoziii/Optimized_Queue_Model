package OptimizedQueueModel;

import java.util.ArrayList;
import java.util.Collections;

import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;
import sim.field.network.*;

public class MyQueues extends SimState {

    // data is based on http://www.doc88.com/p-9902624959376.html

    //1: Time(mins)
    int time = 480;
    //2: The number of patient with appointment
    int number_a = 75;
    //3: On Time Performance of patient with appointment (input 1.00 as 100%)
    double otp_a = 0.8;
    //4: The number of total patient
    int number_t = 480;
    //5: Arrival speed of patient (per min)
    double lambda = 1.5;
    //6: The number of doctors
    int number_d = 5;
    //7: Visiting speed of doctor
    double theta = 5;

    double[][] resultArr1;
    double[][] resultArr2;
    double[][] resultArr3;
    double[][] resultArr4;

    QueueType1 queueType1;
    QueueType2 queueType2;
    QueueType3 queueType3;
    QueueType4 queueType4;

    // Parameters for Queue Type 2
    int xValueForQueueType2 = 2;
    int yValueForQueueType2 = 1;

    // Parameters for Queue Type 4
    int xValueForQueueType4 = 2;    // punishment: insert into queue after x patients
    int yValueForQueueType4 = 5;    // punishment: insert into queue after y% of total patients

    // Make sure all patients served
    int finishedDoctor = 0;

    // Process lock for doctor
    public synchronized void resetResultArr1(int key1, int key2, double value) {
        this.resultArr1[key1][key2] = value;
    }

    public synchronized void resetResultArr2(int key1, int key2, double value) {
        this.resultArr2[key1][key2] = value;
    }

    public synchronized void resetResultArr3(int key1, int key2, double value) {
        this.resultArr3[key1][key2] = value;
    }

    public synchronized void resetResultArr4(int key1, int key2, double value) {
        this.resultArr4[key1][key2] = value;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getNumber_a() {
        return number_a;
    }

    public void setNumber_a(int number_a) {
        this.number_a = number_a;
    }

    public double getOtp_a() {
        return otp_a;
    }

    public void setOtp_a(double otp_a) {
        this.otp_a = otp_a;
    }

    public int getNumber_t() {
        return number_t;
    }

    public void setNumber_t(int number_t) {
        this.number_t = number_t;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public int getNumber_d() {
        return number_d;
    }

    public void setNumber_d(int number_d) {
        this.number_d = number_d;
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }


    public int getxValueForQueueType2() {
        return xValueForQueueType2;
    }

    public void setxValueForQueueType2(int xValueForQueueType2) {
        this.xValueForQueueType2 = xValueForQueueType2;
    }

    public int getyValueForQueueType2() {
        return yValueForQueueType2;
    }

    public void setyValueForQueueType2(int yValueForQueueType2) {
        this.yValueForQueueType2 = yValueForQueueType2;
    }

    public int getxValueForQueueType4() {
        return xValueForQueueType4;
    }

    public void setxValueForQueueType4(int xValueForQueueType4) {
        this.xValueForQueueType4 = xValueForQueueType4;
    }

    public int getyValueForQueueType4() {
        return yValueForQueueType4;
    }

    public void setyValueForQueueType4(int yValueForQueueType4) {
        this.yValueForQueueType4 = yValueForQueueType4;
    }


    public MyQueues(long seed) {
        super(seed);
    }

    public void start() {
        //init
        finishedDoctor = 0;
        super.start();

        // init queue
        this.queueType1 = new QueueType1();
        this.queueType2 = new QueueType2(xValueForQueueType2, yValueForQueueType2);
        this.queueType3 = new QueueType3();
        this.queueType4 = new QueueType4(xValueForQueueType4, yValueForQueueType4, number_d);

        this.resultArr1 = new double[number_t][];
        this.resultArr2 = new double[number_t][];
        this.resultArr3 = new double[number_t][];
        this.resultArr4 = new double[number_t][];

        // Generate doctors
        for (int i = 0; i < number_d; i++) {
            Doctor doctor = new Doctor();
            // add to schedule
            schedule.scheduleOnce(doctor);
        }
        // Generate patient's arrival-time list
        double[] data = {time, number_a, otp_a, number_t, lambda, number_d, theta};
        double[][] inputArriveList = null;
        try {
            inputArriveList = generate_queue(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < number_t; i++) {
            resultArr1[i] = inputArriveList[i].clone();
            resultArr2[i] = inputArriveList[i].clone();
            resultArr3[i] = inputArriveList[i].clone();
            resultArr4[i] = inputArriveList[i].clone();
        }


        // update served-at time to PatientArriveList
        PatientArriveList patientArriveList = new PatientArriveList(inputArriveList);
        schedule.scheduleOnce(patientArriveList);
    }

    public synchronized void afterSim() {
        finishedDoctor++;
        if (finishedDoctor == number_d) {
            double total_time_normal_1 = 0.0;
            double total_time_normal_2 = 0.0;
            double total_time_normal_3 = 0.0;
            double total_time_normal_4 = 0.0;
            double total_time_appointment_1 = 0.0;
            double total_time_appointment_2 = 0.0;
            double total_time_appointment_3 = 0.0;
            double total_time_appointment_4 = 0.0;

            double total_time_normal_terminated_1 = 0.0;
            double total_time_normal_terminated_2 = 0.0;
            double total_time_normal_terminated_3 = 0.0;
            double total_time_normal_terminated_4 = 0.0;
            double total_time_appointment_terminated_1 = 0.0;
            double total_time_appointment_terminated_2 = 0.0;
            double total_time_appointment_terminated_3 = 0.0;
            double total_time_appointment_terminated_4 = 0.0;

            for (int i = 0; i < number_t; i++) {
                if (resultArr1[i][2] - resultArr1[i][1] < 0) {
                    resultArr1[i][2] = resultArr1[i][1];
                }

                if (resultArr2[i][2] - resultArr2[i][1] < 0) {
                    resultArr2[i][2] = resultArr2[i][1];
                }

                if (resultArr3[i][2] - resultArr3[i][1] < 0) {
                    resultArr3[i][2] = resultArr3[i][1];
                }

                if (resultArr4[i][2] - resultArr4[i][1] < 0) {
                    resultArr4[i][2] = resultArr4[i][1];
                }
            }

            // print lists
			System.out.printf("\nQueue1 Info: \n%-10s%-15s%-15s%-15s%-15s\n", "Index", "Schedule At", "Arrival At", "Served At", "Is Appointment?");
			for(int i = 0; i < resultArr1.length; i++) {
	            System.out.printf("%-10d%-15.2f%-15.2f%-15.2f%-15.0f\n", i + 1, resultArr1[i][0],resultArr1[i][1], resultArr1[i][2], resultArr1[i][3]);
	        }
			
			System.out.printf("\nQueue2 Info: \n%-10s%-15s%-15s%-15s%-15s\n", "Index", "Schedule At", "Arrival At", "Served At", "Is Appointment?");
			for(int i = 0; i < resultArr2.length; i++) {
	            System.out.printf("%-10d%-15.2f%-15.2f%-15.2f%-15.0f\n", i + 1, resultArr2[i][0],resultArr2[i][1], resultArr2[i][2], resultArr2[i][3]);
	        }
			
			System.out.printf("\nQueue3 Info: \n%-10s%-15s%-15s%-15s%-15s\n", "Index", "Schedule At", "Arrival At", "Served At", "Is Appointment?");
			for(int i = 0; i < resultArr3.length; i++) {
	            System.out.printf("%-10d%-15.2f%-15.2f%-15.2f%-15.0f\n", i + 1, resultArr3[i][0],resultArr3[i][1], resultArr3[i][2], resultArr3[i][3]);
	        }
			
			System.out.printf("\nQueue4 Info: \n%-10s%-15s%-15s%-15s%-15s\n", "Index", "Schedule At", "Arrival At", "Served At", "Is Appointment?");
			for(int i = 0; i < resultArr4.length; i++) {
	            System.out.printf("%-10d%-15.2f%-15.2f%-15.2f%-15.0f\n", i + 1, resultArr4[i][0],resultArr4[i][1], resultArr4[i][2], resultArr4[i][3]);
	        }

            // statistics part
	        double finished_time_1 = 0.0;
            double finished_time_2 = 0.0;
            double finished_time_3 = 0.0;
            double finished_time_4 = 0.0;
            int number_a_terminated_1 = 0;
            int number_a_terminated_2 = 0;
            int number_a_terminated_3 = 0;
            int number_a_terminated_4 = 0;

            int number_g_terminated_1 = 0;
            int number_g_terminated_2 = 0;
            int number_g_terminated_3 = 0;
            int number_g_terminated_4 = 0;

            for (int i = 0; i < number_t; i++) {
                if (resultArr1[i][3] == 1) {
                    total_time_appointment_1 += resultArr1[i][2] - resultArr1[i][1];
                    if (resultArr1[i][2] < time){
                        total_time_appointment_terminated_1 += resultArr1[i][2] - resultArr1[i][1];
                        number_a_terminated_1++;
                    } else {
                        total_time_appointment_terminated_1 += time - resultArr1[i][1];
                    }
                } else {
                    total_time_normal_1 += resultArr1[i][2] - resultArr1[i][1];
                    if (resultArr1[i][2] < time){
                        total_time_normal_terminated_1 += resultArr1[i][2] - resultArr1[i][1];
                        number_g_terminated_1++;
                    } else {
                        total_time_normal_terminated_1 += time - resultArr1[i][1];

                    }
                }

                if (resultArr2[i][3] == 1) {
                    total_time_appointment_2 += resultArr2[i][2] - resultArr2[i][1];
                    if (resultArr2[i][2] < time){
                        total_time_appointment_terminated_2 += resultArr2[i][2] - resultArr2[i][1];
                        number_a_terminated_2++;
                    } else {
                        total_time_appointment_terminated_2 += time - resultArr2[i][1];
                    }
                } else {
                    total_time_normal_2 += resultArr2[i][2] - resultArr2[i][1];
                    if (resultArr2[i][2] < time){
                        total_time_normal_terminated_2 += resultArr2[i][2] - resultArr2[i][1];
                        number_g_terminated_2++;
                    } else {
                        total_time_normal_terminated_2 += time - resultArr2[i][1];
                    }
                }

                if (resultArr3[i][3] == 1) {
                    total_time_appointment_3 += resultArr3[i][2] - resultArr3[i][1];
                    if (resultArr3[i][2] < time){
                        total_time_appointment_terminated_3 += resultArr3[i][2] - resultArr3[i][1];
                        number_a_terminated_3++;
                    } else {
                        total_time_appointment_terminated_3 += time - resultArr3[i][1];
                    }
                } else {
                    total_time_normal_3 += resultArr3[i][2] - resultArr3[i][1];
                    if (resultArr3[i][2] < time){
                        total_time_normal_terminated_3 += resultArr3[i][2] - resultArr3[i][1];
                        number_g_terminated_3++;
                    } else {
                        total_time_normal_terminated_3 += time - resultArr3[i][1];
                    }
                }

                if (resultArr4[i][3] == 1) {
                    total_time_appointment_4 += resultArr4[i][2] - resultArr4[i][1];
                    if (resultArr4[i][2] < time){
                        total_time_appointment_terminated_4 += resultArr4[i][2] - resultArr4[i][1];
                        number_a_terminated_4++;
                    } else {
                        total_time_appointment_terminated_4 += time - resultArr4[i][1];
                    }
                } else {
                    total_time_normal_4 += resultArr4[i][2] - resultArr4[i][1];
                    if (resultArr4[i][2] < time){
                        total_time_normal_terminated_4 += resultArr4[i][2] - resultArr4[i][1];
                        number_g_terminated_4++;
                    } else {
                        total_time_normal_terminated_4 += time - resultArr4[i][1];
                    }
                }

                finished_time_1 = Math.max(finished_time_1, resultArr1[i][2]);
                finished_time_2 = Math.max(finished_time_2, resultArr2[i][2]);
                finished_time_3 = Math.max(finished_time_3, resultArr3[i][2]);
                finished_time_4 = Math.max(finished_time_4, resultArr4[i][2]);
            }

            System.out.printf("\nResult: \n%-20s%-25s%-25s%-25s%-25s%-25s%-25s\n", "Queue Type", "Ave Appointment Delay", "Ave Appointment Length", "Ave General Delay", "Ave General Length", "Ave Total Delay", "Ave Total Length");
            System.out.printf("%-20d%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f\n", 1, total_time_appointment_1 / number_a, total_time_appointment_1 / finished_time_1, total_time_normal_1 / (number_t - number_a), total_time_normal_1 / finished_time_1, (total_time_appointment_1 + total_time_normal_1) / number_t, (total_time_appointment_1 + total_time_normal_1) / finished_time_1);
            System.out.printf("%-20d%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f\n", 2, total_time_appointment_2 / number_a, total_time_appointment_2 / finished_time_2, total_time_normal_2 / (number_t - number_a), total_time_normal_2 / finished_time_2, (total_time_appointment_2 + total_time_normal_2) / number_t, (total_time_appointment_2 + total_time_normal_2) / finished_time_2);
            System.out.printf("%-20d%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f\n", 3, total_time_appointment_3 / number_a, total_time_appointment_3 / finished_time_3, total_time_normal_3 / (number_t - number_a), total_time_normal_3 / finished_time_3, (total_time_appointment_3 + total_time_normal_3) / number_t, (total_time_appointment_3 + total_time_normal_3) / finished_time_3);
            System.out.printf("%-20d%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f\n", 4, total_time_appointment_4 / number_a, total_time_appointment_4 / finished_time_4, total_time_normal_4 / (number_t - number_a), total_time_normal_4 / finished_time_4, (total_time_appointment_4 + total_time_normal_4) / number_t, (total_time_appointment_4 + total_time_normal_4) / finished_time_4);
            System.out.printf("The result in %d mins: \n", time);
            System.out.printf("%-20d%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f\n", 1, total_time_appointment_terminated_1 / number_a, total_time_appointment_terminated_1 / time, total_time_normal_terminated_1 / (number_t - number_a), total_time_normal_terminated_1 / time, (total_time_appointment_terminated_1 + total_time_normal_terminated_1) / number_t, (total_time_appointment_terminated_1 + total_time_normal_terminated_1) / time);
            System.out.printf("%-20d%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f\n", 2, total_time_appointment_terminated_2 / number_a, total_time_appointment_terminated_2 / time, total_time_normal_terminated_2 / (number_t - number_a), total_time_normal_terminated_2 / time, (total_time_appointment_terminated_2 + total_time_normal_terminated_2) / number_t, (total_time_appointment_terminated_2 + total_time_normal_terminated_2) / time);
            System.out.printf("%-20d%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f\n", 3, total_time_appointment_terminated_3 / number_a, total_time_appointment_terminated_3 / time, total_time_normal_terminated_3 / (number_t - number_a), total_time_normal_terminated_3 / time, (total_time_appointment_terminated_3 + total_time_normal_terminated_3) / number_t, (total_time_appointment_terminated_3 + total_time_normal_terminated_3) / time);
            System.out.printf("%-20d%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f\n", 4, total_time_appointment_terminated_4 / number_a, total_time_appointment_terminated_4 / time, total_time_normal_terminated_4 / (number_t - number_a), total_time_normal_terminated_4 / time, (total_time_appointment_terminated_4 + total_time_normal_terminated_4) / number_t, (total_time_appointment_terminated_4 + total_time_normal_terminated_4) / time);
            System.out.printf("\nThe number of patient rest: \n%-15s%-15s%-15s%-15s\n", "Queue Type", "Appointment", "General", "Total");
            System.out.printf("%-15d%-15d%-15d%-15d\n", 1, number_a - number_a_terminated_1, number_t - number_a - number_g_terminated_1, number_t - (number_a_terminated_1 + number_g_terminated_1));
            System.out.printf("%-15d%-15d%-15d%-15d\n", 2, number_a - number_a_terminated_2, number_t - number_a - number_g_terminated_2, number_t - (number_a_terminated_2 + number_g_terminated_2));
            System.out.printf("%-15d%-15d%-15d%-15d\n", 3, number_a - number_a_terminated_3, number_t - number_a - number_g_terminated_3, number_t - (number_a_terminated_3 + number_g_terminated_3));
            System.out.printf("%-15d%-15d%-15d%-15d\n", 4, number_a - number_a_terminated_4, number_t - number_a - number_g_terminated_4, number_t - (number_a_terminated_4 + number_g_terminated_4));
            /*System.out.printf("\nMax Length of Queue: \nType1  Type2  Type3  Type 4\n%-7d%-7d%-7d%-7d\n", queueType1.maxSize(), queueType2.maxSize(), queueType3.maxSize(), queueType4.maxSize());
            System.out.printf("\nEnd Time of Queue: \nType1  Type2  Type3  Type 4\n%-7.2f%-7.2f%-7.2f%-7.2f\n", finished_time_1, finished_time_2, finished_time_3, finished_time_4);*/
        }
    }

    public double[][] generate_queue(double[] data) throws Exception {
        int time = (int) data[0];
        int number_a = (int) data[1];
        double otp_a = data[2];
        int number_t = (int) data[3];
        double lambda = data[4];
        int number_d = (int) data[5];
        int theta = (int) data[6];
        ArrayList in_line_time = new ArrayList();
        ArrayList actual_time = new ArrayList();
        double[][] line;
        line = new double[number_t][4];

        int number_general = Math.min((int) (time * lambda), number_t - number_a);
        for (int i = 0; i < number_general; i++) {
            in_line_time.add(((int) (Math.random() * time * 100) / 100.0));
        }
        for (int i = 0; i < number_a; i++) {
            double actual = (i * (time * 1.0 / number_a * 100) / 100.0);
            if (Math.random() > otp_a) {
                actual += Math.random() * (time - actual) * 100.0 / 100.0;
            } else {
                double tmp = 0.0;
                do {
                    if (actual > 0) {
                        tmp = Math.random() * time * 0.2 * 100.0 / 100.0;
                    } else {
                        tmp = 0.0;
                    }
                } while (tmp > actual);
                actual -= tmp;
            }
            in_line_time.add(actual);
            actual_time.add(actual);
        }

        Collections.sort(in_line_time);

        //System.out.printf("\nQueue Info: \n%-10s%-15s%-15s%-15s%-15s\n", "Index", "Schedule At", "Arrival At", "Served At", "Is Appointment?");

        for (int i = 0; i < in_line_time.size(); i++) {
            line[i][0] = 0.0;
            line[i][1] = (Double) in_line_time.get(i);
            line[i][2] = 0.0;
            line[i][3] = 0.0;
        }

        for (int i = 0; i < number_a; i++) {
            int tmp = in_line_time.indexOf(actual_time.get(i));
            line[tmp][0] = (i * (time * 1.0 / number_a * 100) / 100.0);
            line[tmp][1] = (Double) actual_time.get(i);
            line[tmp][3] = 1.0;
        }

        /*for(int i = 0; i < in_line_time.size(); i++) {
            System.out.printf("%-10d%-15.2f%-15.2f%-15.2f%-15.0f\n", i + 1, line[i][0], line[i][1], line[i][2], line[i][3]);
        }*/
        /*System.out.print("Init Queue generated!");*/

        return line;
    }

    public static void main(String[] args) {
        doLoop(MyQueues.class, args);
        System.exit(0);
    }
}


