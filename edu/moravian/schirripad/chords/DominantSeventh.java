package edu.moravian.schirripad.chords;

import edu.moravian.schirripad.ModReciever;

public class DominantSeventh extends SeventhChord {

	public DominantSeventh(int root) {
		super(root);
	}

	@Override
	public int getSeventh() {
		return getRoot() + ModReciever.MINOR_SEVENTH;
	}

	@Override
	public int getThird() {
		return getRoot() + ModReciever.MAJOR_THIRD;
	}

	@Override
	public int getFifth() {
		return getRoot() + ModReciever.PERFECT_FIFTH;
	}

}
