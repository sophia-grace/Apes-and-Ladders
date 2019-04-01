// NAME: Sophia Trump
// The Lamport algorithm, implemented in Java.

package jungle;


public class Lamport {
	int N;
	boolean[] choosing;
	int[] number; // every process has a number, the priority of the process with lower number is highest priority

	public Lamport(int numProcesses) { //constructor
		N= numProcesses;
		System.out.print("number of apes: " + numProcesses);
		choosing = new boolean[N];
		number = new int[N];
		for(int j = 0; j < N; j++) {
			choosing[j] = false; // initialize all to false
			number[j] = 0;
		}
	}

	public void requestCS(int pid) { // request access to critical section
		choosing[pid] = true;
		for(int j = 0; j < N; j++) {
			if(number[j] > number[pid]) {
				number[pid] = number[j]; // set number[pid] to the largest thing in number[]
			}
		}
		number[pid]++;
		choosing[pid] = false;
		for(int j = 0; j < N; j++) {
			while(choosing[j]); //if some other process is currently in the CS, wait
			while((number[j] != 0) && (number[j] < number[pid]) || (number[j] == number[pid]) && (pid > j)); //if the current process does not have the highest priority, wait
		}
	}

	public void releaseCS(int pid) { //unlock the CS, current process is done
		number[pid] = 0; //priority is zero, not in the queue at all
	}

}
