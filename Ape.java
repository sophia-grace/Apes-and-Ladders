// NAME: Sophia Trump


/*
*Created on Feb 12, 2005
 */
package jungle;
import java.util.concurrent.*;

/*
 * @author davew
 *
 * The Ape class is a kind of thread,
 *  since all Apes can go about their activities concurrently
 * Note that each Ape has his or her own name and direction,
 *  but in this system, many Apes will share one Ladder.
 */
public class Ape extends Thread {
	static private final boolean debug=true;  // "static" is shared by all Apes
	static private final double rungDelayMin=0.8;
	static private final double rungDelayVar=1.0;
	private String _name;
	private Ladder _ladderToCross;
	private boolean _goingEast; // if false, going west
	static int eastCrossing; // tells if an east ape is currently crossing
	static int westCrossing; // tells if a west ape is currently crossing
	Semaphore sem; // the semaphore to lock eastCrossing variable
	
	public Ape(String name, Ladder toCross, boolean goingEast, Semaphore sem) {
		_name = name;
		_ladderToCross = toCross;
		_goingEast = goingEast;
		this.sem = sem;
	}
	
	public void run() {
		int startRung, move, endRung;
		
		System.out.println("Ape " + _name + " starting to go " + (_goingEast?"East.":"West."));
		if (_goingEast) {
			try {
				// acquiring the lock 
				sem.acquire(); 
				eastCrossing+= 1;
				
			} catch (InterruptedException exc) { 
				System.out.println(exc); 
			} 
			// release the lock
			sem.release();
			
			// permit has been acquired, so
			// start and move on the ladder
			if(westCrossing != 0) { // don't start unless no west apes are crossing 
				startRung = 0;
				endRung = _ladderToCross.nRungs()-1;
				move = 1;
			}
			else { // a west ape is crossing, so don't start
				startRung = 0;
				endRung = 0;
				move = 0;
			} 
		}
		 else { // going west
			try {
				// acquiring the lock 
				sem.acquire(); 
				westCrossing += 1;
					
			} catch (InterruptedException exc) { 
				System.out.println(exc); 
			} 
			// release the lock
			sem.release();
				
			// permit has been acquired, so
			// start and move on the ladder
			if(eastCrossing != 0) { // don't start unless no east apes are crossing */
				startRung = 0;
				endRung = _ladderToCross.nRungs()-1;
				move = 1;
			}
			else { // an east ape is crossing, so don't start
				startRung = 0;
				endRung = 0;
				move = 0;
			} 
		}
		
		if (debug)
			System.out.println("Ape " + _name + " wants rung " + startRung);			
		if (!_ladderToCross.grabRung(startRung)) {
			System.out.println("  Ape " + _name + " has been eaten by the crocodiles!");
			return;  // died
		}
		if (debug)
			System.out.println("Ape " + _name + "  got  rung " + startRung);			
		for (int i = startRung+move; i!=endRung+move; i+=move) {
			Jungle.tryToSleep(rungDelayMin, rungDelayVar);
			if (debug)
				System.out.println("Ape " + _name + " wants rung " + i);			
			if (!_ladderToCross.grabRung(i)) {
				System.out.println("Ape " + _name + ": AAaaaaaah!  falling off the ladder :-(");
				System.out.println("  Ape " + _name + " has been eaten by the crocodiles!");
				_ladderToCross.releaseRung(i-move); /// so far, we have no way to wait, so release the old lock as we die :-(
				return;  //  died
			}
			if (debug)
				System.out.println("Ape " + _name + "  got  " + i + " releasing " + (i-move));			
			_ladderToCross.releaseRung(i-move);
		}
		if (debug)
			System.out.println("Ape " + _name + " releasing " + endRung);			
		_ladderToCross.releaseRung(endRung);
		
		System.out.println("Ape " + _name + " finished going " + (_goingEast?"East.":"West."));
		
		// finished crossing, so update the crossing vars via semaphore access
		try {
			// acquiring the lock
			sem.acquire();
			if(_goingEast) {
				eastCrossing -= 1; // east ape is done
			}
			else {
				westCrossing -= 1; // west ape is done
			}
		} catch (InterruptedException exc) {
			System.out.print(exc);
		} 
		sem.release(); 
		return;  // survived!
	}
}
