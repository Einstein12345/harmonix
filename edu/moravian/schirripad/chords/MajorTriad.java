package edu.moravian.schirripad.chords;

import edu.moravian.schirripad.ModReciever;

public class MajorTriad extends Triad {

	public MajorTriad(int root) {
		super(root);
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
