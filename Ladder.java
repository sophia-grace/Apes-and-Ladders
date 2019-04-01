//NAME: Sophia Trump


/*
 * Created on Feb 12, 2005
 */
package jungle;

/**
 * @author davew
 *
 * The Ladder class is NOT a kind of thread,
 *  since it doesn't actually do anything except get used by Apes.
 * The ladder just keeps track of how many apes are on each rung.
 */

public class Ladder {
	private int rungCapacity[];

	public Ladder(int _nRungs) {
		rungCapacity = new int[_nRungs];
		// capacity 1 available on each rung
		for (int i=0; i<_nRungs; i++)
			rungCapacity[i] = 1;
	}
	public int nRungs() {
		return rungCapacity.length;
	}
	// return True if you succeed in grabbing the rung
	public boolean grabRung(int which) {
		if (rungCapacity[which] < 1) {
			return false;
		} else {
			rungCapacity[which]--;
			return true;
		}
	}
	public void releaseRung(int which) {
		rungCapacity[which]++;
	}
}


