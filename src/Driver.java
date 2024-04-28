import java.util.*;

public class Driver {

    static Queue<Process> READY_QUEUE = createProcesses(); // Ready Queue initialization
    static final int PROCESSES_NUMBER = 8; // Define the number of processes must be generated in the Ready Queue
    static int MIN_BURST_TIME = 5; // Minimum CPU-Burst time
    static int MAX_BURST_TIME = 100; // Maximum CPU-Burst time
    static int[] Iteration = { 100, 1000, 10000, 100000}; // This an array that represents the number of times must repeat the simulation for each scheduling algorithm
    static double[][][] results = new double[4][Iteration.length][2];
    /*
        Array to store the results
        The first D  => stores the scheduling algorithms (FCFS, SJF, RR, MLFQ)
        The second D => represents the numbers of iterations (100, 1000, 10000, 100000)
        The third D  => represents the ATT and AWT of each algorithm
    */
    static int RR_Q = 20; // The slice time I used to simulate RR algorithm
    static int Q1_TIME_SLICE = 10; // The slice time I used to simulate RR algorithm In the first Queue in the MLFQ algorithm
    static int Q2_TIME_SLICE = 50; // The slice time I used to simulate RR algorithm In the Second Queue in the MLFQ algorithm
    static int currentTime = 0;

    public static void main(String[] args) {
        /* Display the Simulation */
        System.out.println();
        System.out.println("Hello, It's Mohammad Murar Project, ID : 1200698");
        System.out.println("This Project Simulates CPU-Scheduling Algorithms");
        System.out.println();
        fcfsSimulation();
        sjfSimulation();
        rrSimulation();
        mlfqSimulation();
        System.out.println();
    }

    //Method to create 8 processes into (Ready-Queue)
    private static Queue<Process> createProcesses(){
        READY_QUEUE = new LinkedList<Process>();
        Random random = new Random();
        for (int i = 0; i < PROCESSES_NUMBER; i++) {

            /* IMPORTANT ABOUT CPU-Bursts and ARRIVAL TIME */
            int pID = i + 1; // Assigns a Process ID starting from 1 to 8
            int burstTime = random.nextInt(MAX_BURST_TIME - MIN_BURST_TIME + 1) + MIN_BURST_TIME; // each Process take random burst time from 5 to 100
            int arrivalTime = i; // I set the arrival time from 0 to 7 
            READY_QUEUE.add(new Process(pID,arrivalTime,burstTime)); // Added the new Process to the READY QUEUE
        }
        return READY_QUEUE;
    }

    // Method that take ArrayList of Processes and calculate ATT and AWT for the list
    private static double[] calculateMatrices(ArrayList<Process> processes){
        int totalTAT = 0, totalWT = 0;
        for (Process process: processes) {//Iterate through each process in the list
            totalWT += process.WT; // take the total WT of all processes
            totalTAT += process.TAT; // take the total TAT of all processes
        }
        //calculate the ATT and AWT
        double awt = (double) totalWT/ processes.size();
        double att = (double) totalTAT/ processes.size();

        return new double[] {awt, att};
    }

    //FCFS algorithm implementation, that the processes are executed in the order of their arrival.
    private static void FCFS(Queue<Process> READY_QUEUE){
        int finishTime = 0;
        for (Process process : READY_QUEUE) { // for each Processes in the ready queue
            if(finishTime < process.arrivalTime){
                /*
                    * Since this algorithm execute the processes in the order of their arrival time
                    * I must update the finish time to ensure that will maintain the order of execution
                    * based on their arrival Time
                */
                finishTime = process.arrivalTime;
            }
            //now I Know how long the process will work (burst time)
            finishTime += process.cpuBurst;
            //Calculate TAT and WT:
            process.TAT = finishTime - process.arrivalTime;
            process.WT = process.TAT - process.cpuBurst;

        }
    }

    //Simulates the FCFS scheduling algorithm for a specified number of iterations.
    private static void fcfsSimulation(){
        for (int i = 0; i < Iteration.length; i++) { // take the value of Iterations from Iterations array
            double totalATT = 0, totalAWT = 0;
            for (int j = 0; j < Iteration[i]; j++) { //Iterate through each iteration (100 - 100000)
                //I create new set of processes for each iteration
                Queue<Process> processes = new LinkedList<>(createProcesses());
                FCFS(processes); //Apply FCFS
                double[] metrics = calculateMatrices(new ArrayList<>(processes)); // to calculate the ATT and AWT for each number of iterations I used calculateMatrices method above
                //calculateMatrices return array that contain 2 value the AWT and ATT
                totalAWT += metrics[0];
                totalATT += metrics[1];
            }
            //Calculate ATT and AWT for the current iteration
            results[0][i][0] = totalAWT / Iteration[i];
            results[0][i][1] = totalATT / Iteration[i];
        }
        //Printing result
        System.out.println("*******************************************************");
        System.out.println("FCFS Results : ");
        printResults(0);
        System.out.println("*******************************************************");
    }

