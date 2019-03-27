/*
 * Created on Feb 12, 2005
 */
package jungle;

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
	
	public Ape(String name, Ladder toCross, boolean goingEast) {
		_name = name;
		_ladderToCross = toCross;
		_goingEast = goingEast;
	}
	
	public void run() {
		int startRung, move, endRung;
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
		return;  // survived!
	}
}
