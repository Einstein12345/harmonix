package edu.moravian.schirripad.chords;

import edu.moravian.schirripad.ModReciever;

public class MajorSeventh extends SeventhChord {

	public MajorSeventh(int root) {
		super(root);
	}

	@Override
	public int getSeventh() {
		return getRoot() + ModReciever.MAJOR_SEVENTH;
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