    //Implement Shortest Job First algorithm with preemption (Start the execution with the first process then check the process with the shortest CPU burst)
    private static void SJF(Queue<Process> processes){
        //initialization of finish time of each process and the number of completed processes
        int finishTime = 0;
        int completed = 0;

        // Continue simulation until all processes are completed
        while (completed != processes.size()){
            //queue that used to store processes that are ready to execute
            Queue<Process> readyP = new LinkedList<>();

            //Iterate through the processes and add the processes are not completed yet
            for (Process process: processes) {
                if(process.arrivalTime <= finishTime && process.remainingTime > 0){
                    readyP.offer(process);
                }
            }

            if(readyP.isEmpty()){
                //find the process with the earliest arrival time
                finishTime = processes.stream().filter(p -> p.remainingTime > 0).mapToInt(p -> p.arrivalTime).min().orElse(Integer.MAX_VALUE);
            } else {
                //select the process with the shortest remainingTime for execution
                Process nextProcess = Collections.min(readyP, Comparator.comparingInt(p -> p.remainingTime));


                int finalFinishTime = finishTime;
                //loop to Iterate over each process in the queue
                //update the WT for Processes that are not currently being executed
                processes.forEach(p -> {
                    if(p != nextProcess && p.arrivalTime <= finalFinishTime && p.remainingTime > 0) {
                        p.WT++;
                    }
                });
                //Execute the ready processes
                nextProcess.remainingTime--;
                finishTime++;

                // If the process is completed, update the TAT and completed
                if(nextProcess.remainingTime ==  0){
                    nextProcess.TAT = finishTime - nextProcess.arrivalTime;
                    completed++;
                }
            }
        }
    }

    //Simulates the SJF scheduling algorithm for a specified number of iterations.
    private static void sjfSimulation(){
        for (int i = 0; i < Iteration.length; i++) { // take the value of Iterations from Iterations array
            double totalATT = 0, totalAWT = 0;
            for (int j = 0; j < Iteration[i]; j++) { //Iterate through each iteration (100 - 100000)
                //I create new set of processes for each iteration
                Queue<Process> processes = new LinkedList<>(createProcesses());
                SJF(processes); //Apply FCFS
                double[] metrics = calculateMatrices(new ArrayList<>(processes)); // to calculate the ATT and AWT for each number of iterations I used calculateMatrices method above
                //calculateMatrices return array that contain 2 value the AWT and ATT
                totalAWT += metrics[0];
                totalATT += metrics[1];
            }
            //Calculate ATT and AWT for the current iteration
            results[0][i][0] = totalAWT / Iteration[i];
            results[0][i][1] = totalATT / Iteration[i];
        }
        //Printing result
        System.out.println("Shortest Job First Results : ");
        printResults(0);
        System.out.println("*******************************************************");
    }

    //implement RR algorithm that give each process time slice for execution
    private static void RR(Queue<Process> processes){
        int currentTime = 0;
        int remainingP = processes.size();// track the number of processes that are not completed yet

        while(remainingP > 0){ // while there is uncompleted processes
            //Iterate through the processes in the queue
            for (Process process : processes){
                if (process.remainingTime > 0){ // checks whether the process has remaining execution time
                    int timeSlice = Math.min(RR_Q,process.remainingTime); //Determine the time slice for the process
                    // update the WT
                    process.WT += currentTime - process.lastExecutionTime;

                    //Execute the process based on Quantum time = 20
                    process.remainingTime -= timeSlice;
                    currentTime += timeSlice;
                    process.lastExecutionTime = currentTime;

                    if (process.remainingTime ==0){ // check if the process is done
                        //update the TAT, and remaining Process will decrease by 1
                        process.TAT = currentTime - process.arrivalTime;
                        remainingP--;
                    }
                }
            }
        }
    }

    //Simulates the RR scheduling algorithm for a specified number of iterations.
    private static void rrSimulation() {
        for (int i = 0; i < Iteration.length; i++) { // take the value of Iterations from Iterations array
            double totalATT = 0, totalAWT = 0;
            for (int j = 0; j < Iteration[i]; j++) { //Iterate through each iteration (100 - 100000)
                //I create new set of processes for each iteration
                Queue<Process> processes = new LinkedList<>(createProcesses());
                RR(processes); //Apply FCFS
                double[] metrics = calculateMatrices(new ArrayList<>(processes)); // to calculate the ATT and AWT for each number of iterations I used calculateMatrices method above
                //calculateMatrices return array that contain 2 value the AWT and ATT
                totalAWT += metrics[0];
                totalATT += metrics[1];
            }
            //Calculate ATT and AWT for the current iteration
            results[0][i][0] = totalAWT / Iteration[i];
            results[0][i][1] = totalATT / Iteration[i];
        }
        //Printing result
        System.out.println("Round Robin With ( Quantum time : 20 ) Results : ");
        printResults(0);
        System.out.println("*******************************************************");
    }

