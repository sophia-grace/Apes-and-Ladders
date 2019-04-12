/*
 * Created on Feb 12, 2005
 */
package jungle_notConcurrent;
import java.util.concurrent.*;

/*
 * @author davew
 *
 * The Ape class is a kind of thread,
 *  since all Apes can go about their activities concurrently
 * Note that each Ape has his or her own name and direction,
 *  but in this system, many Apes will share one Ladder.
 */
public class Ape_notConcurrent extends Thread {
	private String _name;
	private Ladder_notConcurrent _ladderToCross;
	private boolean _goingEast; // if false, going west
	static Semaphore ladder = new Semaphore(1);
	
	public Ape_notConcurrent(String name, Ladder_notConcurrent toCross, boolean goingEast) {
		_name = name;
		_ladderToCross = toCross;
		_goingEast = goingEast;
	}
	
	public void crossLadder(Thread currentApe) {
		int startRung, move, endRung;
		
		System.out.println("Ape " + _name + " wants to start crossing.");
		
		try {
			ladder.acquire();
		} catch (InterruptedException exc) { 
            System.out.println(exc); 
        } 
		
		System.out.println("Ape " + _name + " has been given permission to cross.");
		
		System.out.println("Ape " + _name + " starting to go " + (_goingEast?"East.":"West."));
		if (_goingEast) {
			startRung = 0;
			endRung = _ladderToCross.nRungs()-1;
			move = 1;
		} else {
			startRung = _ladderToCross.nRungs()-1;
			endRung = 0;
			move = -1;
		}
		
		System.out.println("Ape " + _name + " wants rung " + startRung);			
		if (!_ladderToCross.grabRung(startRung)) {
			System.out.println("  Ape " + _name + " has been eaten by the crocodiles!");
			
			ladder.release();
			
			return;  // died
		}
		
		System.out.println("Ape " + _name + "  got  rung " + startRung);			
		for (int i = startRung+move; i!=endRung+move; i+=move) {
			//Jungle.tryToSleep(rungDelayMin, rungDelayVar);
			
			System.out.println("Ape " + _name + " wants rung " + i);			
			if (!_ladderToCross.grabRung(i)) {
				System.out.println("Ape " + _name + ": AAaaaaaah!  falling off the ladder :-(");
				System.out.println("  Ape " + _name + " has been eaten by the crocodiles!");
				_ladderToCross.releaseRung(i-move); /// so far, we have no way to wait, so release the old lock as we die :-(
				
				ladder.release();
				
				return;  //  died
			}
			
			System.out.println("Ape " + _name + "  got  " + i + " releasing " + (i-move));			
			_ladderToCross.releaseRung(i-move);
		}
		
		System.out.println("Ape " + _name + " releasing " + endRung);			
		_ladderToCross.releaseRung(endRung);
		
		System.out.println("Ape " + _name + " finished going " + (_goingEast?"East.":"West."));
	
		ladder.release();
		
		return;  // survived!
	}
	
	public void run() {
		crossLadder(this);
	}
}
