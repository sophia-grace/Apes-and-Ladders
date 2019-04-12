// NAME: Sophia Trump


package jungle;
import java.util.concurrent.*;
import java.util.Random;


public class Jungle {	
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		
		/*
		 * INVARIANT: This program has no deadlock, livelock, or starvation
		 * regardless of the settings of the configuration variables below, i.e.,
		 * even if there are infinite apes going both ways. It also works regardless
		 * of timing (see invariants in Ape.java).
		 */
	
		int    eastBound = 2; // how many apes going East?
		int    westBound = 2; // how many apes going West?
		
		// create a Ladder
		Ladder l = new Ladder(10);
		
		// creating a Semaphore object for east vs west
        // with number of permits 1 
		Semaphore eastwest_sem = new Semaphore(1);
		
		// creating a Semaphore object for each rung on the ladder
		Semaphore[] rungs_sem = new Semaphore[l.nRungs()]; // put a semaphore at each index
		for(int i = 0; i < rungs_sem.length; i++) {
			rungs_sem[i] = new Semaphore(1);
		}
		
		/*
		 * INVARIANT: All apes in the Jungle will eventually start to cross the ladder.
		 * The code below starts all of the apes, independent of each other 
		 * (as described in the invariants below).
		 */
		
		/* 
		 * INVARIANT: East and west apes starting are independent of each other.
		 * From the code below, east and west apes starting is intermixed.
		 * Thus, if one of the number of apes is infinite, the opposite apes
		 * will still be able to start.
		 */
		
		int i;
		if(eastBound > westBound) {
			i = westBound;
		}
		else {
			i = eastBound;
		} 
		
		// create instance of Random class 
        Random rand = new Random();
		
		// start all of the apes
		int k = 1;
		for(; k < i + 1; k++) {
			Ape east = new Ape("E-" + k, l,true, eastwest_sem, rungs_sem);
			Ape west = new Ape("W-"+ k, l,false, eastwest_sem, rungs_sem);
	
			int random = rand.nextInt(2);
			
			if(random == 0) { // if random number is 0, start east ape first
				east.start();
				west.start();
			}
			else { // if random number is not 0, start west ape first
				west.start();
				east.start();
			}
		}
		
		/*
		 * INVARIANT: Even if the number of east apes != the number of west apes,
		 * all apes will still be started. The code below ensures the remaining
		 * apes are started.
		 */
		
		// start all of the remaining apes (this case occurs if eastBound != westBound)
		int difference = Math.abs(eastBound - westBound);
		if(westBound < eastBound) { // if there are more west apes, start the rest of them
			for(int j = 0; j < difference; j++) { 
				Ape east = new Ape("E-"+ k, l,true, eastwest_sem, rungs_sem);
				east.start();
				k++;
			}
		} 
		else if(westBound > eastBound) { // if there are more west apes, start the rest of them
			for(int j = 0; j < difference; j++) { 
				Ape west = new Ape("W-"+ k, l,false, eastwest_sem, rungs_sem);
				west.start();
				k++;
			}
		} 
		
		// output how long it took to run the code (used for efficiency comparison)
		long endTime = System.nanoTime();

		long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
		System.out.println("Duration of execution Concurrent (millis): " + duration);
	}
	
}
