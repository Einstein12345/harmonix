package edu.moravian.schirripad.chords;

import edu.moravian.schirripad.ModReciever;

public class AugmentedTriad extends Triad {

	public AugmentedTriad(int root) {
		super(root);
	}

	@Override
	public int getThird() {
		// TODO Auto-generated method stub
		return getRoot() + ModReciever.MAJOR_THIRD;
	}

	@Override
	public int getFifth() {
		// TODO Auto-generated method stub
		return getRoot() + ModReciever.AUGMENTED_FIFTH;
	}

}
