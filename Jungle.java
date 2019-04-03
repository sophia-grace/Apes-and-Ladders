// NAME: Sophia Trump


/*
 * Created on Feb 12, 2005
 *
 */
package jungle;
import java.util.Random;
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
		int    eastBound = 0; // how many apes going East? use -1 for inifinity
		int    westBound = 20; // how many apes going West? use -1 for inifinity
		int    numApes = eastBound + westBound; // the total number of apes
		double apeMin = 4.0;  // how long to wait between consecutive apes going one way
		double apeVar = 1.0;  //  4 seconds is usually enough, but vary a bit to see what happens
		double sideMin = 5.0; // how long to wait before coming back across
		double sideVar = 0.0; //  5.0 seconds is usually enough 
		
		
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
		
		
		
	/*	// create arrays of threads (so can later intermix .start of east and west apes)
		Thread eastThreads[] = new Thread[eastBound];
		Thread westThreads[] = new Thread[westBound];
		
		// and create some Westbound apes who want the SAME ladder
		for(int i = 0; i < westThreads.length; i++) {
			Ape a = new Ape("W-", (i + 1), l,false, eastwest_sem, rungs_sem);
			westThreads[i] = a;
		}
		
		// create some Eastbound apes who want that ladder
		for(int i = 0; i < eastThreads.length; i++) {
			Ape a = new Ape("E-", (i + 1), l,true, eastwest_sem, rungs_sem);
			eastThreads[i] = a;
		}
		
		
		// intermix start of east and west threads
		if(eastBound > westBound) {
			int i = 0;
			while(i < westThreads.length) {
				eastThreads[i].start();
				westThreads[i].start();
				i++;
			}
			
			// now start the rest of the east threads
			while(i < eastThreads.length) {
				eastThreads[i].start();
				i++;
			}
			
		}
		else if(eastBound < westBound) {
			int i = 0;
			while(i < eastThreads.length) {
				westThreads[i].start();			
				eastThreads[i].start();
				i++;
			}
			
			// now start the rest of the west threads
			while(i < westThreads.length) {
				westThreads[i].start();
				i++;
			}			
		}
		else { // eastBound == westBound
			int i = 0;
			while(i < eastThreads.length) {
				westThreads[i].start();
				eastThreads[i].start();
				i++;
			}
		} */
		
		
		// create some Eastbound apes who want that ladder
				int nRemaining = eastBound;
				int apeCounter = 1;
				while (nRemaining != 0) {
					Ape a = new Ape("E-", apeCounter, l,true, eastwest_sem, rungs_sem);
					a.start();
					apeCounter++;
			
					if (nRemaining > 0)
						nRemaining--;
				}

	
				
				// and create some Westbound apes who want the SAME ladder
				nRemaining = westBound;
				apeCounter=1;
				while (nRemaining != 0) {
					Ape a = new Ape("W-", apeCounter, l,true, eastwest_sem, rungs_sem);
					a.start();
					apeCounter++;
				
					if (nRemaining > 0)
						nRemaining--;
				}
			


	} 

	private static java.util.Random dice = new java.util.Random(); // random number generator, for delays mostly	
	public static void tryToSleep(double secMin, double secVar) {
        try {
            java.lang.Thread.sleep(Math.round(secMin*1000) + Math.round(dice.nextDouble()*(secVar)*1000));
        } catch (InterruptedException e) {
            System.out.println("Not Handling interruptions yet ... just going on with the program without as much sleep as needed ... how appropriate!");
        }
	}
}