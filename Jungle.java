// NAME: Sophia Trump


/*
 * Created on Feb 12, 2005
 *
 */
package jungle;
import java.util.concurrent.*;

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
		int    eastBound = 3; // how many apes going East? use -1 for inifinity
		int    westBound = 3; // how many apes going West? use -1 for inifinity
		
		
		// create a Ladder
		Ladder l = new Ladder(4);
		
		// creating a Semaphore object for east vs west
        // with number of permits 1 
		Semaphore eastwest_sem = new Semaphore(1);
		
		
		// creating a Semaphore object for each rung on the ladder
		Semaphore[] rungs_sem = new Semaphore[l.nRungs()]; // put a semaphore at each index
		for(int i = 0; i < rungs_sem.length; i++) {
			rungs_sem[i] = new Semaphore(1);
		}
		
		
		// create some Eastbound apes who want that ladder
		int nRemaining = eastBound;
		int apeCounter = 1;
		while (nRemaining != 0) {
			Ape a = new Ape("E-" + apeCounter, l,true, eastwest_sem, rungs_sem);
			a.start();
			apeCounter++;
			
			if (nRemaining > 0)
				nRemaining--;
		}

	
				
		// and create some Westbound apes who want the SAME ladder
		nRemaining = westBound;
		apeCounter=1;
		while (nRemaining != 0) {
			Ape a = new Ape("W-"+ apeCounter, l,false, eastwest_sem, rungs_sem);
			a.start();
			apeCounter++;
			
			if (nRemaining > 0)
				nRemaining--;
		} 
	}
}