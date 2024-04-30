# CPU-Scheduling-Algorithms
## Operating system project
### In this project I semulated the CPU-Scheduling using some algorithms and making a comparison between them using:
1. FCFS (first come first served)
2. SJF with preemption (shortest job first)
3. RR (Round Robin)
4. MLFQ (Multilevel Feedback Queue)

. Creates 8 processes in the ready queue with a random CPU-burst for each process between 5- & 100-time units.\n
. When creating the processes, assign some kind of order (arrival time) for their arrival.\n
. For RR, use a slice time (quantum, Q = 20 units).\n
. In multilevel feedback Queue, consider 3 queues,\n
    -Q1: 	RR with 10 units\n
    -Q2:	RR with 50 units.\n
    -Q3: FCFS\n
. For each of the scheduling algorithms above, you should compute the average turnaround time (ATT) and average waiting time (AWT).\n
. Repeat steps (2) for 100 times, 1000 times, 10000 times, and 100000 times.\n
