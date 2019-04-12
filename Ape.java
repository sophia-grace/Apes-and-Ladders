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
		
		/*
		 * INVARIANT: Only apes going the same direction will ever be in this function at the same time.
		 * See logic presented in the run() method for more details.
		 * However, this function supports either direction via if statements.
		 */
		
		int startRung, move, endRung;
		
		/*
		 * INVARIANT: East apes will go east and west apes will go west.
		 * This is ensured through the following if/else statements.
		 */
		
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
	
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\tApe " + _name + " wants rung " + startRung);

		/* 
		 * INVARIANT: only 1 ape can acquire the start rung at a time. 
		 * Other apes must wait in the semaphore until the start rung is "released".
		 * The apes will eventually acquire the rung (see logic below related to releasing the semaphore).
		 * */
		
		// set the start rung to busy
		try {
			// acquiring the lock for start
			rungs_sem[startRung].acquire(); 
		} catch (InterruptedException exc) { 
			System.out.println(exc); 
		} 			

		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tApe " + _name + "  got  rung " + startRung);

		/*
		 * INVARIANT: All apes will finish crossing the ladder.
		 * This logic comes from the for loop going from the startRung to the endRung, 
		 * combined with the logic presented below about the rungs semaphore, as well as
		 * the logic that all apes on the ladder at once are traveling the same direction.
		 */
			
		for (int i = startRung+move; i!=endRung+move; i+=move) {
			System.out.println("\t\t\t\t\t\t\t\t\t\t\t\tApe " + _name + " wants rung " + i);	

			// check if the rung can be grabbed (that there is no ape on the next rung)
			try {
				
				/*
				 * INVARIANT: only 1 ape is on a given rung at a time.
				 * Apes will wait until the next rung they want is free (given by the semaphore).
				 * Apes will eventually get that rung (see the invariant 2 below this one).
				 */
				
				/*
				 * INVARIANT: The first ape crossing will always have available its next rung.
				 * This logic comes from the invariant that apes traveling opposing directions
				 * will never be on the ladder at the same time (see invariants in the run() method).
				 */
				
				rungs_sem[i].acquire();

				// if the next rung is empty, grab that rung
				System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tApe " + _name + "  got  " + i + " releasing " + (i-move));			
				_ladderToCross.releaseRung(i-move);

				
				/* 
				 * INVARIANT: Apes that want a rung that is busy will eventually get that rung.
				 * Once an ape has moved to its next rung (guaranteed by the above logic), it always releases the previous,
				 * allowing apes waiting to access that rung to gain access to it via its semaphore.
				 */
				
				// release the lock on the previous rung
				rungs_sem[i-move].release();
			} catch (InterruptedException exc) { 
				System.out.println(exc); 
			}	
			
			
			/*
			 * INVARIANT: This section of code (the following if statement) will never be needed.
			 * From the invariants above, apes will always wait to grab the next rung until it is actually free.
			 */
			
			// the rung can't be grabbed, so the ape falls (assumes the above fails)
			if (!_ladderToCross.grabRung(i)) {
				System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tApe " + _name + ": AAaaaaaah!  falling off the ladder :-(");
				System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tApe " + _name + " has been eaten by the crocodiles!");
				_ladderToCross.releaseRung(i-move); /// so far, we have no way to wait, so release the old lock as we die :-(

				// release the lock that was dropped from
				rungs_sem[i].release();

				// decrement the number of east apes crossing
				try {
					// acquiring the lock 
					eastwest_sem.acquire(); 
					if(_goingEast) {
						eastCrossing -= 1;
					}
					else {
						westCrossing -= 1;
					}
				} catch (InterruptedException exc) { 
					System.out.println(exc); 
				}
				eastwest_sem.release();

				// notify waiting apes of opposite direction that apes of current direction are done crossing
				if(_goingEast) {
					if(eastCrossing == 0) {
						synchronized(_ladderToCross) {
							_ladderToCross.notifyAll();
						}
					}
				}
				else {
					if(westCrossing == 0) {
						synchronized(_ladderToCross) {
							_ladderToCross.notifyAll();
						}
					}

				return;  //  died
				}
			}
		}
			
		/*
		 * INVARIANT: All apes will eventually reach their endRung.
		 * This comes from the fact that all apes will never grab rungs that are not
		 * available (and thus will never fall) that there will never be apes coming from the 
		 * opposite direction, and that the for() loop iterates from the startRung to endRung.
		 * (Also from the the fact that the endRung is always released once it is grabbed)
		 */

		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tApe " + _name + " releasing " + endRung);			
		_ladderToCross.releaseRung(endRung);
		System.out.println("\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t**APE " + _name + " FINISHED GOING " + (_goingEast?"EAST":"WEST") + "**\n");			

		// free up the released rung
		rungs_sem[endRung].release();

		
		/*
		 * INVARIANT: Only 1 ape will be able to decrement the number of apes on the ladder at once.
		 * This is guaranteed via semaphore access to the shared variable.
		 * All apes that wish to decrement the number of apes on the ladder will eventually be able to,
		 * since the semaphore is always released after an ape has used it.
		 */
		
		// decrement the number of apes crossing the current ape's direction
		try {
			// acquiring the lock 
			eastwest_sem.acquire(); 
			if(_goingEast) {
				eastCrossing -= 1;
			}
			else {
				westCrossing -= 1;
			}
		} catch (InterruptedException exc) { 
			System.out.println(exc); 
		}
		eastwest_sem.release();
		
		/*
		 * INVARIANT: All waiting apes that wish to travel the opposite direction will eventually be able to do so.
		 * This comes from the fact that all apes will eventually finish crossing (see the invariants above),
		 * making the number of apes crossing that direction eventually equal 0. Thus, the apes that are waiting will be notified
		 * once this occurs.
		 */
		
		// notify waiting apes of the opposite direction that opposing apes are done crossing
		if(_goingEast) {
			if(eastCrossing == 0) {
				synchronized(_ladderToCross) {
					_ladderToCross.notifyAll();
				}
			}
		}
		else {
			if(westCrossing == 0) {
				synchronized(_ladderToCross) {
					_ladderToCross.notifyAll();
				}
			}
		}

		return; //survived!
	} 


	public  void run() {
		
		/*
		 * INVARIANT: All apes will eventually want to cross the ladder.
		 * This comes from invariants presented in the Jungle.java file.
		 */
		
		System.out.println("Ape " + _name + " wants to cross the ladder.");
		
		/*
		 * INVARIANT: Only apes going the same direction will ever be on the ladder
		 * at the same time. This comes from the following code in which apes wait
		 * if the number of apes currently crossing the ladder in the opposite direction
		 * is > 0. Since the following code is in a synchronized block, only 1 thread at a time
		 * will be able to check if it is safe to start crossing. Thus, it cannot be that
		 * 2 threads check simultaneously, both see that the number of threads traveling the
		 * opposite direction is 0, then both proceed to cross the ladder (resulting in deadlock).
		 * By extension, it will never be that the number of east AND west apes crossing > 0 
		 * (also resulting in deadlock).
		 */
		
		/*
		 * INVARIANT: Threads that are meant to wait because threads of the opposite
		 * direction are currently crossing will eventually be notified and get to cross, no
		 * threads will be waiting forever.
		 * See the invariants in the crossLadder() method for more details.
		 */

		// check first if there are apes going the opposite direction
		synchronized(_ladderToCross) {
			if(_goingEast) {
				while(westCrossing > 0) { // there are west apes crossing, so east apes wait
					System.out.println("\t\t\t\t\tAPE " + _name + " DENIED ACCESS TO THE LADDER.");
					try {
						_ladderToCross.wait();
					} catch (InterruptedException exc) {
						System.out.println(exc);
					}
				}
			}
			else {
				//synchronized(_ladderToCross) {
				while(eastCrossing > 0) { // there are east apes crossing, so west apes wait
					System.out.println("\t\t\t\t\tAPE " + _name + " DENIED ACCESS TO THE LADDER.");
					try {
						_ladderToCross.wait();
					} catch (InterruptedException exc) {
						System.out.println(exc);
					}
				}
			}
		}
		
		/*
		 * INVARIANT: Only 1 thread at a time is able to modify the eastCrossing and westCrossing
		 * variables. But, all threads that want to will eventually be able to.
		 * This is ensured via semaphore.
		 */
		
		// given permission, so then start crossing
		// first, increment the number of apes going in that direction (for use in the previous check)
		try {
			eastwest_sem.acquire();
			if(_goingEast) {
				eastCrossing += 1;
			}
			else {
				westCrossing += 1;
			}
		} catch (InterruptedException exc) { 
			System.out.println(exc); 
		}
		eastwest_sem.release();

		System.out.println("\t\t\t\t\tApe " + _name + " has been given permission to start crossing.");
		
		// now actually start crossing
		crossLadder(this);

		return;
	}
}