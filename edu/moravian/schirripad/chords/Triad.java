package edu.moravian.schirripad.chords;

public abstract class Triad {
	private int root, playedRoot;

	public Triad(int root) {
		this.root = root;
		this.playedRoot = root;
	}

	public abstract int getThird();

	public abstract int getFifth();

	public int getRoot() {
		return root;
	}

	public int getAlteredRoot() {
		return playedRoot;
	}

	public void alterRoot(int alteredRoot) {
		this.playedRoot = alteredRoot;
	}

}
