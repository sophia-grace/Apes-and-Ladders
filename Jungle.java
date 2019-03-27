/*
 * Created on Feb 12, 2005
 *
 */
package jungle;
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
		int    eastBound = 4; // how many apes going East? use -1 for inifinity
		int    westBound = 2; // how many apes going West? use -1 for inifinity
		double apeMin = 4.0;  // how long to wait between consecutive apes going one way
		double apeVar = 1.0;  //  4 seconds is usually enough, but vary a bit to see what happens
		double sideMin = 5.0; // how long to wait before coming back across
		double sideVar = 0.0; //  5.0 seconds is usually enough 
		
		// create a Ladder
		Ladder l = new Ladder(4);
		
		// create some Eastbound apes who want that ladder
		int nRemaining = eastBound;
		int apeCounter = 1;
		while (nRemaining != 0) {
			Ape a = new Ape("E-"+apeCounter, l,true);
			a.start();
			apeCounter++;
			tryToSleep(apeMin, apeVar);
			if (nRemaining > 0)
				nRemaining--;
		}

		// put this in to create a pause that will avoid the problem BUT OF COURSE THIS IS NOT A SOLUTION TO THE LAB!
		tryToSleep(sideMin, sideVar);
		
		// and create some Westbound apes who want the SAME ladder
		nRemaining = westBound;
		apeCounter=1;
		while (nRemaining != 0) {
			Ape a = new Ape("W-"+apeCounter, l,false);
			a.start();
			apeCounter++;
			tryToSleep(apeMin, apeVar);
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

