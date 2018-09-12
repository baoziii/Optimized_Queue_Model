package OptimizedQueueModel;

import java.util.ArrayList;
import java.util.Collections;
import java.io.*;



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

    // run simulation t time(s)
    int t = 1000;

    double[][] resultArr1;
    double[][] resultArr2;
    double[][] resultArr3;
    double[][] resultArr4;
    double[][] resultArr5;

    QueueType1 queueType1;
    QueueType2 queueType2;
    QueueType3 queueType3;
    QueueType4 queueType4;
    QueueType5 queueType5;

    // Parameters for Queue Type 2
    int xValueForQueueType2 = 2;
    int yValueForQueueType2 = 1;

    // Parameters for Queue Type 5
    int xValueForQueueType5 = 2;    // punishment: insert into queue after x patients
    int yValueForQueueType5 = 5;    // punishment: insert into queue after y% of total patients

/*    int xValueForQueueType4 = 2;    // punishment: insert into queue after x patients
    int yValueForQueueType4 = 5;    // punishment: insert into queue after y% of total patients*/

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

    public synchronized void resetResultArr5(int key1, int key2, double value) {
        this.resultArr5[key1][key2] = value;
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



/*    public int getxValueForQueueType4() {
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
    }*/



    public int getxValueForQueueType5() {
        return xValueForQueueType5;
    }

    public void setxValueForQueueType5(int xValueForQueueType5) {
        this.xValueForQueueType5 = xValueForQueueType5;
    }

    public int getyValueForQueueType5() {
        return yValueForQueueType5;
    }

    public void setyValueForQueueType5(int yValueForQueueType5) {
        this.yValueForQueueType5 = yValueForQueueType5;
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
        this.queueType4 = new QueueType4(number_d);
        //this.queueType4 = new QueueType4(xValueForQueueType4, yValueForQueueType4, number_d);
        this.queueType5 = new QueueType5(xValueForQueueType5, yValueForQueueType5, number_d);

        this.resultArr1 = new double[number_t][];
        this.resultArr2 = new double[number_t][];
        this.resultArr3 = new double[number_t][];
        this.resultArr4 = new double[number_t][];
        this.resultArr5 = new double[number_t][];

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
            resultArr5[i] = inputArriveList[i].clone();
        }


        // update served-at time to PatientArriveList
        PatientArriveList patientArriveList = new PatientArriveList(inputArriveList);
        schedule.scheduleOnce(patientArriveList);
    }

    public synchronized void afterSim() {
        finishedDoctor++;
        BufferedWriter bw11 = null;
        BufferedWriter bw12 = null;
        BufferedWriter bw13 = null;
        BufferedWriter bw14 = null;
        BufferedWriter bw15 = null;
        BufferedWriter bw21 = null;
        BufferedWriter bw22 = null;
        BufferedWriter bw23 = null;
        BufferedWriter bw24 = null;
        BufferedWriter bw25 = null;
        BufferedWriter bw31 = null;
        BufferedWriter bw32 = null;
        BufferedWriter bw33 = null;
        BufferedWriter bw34 = null;
        BufferedWriter bw35 = null;

        if (finishedDoctor == number_d) {
            try {
                bw11 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data/statistics1.txt"), true)));
                bw12 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data/statistics2.txt"), true)));
                bw13 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data/statistics3.txt"), true)));
                bw14 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data/statistics4.txt"), true)));
                bw15 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data/statistics5.txt"), true)));

                bw21 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data/average1.txt"), true)));
                bw22 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data/average2.txt"), true)));
                bw23 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data/average3.txt"), true)));
                bw24 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data/average4.txt"), true)));
                bw25 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data/average5.txt"), true)));

                bw31 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data/remain1.txt"), true)));
                bw32 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data/remain2.txt"), true)));
                bw33 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data/remain3.txt"), true)));
                bw34 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data/remain4.txt"), true)));
                bw35 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data/remain5.txt"), true)));

                if (job() == 0) {
                    bw11.write(String.format("%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s\n", "Queue Type", "0-3", "3-6", "6-9", "9-12", "12-15", "15-18", "18-21", ">21"));
                    bw12.write(String.format("%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s\n", "Queue Type", "0-3", "3-6", "6-9", "9-12", "12-15", "15-18", "18-21", ">21"));
                    bw13.write(String.format("%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s\n", "Queue Type", "0-3", "3-6", "6-9", "9-12", "12-15", "15-18", "18-21", ">21"));
                    bw14.write(String.format("%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s\n", "Queue Type", "0-3", "3-6", "6-9", "9-12", "12-15", "15-18", "18-21", ">21"));
                    bw15.write(String.format("%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s\n", "Queue Type", "0-3", "3-6", "6-9", "9-12", "12-15", "15-18", "18-21", ">21"));
                    bw21.write(String.format("%-20s%-25s%-25s%-25s%-25s%-25s%-25s\n", "Queue Type", "Ave Appointment Delay", "Ave Appointment Length", "Ave General Delay", "Ave General Length", "Ave Total Delay", "Ave Total Length"));
                    bw22.write(String.format("%-20s%-25s%-25s%-25s%-25s%-25s%-25s\n", "Queue Type", "Ave Appointment Delay", "Ave Appointment Length", "Ave General Delay", "Ave General Length", "Ave Total Delay", "Ave Total Length"));
                    bw23.write(String.format("%-20s%-25s%-25s%-25s%-25s%-25s%-25s\n", "Queue Type", "Ave Appointment Delay", "Ave Appointment Length", "Ave General Delay", "Ave General Length", "Ave Total Delay", "Ave Total Length"));
                    bw24.write(String.format("%-20s%-25s%-25s%-25s%-25s%-25s%-25s\n", "Queue Type", "Ave Appointment Delay", "Ave Appointment Length", "Ave General Delay", "Ave General Length", "Ave Total Delay", "Ave Total Length"));
                    bw25.write(String.format("%-20s%-25s%-25s%-25s%-25s%-25s%-25s\n", "Queue Type", "Ave Appointment Delay", "Ave Appointment Length", "Ave General Delay", "Ave General Length", "Ave Total Delay", "Ave Total Length"));
                    bw31.write(String.format("%-15s%-15s%-15s%-15s\n", "Queue Type", "Appointment", "General", "Total"));
                    bw32.write(String.format("%-15s%-15s%-15s%-15s\n", "Queue Type", "Appointment", "General", "Total"));
                    bw33.write(String.format("%-15s%-15s%-15s%-15s\n", "Queue Type", "Appointment", "General", "Total"));
                    bw34.write(String.format("%-15s%-15s%-15s%-15s\n", "Queue Type", "Appointment", "General", "Total"));
                    bw35.write(String.format("%-15s%-15s%-15s%-15s\n", "Queue Type", "Appointment", "General", "Total"));
                }
            } catch (Exception e){
                System.err.print(e);
            }

            int[][] result = new int[5][8];

            double total_time_normal_1 = 0.0;
            double total_time_normal_2 = 0.0;
            double total_time_normal_3 = 0.0;
            double total_time_normal_4 = 0.0;
            double total_time_normal_5 = 0.0;
            double total_time_appointment_1 = 0.0;
            double total_time_appointment_2 = 0.0;
            double total_time_appointment_3 = 0.0;
            double total_time_appointment_4 = 0.0;
            double total_time_appointment_5 = 0.0;

            double total_time_normal_terminated_1 = 0.0;
            double total_time_normal_terminated_2 = 0.0;
            double total_time_normal_terminated_3 = 0.0;
            double total_time_normal_terminated_4 = 0.0;
            double total_time_normal_terminated_5 = 0.0;
            double total_time_appointment_terminated_1 = 0.0;
            double total_time_appointment_terminated_2 = 0.0;
            double total_time_appointment_terminated_3 = 0.0;
            double total_time_appointment_terminated_4 = 0.0;
            double total_time_appointment_terminated_5 = 0.0;

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

                if (resultArr5[i][2] - resultArr5[i][1] < 0) {
                    resultArr5[i][2] = resultArr5[i][1];
                }
            }

            // print lists
			/*System.out.printf("\nQueue1 Info: \n%-10s%-15s%-15s%-15s%-15s\n", "Index", "Schedule At", "Arrival At", "Served At", "Is Appointment?");
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

	        System.out.printf("\nQueue5 Info: \n%-10s%-15s%-15s%-15s%-15s\n", "Index", "Schedule At", "Arrival At", "Served At", "Is Appointment?");
			for(int i = 0; i < resultArr5.length; i++) {
	            System.out.printf("%-10d%-15.2f%-15.2f%-15.2f%-15.0f\n", i + 1, resultArr5[i][0],resultArr5[i][1], resultArr5[i][2], resultArr5[i][3]);
	        }
	        */

            // statistics part
	        double finished_time_1 = 0.0;
            double finished_time_2 = 0.0;
            double finished_time_3 = 0.0;
            double finished_time_4 = 0.0;
            double finished_time_5 = 0.0;

            double differ_1, differ_2, differ_3, differ_4, differ_5;

            int number_a_terminated_1 = 0;
            int number_a_terminated_2 = 0;
            int number_a_terminated_3 = 0;
            int number_a_terminated_4 = 0;
            int number_a_terminated_5 = 0;

            int number_g_terminated_1 = 0;
            int number_g_terminated_2 = 0;
            int number_g_terminated_3 = 0;
            int number_g_terminated_4 = 0;
            int number_g_terminated_5 = 0;

            for (int i = 0; i < number_t; i++) {
                differ_1 = resultArr1[i][2] - resultArr1[i][1];
                differ_2 = resultArr2[i][2] - resultArr2[i][1];
                differ_3 = resultArr3[i][2] - resultArr3[i][1];
                differ_4 = resultArr4[i][2] - resultArr4[i][1];
                differ_5 = resultArr5[i][2] - resultArr5[i][1];
                classifyArray(differ_1, differ_2, differ_3, differ_4, differ_5, result);
                if (resultArr1[i][3] == 1) {
                    total_time_appointment_1 += differ_1;
                    if (resultArr1[i][2] < time){
                        total_time_appointment_terminated_1 += differ_1;
                        number_a_terminated_1++;
                    } else {
                        total_time_appointment_terminated_1 += time - resultArr1[i][1];
                    }
                } else {
                    total_time_normal_1 += differ_1;
                    if (resultArr1[i][2] < time){
                        total_time_normal_terminated_1 += differ_1;
                        number_g_terminated_1++;
                    } else {
                        total_time_normal_terminated_1 += time - resultArr1[i][1];

                    }
                }

                if (resultArr2[i][3] == 1) {
                    total_time_appointment_2 += differ_2;
                    if (resultArr2[i][2] < time){
                        total_time_appointment_terminated_2 += differ_2;
                        number_a_terminated_2++;
                    } else {
                        total_time_appointment_terminated_2 += time - resultArr2[i][1];
                    }
                } else {
                    total_time_normal_2 += differ_2;
                    if (resultArr2[i][2] < time){
                        total_time_normal_terminated_2 += differ_2;
                        number_g_terminated_2++;
                    } else {
                        total_time_normal_terminated_2 += time - resultArr2[i][1];
                    }
                }

                if (resultArr3[i][3] == 1) {
                    total_time_appointment_3 += differ_3;
                    if (resultArr3[i][2] < time){
                        total_time_appointment_terminated_3 += differ_3;
                        number_a_terminated_3++;
                    } else {
                        total_time_appointment_terminated_3 += time - resultArr3[i][1];
                    }
                } else {
                    total_time_normal_3 += differ_3;
                    if (resultArr3[i][2] < time){
                        total_time_normal_terminated_3 += differ_3;
                        number_g_terminated_3++;
                    } else {
                        total_time_normal_terminated_3 += time - resultArr3[i][1];
                    }
                }

                if (resultArr4[i][3] == 1) {
                    total_time_appointment_4 += differ_4;
                    if (resultArr4[i][2] < time){
                        total_time_appointment_terminated_4 += differ_4;
                        number_a_terminated_4++;
                    } else {
                        total_time_appointment_terminated_4 += time - resultArr4[i][1];
                    }
                } else {
                    total_time_normal_4 += differ_4;
                    if (resultArr4[i][2] < time){
                        total_time_normal_terminated_4 += differ_4;
                        number_g_terminated_4++;
                    } else {
                        total_time_normal_terminated_4 += time - resultArr4[i][1];
                    }
                }

                if (resultArr5[i][3] == 1) {
                    total_time_appointment_5 += differ_5;
                    if (resultArr5[i][2] < time){
                        total_time_appointment_terminated_5 += differ_5;
                        number_a_terminated_5++;
                    } else {
                        total_time_appointment_terminated_5 += time - resultArr5[i][1];
                    }
                } else {
                    total_time_normal_5 += differ_5;
                    if (resultArr5[i][2] < time){
                        total_time_normal_terminated_5 += differ_5;
                        number_g_terminated_5++;
                    } else {
                        total_time_normal_terminated_5 += time - resultArr5[i][1];
                    }
                }

                finished_time_1 = Math.max(finished_time_1, resultArr1[i][2]);
                finished_time_2 = Math.max(finished_time_2, resultArr2[i][2]);
                finished_time_3 = Math.max(finished_time_3, resultArr3[i][2]);
                finished_time_4 = Math.max(finished_time_4, resultArr4[i][2]);
                finished_time_5 = Math.max(finished_time_5, resultArr5[i][2]);
            }


            try {
                bw11.write(String.format("%-15d%-15d%-15d%-15d%-15d%-15d%-15d%-15d%-15d\n", 1, result[0][0], result[0][1], result[0][2], result[0][3], result[0][4], result[0][5], result[0][6], result[0][7]));
                bw12.write(String.format("%-15d%-15d%-15d%-15d%-15d%-15d%-15d%-15d%-15d\n", 2, result[1][0], result[1][1], result[1][2], result[1][3], result[1][4], result[1][5], result[1][6], result[1][7]));
                bw13.write(String.format("%-15d%-15d%-15d%-15d%-15d%-15d%-15d%-15d%-15d\n", 3, result[2][0], result[2][1], result[2][2], result[2][3], result[2][4], result[2][5], result[2][6], result[2][7]));
                bw14.write(String.format("%-15d%-15d%-15d%-15d%-15d%-15d%-15d%-15d%-15d\n", 4, result[3][0], result[3][1], result[3][2], result[3][3], result[3][4], result[3][5], result[3][6], result[3][7]));
                bw15.write(String.format("%-15d%-15d%-15d%-15d%-15d%-15d%-15d%-15d%-15d\n", 5, result[4][0], result[4][1], result[4][2], result[4][3], result[4][4], result[4][5], result[4][6], result[4][7]));

                bw11.flush();
                bw12.flush();
                bw13.flush();
                bw14.flush();
                bw15.flush();

                bw21.write(String.format("%-20d%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f\n", 1, total_time_appointment_1 / number_a, total_time_appointment_1 / finished_time_1, total_time_normal_1 / (number_t - number_a), total_time_normal_1 / finished_time_1, (total_time_appointment_1 + total_time_normal_1) / number_t, (total_time_appointment_1 + total_time_normal_1) / finished_time_1));
                bw22.write(String.format("%-20d%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f\n", 2, total_time_appointment_2 / number_a, total_time_appointment_2 / finished_time_2, total_time_normal_2 / (number_t - number_a), total_time_normal_2 / finished_time_2, (total_time_appointment_2 + total_time_normal_2) / number_t, (total_time_appointment_2 + total_time_normal_2) / finished_time_2));
                bw23.write(String.format("%-20d%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f\n", 3, total_time_appointment_3 / number_a, total_time_appointment_3 / finished_time_3, total_time_normal_3 / (number_t - number_a), total_time_normal_3 / finished_time_3, (total_time_appointment_3 + total_time_normal_3) / number_t, (total_time_appointment_3 + total_time_normal_3) / finished_time_3));
                bw24.write(String.format("%-20d%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f\n", 4, total_time_appointment_4 / number_a, total_time_appointment_4 / finished_time_4, total_time_normal_4 / (number_t - number_a), total_time_normal_4 / finished_time_4, (total_time_appointment_4 + total_time_normal_4) / number_t, (total_time_appointment_4 + total_time_normal_4) / finished_time_4));
                bw25.write(String.format("%-20d%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f%-25.4f\n", 5, total_time_appointment_5 / number_a, total_time_appointment_5 / finished_time_5, total_time_normal_5 / (number_t - number_a), total_time_normal_5 / finished_time_5, (total_time_appointment_5 + total_time_normal_5) / number_t, (total_time_appointment_5 + total_time_normal_5) / finished_time_5));

                bw21.flush();
                bw22.flush();
                bw23.flush();
                bw24.flush();
                bw25.flush();

                bw31.write(String.format("%-15d%-15d%-15d%-15d\n", 1, number_a - number_a_terminated_1, number_t - number_a - number_g_terminated_1, number_t - (number_a_terminated_1 + number_g_terminated_1)));
                bw32.write(String.format("%-15d%-15d%-15d%-15d\n", 2, number_a - number_a_terminated_2, number_t - number_a - number_g_terminated_2, number_t - (number_a_terminated_2 + number_g_terminated_2)));
                bw33.write(String.format("%-15d%-15d%-15d%-15d\n", 3, number_a - number_a_terminated_3, number_t - number_a - number_g_terminated_3, number_t - (number_a_terminated_3 + number_g_terminated_3)));
                bw34.write(String.format("%-15d%-15d%-15d%-15d\n", 4, number_a - number_a_terminated_4, number_t - number_a - number_g_terminated_4, number_t - (number_a_terminated_4 + number_g_terminated_4)));
                bw35.write(String.format("%-15d%-15d%-15d%-15d\n", 5, number_a - number_a_terminated_5, number_t - number_a - number_g_terminated_5, number_t - (number_a_terminated_5 + number_g_terminated_5)));

                bw31.flush();
                bw32.flush();
                bw33.flush();
                bw34.flush();
                bw35.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bw11.close();
                    bw12.close();
                    bw13.close();
                    bw14.close();
                    bw15.close();
                    bw21.close();
                    bw22.close();
                    bw23.close();
                    bw24.close();
                    bw25.close();
                    bw31.close();
                    bw32.close();
                    bw33.close();
                    bw34.close();
                    bw35.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /*if (job == this.t){
                try{
                    BufferedReader reader1 = new BufferedReader(new FileReader("statistics.txt"));
                    String tempString = null;
                    while ((tempString = reader1.readLine()) != null) {
                        String[] strarray = tempString.split(" ");
                        for(int i = 0; i < strarray.length; i++){
                            if
                        }
                    }
                    reader1.close();
                }catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader1 != null) {
                        try {
                            reader1.close();
                        } catch (IOException e1) {

                        }
                    }
                }

            }*/

            /*System.out.printf("\nResult: \n%-20s%-25s%-25s%-25s%-25s%-25s%-25s\n", "Queue Type", "Ave Appointment Delay", "Ave Appointment Length", "Ave General Delay", "Ave General Length", "Ave Total Delay", "Ave Total Length");
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
            System.out.printf("\nMax Length of Queue: \nType1  Type2  Type3  Type 4\n%-7d%-7d%-7d%-7d\n", queueType1.maxSize(), queueType2.maxSize(), queueType3.maxSize(), queueType4.maxSize());
            System.out.printf("\nEnd Time of Queue: \nType1  Type2  Type3  Type 4\n%-7.2f%-7.2f%-7.2f%-7.2f\n", finished_time_1, finished_time_2, finished_time_3, finished_time_4);*/
        }
    }

    public void classifyArray (double differ_1, double differ_2, double differ_3, double differ_4, double differ_5, int[][] result){
        double differ;
        for (int type = 0; type < 5; type++){
            if (type == 0)
                differ = differ_1;
            else if (type == 1)
                differ = differ_2;
            else if (type == 2)
                differ = differ_3;
            else if (type == 3)
                differ = differ_4;
            else
                differ = differ_5;

            if (differ < 3) {
                result[type][0]++;
            }else if (differ < 6) {
                result[type][1]++;
            }else if (differ < 9) {
                result[type][2]++;
            }else if (differ < 12) {
                result[type][3]++;
            }else if (differ < 15) {
                result[type][4]++;
            }else if (differ < 18) {
                result[type][5]++;
            }else if (differ < 21) {
                result[type][6]++;
            }else {
                result[type][7]++;
            }
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
        if(new File("data/statistics1.txt").exists()) new File("data/statistics1.txt").delete();
        if(new File("data/statistics2.txt").exists()) new File("data/statistics2.txt").delete();
        if(new File("data/statistics3.txt").exists()) new File("data/statistics3.txt").delete();
        if(new File("data/statistics4.txt").exists()) new File("data/statistics4.txt").delete();
        if(new File("data/statistics5.txt").exists()) new File("data/statistics5.txt").delete();

        if(new File("data/average1.txt").exists()) new File("data/average1.txt").delete();
        if(new File("data/average2.txt").exists()) new File("data/average2.txt").delete();
        if(new File("data/average3.txt").exists()) new File("data/average3.txt").delete();
        if(new File("data/average4.txt").exists()) new File("data/average4.txt").delete();
        if(new File("data/average5.txt").exists()) new File("data/average5.txt").delete();

        if(new File("data/remain1.txt").exists()) new File("data/remain1.txt").delete();
        if(new File("data/remain2.txt").exists()) new File("data/remain2.txt").delete();
        if(new File("data/remain3.txt").exists()) new File("data/remain3.txt").delete();
        if(new File("data/remain4.txt").exists()) new File("data/remain4.txt").delete();
        if(new File("data/remain5.txt").exists()) new File("data/remain5.txt").delete();

        MyQueues newsim = new MyQueues(1);

        String[] array_args = new String[3];
        array_args[0] = "-repeat";
        array_args[1] = String.valueOf(newsim.t);
        array_args[2] = "-quiet";

        runSimulationWithArgs(array_args);
    }

    public static void runSimulationWithArgs(String[] args) {
        long t1 = System.currentTimeMillis();
        doLoop(MyQueues.class, args);
        long t2 = System.currentTimeMillis();
        System.out.print("Program finished in " + (t2 - t1) / 1000.0 + " seconds\n");
        System.exit(0);
    }
}


