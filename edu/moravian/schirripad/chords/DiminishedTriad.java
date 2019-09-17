package edu.moravian.schirripad.chords;

import edu.moravian.schirripad.ModReciever;

public class DiminishedTriad extends Triad {

	public DiminishedTriad(int root) {
		super(root);
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