    //Implement MLFQ algorithm that contain 3 queues
    private static void MLFQ(Queue<Process> processes){
        // initialize the 3 queues must use for the algorithm
        Queue<Process> queue1 = new LinkedList<>(processes); // The Entrance Queue
        Queue<Process> queue2 = new LinkedList<>();
        Queue<Process> queue3 = new LinkedList<>();

        currentTime = 0;
        while(!queue1.isEmpty() || !queue2.isEmpty()){// to check if their another process will enter the Entrance queue
            while (!queue1.isEmpty()){ //while the queue1 not empty
                Process p = queue1.poll(); // get the process from the queue
                RR_MLFQ(p, Q1_TIME_SLICE); // using RR_MLFQ method to execute the processes in the first queue

                if(p.remainingTime > 0) { // if the process does not finish yet
                    p.lastExecutionTime = currentTime; // Update where the process reach in its execution
                     queue2.add(p); // and moved the process to the second queue
                }
            }

            // work the same as above loop but with time slice = 50 and if the process does not finish it will go to queue3
            while (!queue2.isEmpty() && queue1.isEmpty()){
                Process p = queue2.poll();
                RR_MLFQ(p, Q2_TIME_SLICE);
                if(p.remainingTime > 0) {
                    p.lastExecutionTime = currentTime;
                    queue3.add(p);
                }
            }

            // while the queue3 have processes it will execute each process using the FCFS algorithm
            while (!queue3.isEmpty() && queue1.isEmpty() && queue2.isEmpty()){
                Process p = queue3.poll(); // take the process from the queue 3 to execute

                //Working like FCFS above but for one process
                if (currentTime < p.arrivalTime){
                    currentTime = p.arrivalTime;
                }
                p.WT = currentTime - p.arrivalTime;
                currentTime += p.cpuBurst;
                p.TAT = currentTime - p.arrivalTime;

                if(p.remainingTime > 0) {
                    p.lastExecutionTime = currentTime; // Update last execution time as we might loop back to queue1 if the process does not finish execution
                }
            }
        }
    }

    //implement RR helper for MLFQ that take one process not list, and it works like the RR method above
    private static void RR_MLFQ(Process process, int quantum){
        int timeSlice = Math.min(quantum, process.remainingTime);
        process.WT += currentTime - process.lastExecutionTime;
        process.remainingTime -= timeSlice;
        currentTime += timeSlice;
        process.lastExecutionTime = currentTime;
        if (process.remainingTime == 0){
            process.TAT = currentTime - process.arrivalTime;
        }
    }

    //Simulates the MLFQ scheduling algorithm for a specified number of iterations.
    private static void mlfqSimulation() {
        for (int i = 0; i < Iteration.length; i++) { // take the value of Iterations from Iterations array
            double totalATT = 0, totalAWT = 0;
            for (int j = 0; j < Iteration[i]; j++) { //Iterate through each iteration (100 - 100000)
                //I create new set of processes for each iteration
                Queue<Process> processes = new LinkedList<>(createProcesses());
                MLFQ(processes); //Apply FCFS
                double[] metrics = calculateMatrices(new ArrayList<>(processes)); // to calculate the ATT and AWT for each number of iterations I used calculateMatrices method above
                //calculateMatrices return array that contain 2 value the AWT and ATT
                totalAWT += metrics[0];
                totalATT += metrics[1];
            }
            //Calculate ATT and AWT for the current iteration
            results[0][i][0] = totalAWT / Iteration[i];
            results[0][i][1] = totalATT / Iteration[i];
        }
        //Printing result
        System.out.println("Multi Level Feedback queue Results : ");
        printResults(0);
        System.out.println("*******************************************************");
    }

    // Method that display the results of the Algorithms as a Table
    private static void printResults(int index) {
        System.out.printf("%-20s%-20s%-20s\n", "Iterations", "ATT", "AWT");
        for (int i = 0; i < results[index].length; i++) { //Iterate on the results Array and get the results of each Iteration
            System.out.printf("%-20d%-20.2f%-20.2f\n", Iteration[i], results[index][i][1], results[index][i][0]);
        }
    }
}
