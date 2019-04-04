// NAME: Sophia Trump

/* 
 * Resources used:
 * https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Semaphore.html
 * https://stackoverflow.com/questions/2536692/a-simple-scenario-using-wait-and-notify-in-java
 * https://www.cs.umd.edu/~hollings/cs412/s96/synch/eastwest.html
 * http://www.cs.umd.edu/~shankar/412-Notes/15-EastWestBridge.html
 */

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
	static private final double rungDelayMin=0.8;
	static private final double rungDelayVar=1.0;
	private String _name;
	private Ladder _ladderToCross;
	private boolean _goingEast; // if false, going west
	static int eastCrossing = 0; // tells if an east ape is currently crossing
	static int westCrossing = 0; // tells if a west ape is currently crossing
	Semaphore eastwest_sem; // the semaphore to lock eastCrossing variable
	Semaphore rungs_sem[]; // the semaphore to lock each rung on the ladder

	
	
	public Ape(String name, Ladder toCross, boolean goingEast, Semaphore eastwest_sem, Semaphore rungs_sem[]) {
		_name = name;
		_ladderToCross = toCross;
		_goingEast = goingEast;
		this.eastwest_sem = eastwest_sem;
		this.rungs_sem = rungs_sem;
	}
	
	public void crossLadder(Thread currentApe) {
		int startRung, move, endRung;
		
		if(_goingEast) {
			startRung = 0;
			endRung = _ladderToCross.nRungs() - 1;
			move = 1;
		}
		else {
			startRung = _ladderToCross.nRungs() - 1;
			endRung = 0;
			move = -1;
		}
		
		System.out.println("Ape " + _name + " wants rung " + startRung);
		
		// set the start rung to busy
		try {
			// acquiring the lock for start
			rungs_sem[startRung].acquire(); 
			
		} catch (InterruptedException exc) { 
			System.out.println(exc); 
		} 			
		System.out.println("Ape " + _name + "  got  rung " + startRung);
	
		
		if(_goingEast) { //if it is an East ape and there are no apes crossing west
			
			for (int i = startRung+move; i!=endRung+move; i+=move) {

				System.out.println("Ape " + _name + " wants rung " + i);	

				// check if the rung can be grabbed (that there is no ape on the next rung)
				try {
			
					rungs_sem[i].acquire();
					
					// if the next rung is empty, grab that rung
					System.out.println("Ape " + _name + "  got  " + i + " releasing " + (i-move));			
					_ladderToCross.releaseRung(i-move);
							
					// release the lock on the previous rung
					rungs_sem[i-move].release();
				} catch (InterruptedException exc) { 
					System.out.println(exc); 
				}	
					
				
				// the rung can't be grabbed, so the ape falls (assumes the above fails)
				if (!_ladderToCross.grabRung(i)) {
					System.out.println("Ape " + _name + ": AAaaaaaah!  falling off the ladder :-(");
					System.out.println("  Ape " + _name + " has been eaten by the crocodiles!");
					_ladderToCross.releaseRung(i-move); /// so far, we have no way to wait, so release the old lock as we die :-(
					
					// release the lock that was dropped from
					rungs_sem[i].release();
					
					// decrement the number of east apes crossing
					try {
						// acquiring the lock 
						eastwest_sem.acquire(); 
						eastCrossing -= 1;
						
						
					} catch (InterruptedException exc) { 
						System.out.println(exc); 
					}
					eastwest_sem.release();
					
					// notify waiting west apes if east apes are not crossing
					if(eastCrossing == 0) {
						synchronized(_ladderToCross) {
							_ladderToCross.notifyAll();
						}
					}
					
					return;  //  died
				}
			}
		
			System.out.println("Ape " + _name + " releasing " + endRung);			
			_ladderToCross.releaseRung(endRung);
			System.out.println("Ape " + _name + " finished going " + (_goingEast?"East.":"West."));
			
			// free up the released rung
			rungs_sem[endRung].release();
			
			// decrement the number of east apes crossing
			try {
				// acquiring the lock 
				eastwest_sem.acquire(); 
				eastCrossing -= 1;
				
				
			} catch (InterruptedException exc) { 
				System.out.println(exc); 
			}
			eastwest_sem.release();
			
			// notify waiting west apes if east apes are done crossing
			if(eastCrossing == 0) {
				synchronized(_ladderToCross) {
					_ladderToCross.notifyAll();
				}
			}
			
			return; //survived!
		} 
		else if(!(_goingEast)) { //if it is an west ape and there are no apes crossing east
			
			for (int i = startRung+move; i!=endRung+move; i+=move) {
			
				System.out.println("Ape " + _name + " wants rung " + i);	

				// check if the rung can be grabbed (that there is no ape on the next rung)
				try {
					
					rungs_sem[i].acquire();
					
					// if the next rung is empty, grab that rung
					System.out.println("Ape " + _name + "  got  " + i + " releasing " + (i-move));			
					_ladderToCross.releaseRung(i-move);
							
					// release the lock on the previous rung
					rungs_sem[i-move].release();
				} catch (InterruptedException exc) { 
					System.out.println(exc); 
				}	
					
				
				// the rung can't be grabbed, so the ape falls (assumes the above fails)
				if (!_ladderToCross.grabRung(i)) {
					System.out.println("Ape " + _name + ": AAaaaaaah!  falling off the ladder :-(");
					System.out.println("  Ape " + _name + " has been eaten by the crocodiles!");
					_ladderToCross.releaseRung(i-move); /// so far, we have no way to wait, so release the old lock as we die :-(
					
					// release the lock that was dropped from
					rungs_sem[i].release();
					
					// decrement the number of west apes crossing
					try {
						// acquiring the lock 
						eastwest_sem.acquire(); 
						westCrossing -= 1;
						
						
					} catch (InterruptedException exc) { 
						System.out.println(exc); 
					}
					eastwest_sem.release();
					
					// notify waiting east apes if west apes are done crossing
					if(westCrossing == 0) {
						synchronized(_ladderToCross) {
							_ladderToCross.notifyAll();
						}
					}
					
					return;  //  died
				}
			}
		
			System.out.println("Ape " + _name + " releasing " + endRung);			
			_ladderToCross.releaseRung(endRung);
			System.out.println("Ape " + _name + " finished going " + (_goingEast?"East.":"West."));
			
			
			// free up the released rung
			rungs_sem[endRung].release();
			
			// decrement the number of west apes crossing
			try {
				// acquiring the lock 
				eastwest_sem.acquire(); 
				westCrossing -= 1;
							
							
			} catch (InterruptedException exc) { 
				System.out.println(exc); 
			}
			eastwest_sem.release();
			
			
			// notify waiting east apes if west apes are done crossing
			if(westCrossing == 0) {
				synchronized(_ladderToCross) {
					_ladderToCross.notifyAll();
				}
			}
			
			return; //survived!
		}
	}
	
	public void run() {
		
		if (_goingEast) {
			
			// check first if there are apes going west
			try {
				// acquiring the lock 
				eastwest_sem.acquire(); 
				eastCrossing += 1;
			} catch (InterruptedException exc) { 
				System.out.println(exc); 
			}
			eastwest_sem.release();
			
			
			// are there currently west apes crossing?
			// if yes, wait
			synchronized(_ladderToCross) {
				while(westCrossing > 0) {
					try {
						_ladderToCross.wait();
					} catch (InterruptedException exc) {
						System.out.println(exc);
					}
				}
			}
			
			
			// now actually start crossing
			crossLadder(this);

			return;
		}
		 else { // going west
			 
			 //check first if there are apes going east
			try {
				// acquiring the lock 
				eastwest_sem.acquire(); 
				westCrossing += 1;
					
			} catch (InterruptedException exc) { 
				System.out.println(exc); 
			}  
			eastwest_sem.release();
			
			
			// are there currently east apes crossing?
			// if yes, wait
			synchronized(_ladderToCross) {
				while(eastCrossing > 0) {
					try {
						_ladderToCross.wait();
					} catch (InterruptedException exc) {
						System.out.println(exc);
					}
				}
			} 
				
			// now actually start crossing
			crossLadder(this);
		
			return;
		}
		
	}
}