package edu.moravian.schirripad.chords;

import edu.moravian.schirripad.ModReciever;

public class HalfDiminishedSeventh extends SeventhChord{

	public HalfDiminishedSeventh(int root) {
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
		return getRoot() + ModReciever.DIMINISHED_FIFTH;
	}

}
