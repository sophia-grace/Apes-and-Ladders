// NAME: Sophia Trump


/*
 * Created on Feb 12, 2005
 *
 */
package jungle;
import java.util.concurrent.*;
import java.util.Random;

/**
 * @author davew
 *
 * This class just exists to create the objects and threads we need:
 *  One ladder and many apes.
 * You should not need to change anything here unless you want to
 *  use it to add other objects that aren't associated with some
 *  existing object (an ape or ladder).
 */
public class Jungle {	
	public static void main(String[] args) {
		//
		//  A solution for Lab 3 should work (have no deadlock, livelock, or starvation)
		//    regardless of the settings of the configuration variables below,
		//    i.e., even if there are infinite apes going both ways.
		//  It should also work regardless of timing, so any values for the
		//    timing configuration should work, and there should be no way to
		//    add spurious "tryToSleep"'s *anywhere* to mess it up.
		//
		int    eastBound = 2; // how many apes going East? use -1 for inifinity
		int    westBound = 2; // how many apes going West? use -1 for inifinity
		
		
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
		

		//long endTime = System.nanoTime();

	//	long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
	//	System.out.println("Duration of execution Concurrent (millis): " + duration);
	}
	
}
