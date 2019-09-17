package edu.moravian.schirripad.chords;

import edu.moravian.schirripad.ModReciever;

public class MinorSeventh extends SeventhChord{

	public MinorSeventh(int root) {
		super(root);
	}

	@Override
	public int getSeventh() {
		return getRoot() + ModReciever.MINOR_SEVENTH;
	}

	@Override
	public int getThird() {
		return getRoot() + ModReciever.MINOR_THIRD;
	}

	@Override
	public int getFifth() {
		return getRoot() + ModReciever.PERFECT_FIFTH;
	}

}
