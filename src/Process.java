public class Process { // Process class that contain important attributes declarations
    int processID;
    int arrivalTime;
    int cpuBurst;
    int TAT; // Turnaround Time
    int WT; // Waiting Time
    int remainingTime; //attribute to help me with implement SJF and RR
    int lastExecutionTime;

    public Process(int processID, int arrivalTime, int cpuBurst) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.cpuBurst = cpuBurst;
        this.remainingTime = cpuBurst;
        this.WT = 0;
        this.TAT = 0;
        this.lastExecutionTime = 0;
    }
}
