package edu.moravian.schirripad.chords;

import edu.moravian.schirripad.ModReciever;

public class MinorTriad extends Triad {

	public MinorTriad(int root) {
		super(root);
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
