package edu.moravian.schirripad;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import com.synthbot.audioplugin.vst.vst2.JVstHost2;

import edu.moravian.schirripad.SequencedPlayback.SequencedEvent;
import edu.moravian.schirripad.chords.AugmentedTriad;
import edu.moravian.schirripad.chords.DiminishedTriad;
import edu.moravian.schirripad.chords.MajorTriad;
import edu.moravian.schirripad.chords.MinorTriad;
import edu.moravian.schirripad.chords.Triad;

public class ModReciever implements Receiver, ActionListener {
	private boolean doHarmonize = true;
	private boolean doMelodicallyInvert = false;

	private boolean doHarmonizeChord = false;
	private boolean doHarmonizeChordDiatonically = true;
	private int chordalType = MAJOR_TRIAD;
	private int tonic = C;
	private ScaleTypes scaleType = ScaleTypes.MAJOR;

	private HashSet<Integer> harmIntervals = new HashSet<>();
	private ShortMessage prior, lastCreated, current, lastSequenced;

	private int ticksSinceLastSeq = 0;
	private boolean isRecording;
	private MidiDevice prox;
	private JVstHost2 vst = null;

	public static final int HALF_STEP = 1, WHOLE_STEP = 2, MINOR_SECOND = 1, MAJOR_SECOND = 2, DIMINISHED_THIRD = 2,
			MINOR_THIRD = 3, MAJOR_THIRD = 4, DIMINISHED_FOURTH = 4, PERFECT_FOURTH = 5, AUGMENTED_FOURTH = 6,
			TRITONE = 6, DIMINISHED_FIFTH = 6, PERFECT_FIFTH = 7, AUGMENTED_FIFTH = 8, DIMINISHED_SIXTH = 7,
			MINOR_SIXTH = 8, MAJOR_SIXTH = 9, AUGMENTED_SIXTH = 10, DIMINISHED_SEVENTH = 9, MINOR_SEVENTH = 10,
			MAJOR_SEVENTH = 11, AUGMENTED_SEVENTH = 12, OCTAVE = 12, NOTE_PRESSED = 144, NOTE_RELEASED = 128,
			MAJOR_TRIAD = 13, MINOR_TRIAD = 14, DIMINISHED_TRIAD = 15, AUGMENTED_TRIAD = 16, MAJOR_SEVENTH_CHORD = 17,
			MINOR_SEVENTH_CHORD = 18, DOM_SEVENTH_CHORD = 19, C = 0, CS = 1, Db = 1, D = 2, DS = 3, Eb = 3, E = 4,
			ES = 5, F = 5, Fb = 4, FS = 6, Gb = 6, G = 7, GS = 8, Ab = 8, A = 9, AS = 10, Bb = 10, B = 11;

	public static final int[] MAJOR_STEPS = { WHOLE_STEP, WHOLE_STEP, HALF_STEP, WHOLE_STEP, WHOLE_STEP, WHOLE_STEP,
			HALF_STEP },
			MINOR_STEPS = { WHOLE_STEP, HALF_STEP, WHOLE_STEP, WHOLE_STEP, HALF_STEP, WHOLE_STEP, WHOLE_STEP };

	public ModReciever(MidiDevice prox) {
		this.prox = prox;
		try {
			prox.open();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ModReciever(MidiDevice prox, JVstHost2 vst) {
		this.prox = prox;
		try {
			prox.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (vst.isSynth())
			this.vst = vst;
	}

	@Override
	public void close() {
		prox.close();
	}

	@Override
	public void send(MidiMessage mm, long t) {
		try {
			prox.getReceiver().send(mm, t);
		} catch (MidiUnavailableException e1) {
			e1.printStackTrace();
		}
		if (mm instanceof ShortMessage) {

			ShortMessage sm = (ShortMessage) mm;

			int cmd = sm.getCommand();
			int key = sm.getData1();
			// int oct = (key / 12) - 1;
			// int note = key % 12;
			int vel = sm.getData2();

			current = sm;

			// System.out.println(cmd);

			if (doHarmonize && key <= 120) {
				ShortMessage nm = new ShortMessage();
				try {
					if (harmIntervals.size() > 0) {
						Iterator<Integer> it = harmIntervals.iterator();
						while (it.hasNext()) {
							nm.setMessage(cmd, sm.getChannel(), key + it.next(), vel);
							prox.getReceiver().send(nm, -1);
						}
					}
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (doHarmonizeChord && key <= 120) {
				Triad mt = new MajorTriad(C);
				if (doHarmonizeChordDiatonically) {
					mt = harmonizeDiatonically(tonic, key, scaleType);
					try {
						ShortMessage cm1 = new ShortMessage();
						ShortMessage cm2 = new ShortMessage();
						cm1.setMessage(cmd, sm.getChannel(), alterNoteToRange(mt.getThird()), vel);
						cm2.setMessage(cmd, sm.getChannel(), alterNoteToRange(mt.getFifth()), vel);

						prox.getReceiver().send(cm1, -1);
						prox.getReceiver().send(cm2, -1);
					} catch (InvalidMidiDataException | MidiUnavailableException e) {
						e.printStackTrace();
					}
				} else if (key <= 120) {
					try {
						ShortMessage cm1 = null;
						ShortMessage cm2 = null;
						ShortMessage cm3 = null;
						switch (chordalType) {
						case MAJOR_TRIAD: {
							cm1 = new ShortMessage();
							cm2 = new ShortMessage();
							cm1.setMessage(cmd, sm.getChannel(), key + MAJOR_THIRD, vel);
							cm2.setMessage(cmd, sm.getChannel(), key + PERFECT_FIFTH, vel);
							break;
						}
						case MINOR_TRIAD: {
							cm1 = new ShortMessage();
							cm2 = new ShortMessage();
							cm1.setMessage(cmd, sm.getChannel(), key + MINOR_THIRD);
							cm2.setMessage(cmd, sm.getChannel(), key + PERFECT_FIFTH);
							break;
						}
						case DIMINISHED_TRIAD: {
							cm1 = new ShortMessage();
							cm2 = new ShortMessage();
							cm1.setMessage(cmd, sm.getChannel(), key + MINOR_THIRD, vel);
							cm2.setMessage(cmd, sm.getChannel(), key + DIMINISHED_FIFTH);
							break;
						}
						case AUGMENTED_TRIAD: {
							cm1 = new ShortMessage();
							cm2 = new ShortMessage();
							cm1.setMessage(cmd, sm.getChannel(), key + MAJOR_THIRD, vel);
							cm2.setMessage(cmd, sm.getChannel(), key + AUGMENTED_FIFTH);
							break;
						}
						case MAJOR_SEVENTH_CHORD: {
							cm1 = new ShortMessage();
							cm2 = new ShortMessage();
							cm3 = new ShortMessage();
							cm1.setMessage(cmd, sm.getChannel(), key + MAJOR_THIRD, vel);
							cm2.setMessage(cmd, sm.getChannel(), key + PERFECT_FIFTH, vel);
							cm3.setMessage(cmd, sm.getChannel(), key + MAJOR_SEVENTH, vel);
							break;
						}
						case MINOR_SEVENTH_CHORD: {
							cm1 = new ShortMessage();
							cm2 = new ShortMessage();
							cm3 = new ShortMessage();
							cm1.setMessage(cmd, sm.getChannel(), key + MINOR_THIRD, vel);
							cm2.setMessage(cmd, sm.getChannel(), key + PERFECT_FIFTH, vel);
							cm3.setMessage(cmd, sm.getChannel(), key + MINOR_SEVENTH, vel);
							break;
						}
						case DOM_SEVENTH_CHORD: {
							cm1 = new ShortMessage();
							cm2 = new ShortMessage();
							cm3 = new ShortMessage();
							cm1.setMessage(cmd, sm.getChannel(), key + MAJOR_THIRD, vel);
							cm2.setMessage(cmd, sm.getChannel(), key + PERFECT_FIFTH, vel);
							cm3.setMessage(cmd, sm.getChannel(), key + MINOR_SEVENTH, vel);
							break;
						}
						}
						if (cm1 != null)
							prox.getReceiver().send(cm1, -1);
						if (cm2 != null)
							prox.getReceiver().send(cm2, -1);
						if (cm3 != null)
							prox.getReceiver().send(cm3, -1);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			try {
				prox.getReceiver().send((MidiMessage) sm, -1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (doMelodicallyInvert && !(prior == null)) {
				ShortMessage nm = new ShortMessage();
				try {
					if (lastCreated != null) {
						nm.setMessage(ShortMessage.NOTE_OFF, lastCreated.getData1(), vel);
						prox.getReceiver().send(nm, -1);
					} else {
						lastCreated = prior;
					}
					int invInt = (prior.getData1() - sm.getData1());
					if (invInt > 12)
						invInt = invInt - 12;
					if (invInt < -12)
						invInt = invInt + 12;
					if (lastCreated.getData1() + invInt > 127)
						nm.setMessage(cmd, sm.getChannel(), lastCreated.getData1() + invInt - 12, vel);
					else
						nm.setMessage(cmd, sm.getChannel(), lastCreated.getData1() + invInt, vel);
					prox.getReceiver().send(nm, -1);
					lastCreated = nm;
				} catch (InvalidMidiDataException | MidiUnavailableException e) {
					e.printStackTrace();
				}
			} else if (!doMelodicallyInvert && !(prior == null))
				lastCreated = null;
			prior = sm;
		}
	}

	public static boolean isNoteInKey(int tonic, int key, ScaleTypes scaleType) {
		int note = (key % 12);
		if (note == 0)
			note += 12;
		switch (scaleType) {
		case MAJOR:
			if ((note == tonic) || (note == tonic + MAJOR_SECOND) || (note == tonic + MAJOR_THIRD)
					|| (note == tonic + PERFECT_FOURTH) || (note == tonic + PERFECT_FIFTH)
					|| (note == tonic + MAJOR_SIXTH) || (note == tonic + MAJOR_SEVENTH))
				return true;
			break;
		case MINOR:
			if ((note == tonic) || (note == tonic + MAJOR_SECOND) || (note == tonic + MINOR_THIRD)
					|| (note == tonic + PERFECT_FOURTH) || (note == tonic + PERFECT_FIFTH)
					|| (note == tonic + MINOR_SIXTH) || (note == tonic + MINOR_SEVENTH))
				return true;
			break;
		case HARMONIC_MINOR:
			if ((note == tonic) || (note == tonic + MAJOR_SECOND) || (note == tonic + MINOR_THIRD)
					|| (note == tonic + PERFECT_FOURTH) || (note == tonic + PERFECT_FIFTH)
					|| (note == tonic + MINOR_SIXTH) || (note == tonic + MAJOR_SEVENTH))
				return true;
			break;
		case DORIAN:
			if ((note == tonic) || (note == tonic + MAJOR_SECOND) || (note == tonic + MINOR_THIRD)
					|| (note == tonic + PERFECT_FOURTH) || (note == tonic + PERFECT_FIFTH)
					|| (note == tonic + MAJOR_SIXTH) || (note == tonic + MINOR_SEVENTH))
				return true;
			break;
		}
		return false;
	}

	public static boolean isNoteInKeyAbs(int tonic, int note, ScaleTypes scaleType) {
		switch (scaleType) {
		default: // Default = Major
		case MAJOR:
			if ((note == tonic) || (note == tonic + MAJOR_SECOND) || (note == tonic + MAJOR_THIRD)
					|| (note == tonic + PERFECT_FOURTH) || (note == tonic + PERFECT_FIFTH)
					|| (note == tonic + MAJOR_SIXTH) || (note == tonic + MAJOR_SEVENTH))
				return true;
			break;
		case MELODIC_MINOR_DESCENDING:
		case MINOR:
			if ((note == tonic) || (note == tonic + MAJOR_SECOND) || (note == tonic + MINOR_THIRD)
					|| (note == tonic + PERFECT_FOURTH) || (note == tonic + PERFECT_FIFTH)
					|| (note == tonic + MINOR_SIXTH) || (note == tonic + MINOR_SEVENTH))
				return true;
			break;
		case HARMONIC_MINOR:
			if ((note == tonic) || (note == tonic + MAJOR_SECOND) || (note == tonic + MINOR_THIRD)
					|| (note == tonic + PERFECT_FOURTH) || (note == tonic + PERFECT_FIFTH)
					|| (note == tonic + MINOR_SIXTH) || (note == tonic + MAJOR_SEVENTH))
				return true;
			break;
		case DORIAN:
			if ((note == tonic) || (note == tonic + MAJOR_SECOND) || (note == tonic + MINOR_THIRD)
					|| (note == tonic + PERFECT_FOURTH) || (note == tonic + PERFECT_FIFTH)
					|| (note == tonic + MAJOR_SIXTH) || (note == tonic + MINOR_SEVENTH))
				return true;
			break;
		case MELODIC_MINOR_ASCENDING:
			if ((note == tonic) || (note == tonic + MAJOR_SECOND) || (note == tonic + MINOR_THIRD)
					|| (note == tonic + PERFECT_FOURTH) || (note == tonic + PERFECT_FIFTH)
					|| (note == tonic + MAJOR_SIXTH) || (note == tonic + MAJOR_SEVENTH))
				return true;
			break;
		case LYDIAN:
			if ((note == tonic) || (note == tonic + MAJOR_SECOND) || (note == tonic + MAJOR_THIRD)
					|| (note == tonic + AUGMENTED_FOURTH) || (note == tonic + PERFECT_FIFTH)
					|| (note == tonic + MAJOR_SIXTH) || (note == tonic + MAJOR_SEVENTH))
				return true;
			break;
		}
		return false;
	}

	public static Triad harmonizeDiatonically(int tonic, int key, ScaleTypes scaleType) {
		int chordRoot = (key % 12) - tonic;
		int rootAlteration = 0;

		while (chordRoot < 0) {
			chordRoot = chordRoot + 12;
		}
		if (!(isNoteInKeyAbs(tonic, tonic + chordRoot, scaleType))) {
			// Get the next closest note that is in the key, and use that as the chordal
			// root when forming the harmonies, however use the chromatic root when actually
			// playing the note

			int oct = (key / 12) - 1;
			int note = key % 12;

			int newNote = note;
			while (!isNoteInKeyAbs(tonic, newNote, scaleType)) {
				newNote += ModReciever.HALF_STEP;
			}
			rootAlteration = newNote - note;
			key = note * ((oct + 1) * 12);

		}
		Triad mt = new MajorTriad(0);
		switch (scaleType) {
		case MAJOR: {
			if (chordRoot == 0 || chordRoot == PERFECT_FOURTH || chordRoot == PERFECT_FIFTH) {
				mt = new MajorTriad(key);
			} else if (chordRoot == MAJOR_SECOND || chordRoot == MAJOR_THIRD || chordRoot == MAJOR_SIXTH) {
				mt = new MinorTriad(key);
			} else {
				mt = new DiminishedTriad(key);
			}
			break;
		}
		case MINOR: {
			if (chordRoot == 0 || chordRoot == PERFECT_FOURTH || chordRoot == PERFECT_FIFTH) {
				mt = new MinorTriad(key);
			} else if (chordRoot == MINOR_THIRD || chordRoot == MINOR_SIXTH || chordRoot == MINOR_SEVENTH) {
				mt = new MajorTriad(key);
			} else {
				mt = new DiminishedTriad(key);
			}
			break;
		}
		case HARMONIC_MINOR: {
			if (chordRoot == 0 || chordRoot == PERFECT_FOURTH) {
				mt = new MinorTriad(key);
			} else if (chordRoot == PERFECT_FIFTH || chordRoot == MINOR_SIXTH) {
				mt = new MajorTriad(key);
			} else if (chordRoot == MAJOR_SECOND || chordRoot == MAJOR_SEVENTH) {
				mt = new DiminishedTriad(key);
			} else {
				mt = new AugmentedTriad(key);
			}
			break;
		}
		case MELODIC_MINOR_ASCENDING: {
			if (chordRoot == 0 || chordRoot == MAJOR_SIXTH) {
				mt = new MinorTriad(key);
			} else if (chordRoot == PERFECT_FOURTH || chordRoot == PERFECT_FIFTH) {
				mt = new MajorTriad(key);
			} else if (chordRoot == MINOR_THIRD) {
				mt = new AugmentedTriad(key);
			} else {
				mt = new DiminishedTriad(key);
			}
			break;
		}
		case MELODIC_MINOR_DESCENDING: {
			if (chordRoot == 0 || chordRoot == PERFECT_FOURTH || chordRoot == PERFECT_FIFTH) {
				mt = new MinorTriad(key);
			} else if (chordRoot == MINOR_THIRD || chordRoot == MINOR_SIXTH || chordRoot == MINOR_SEVENTH) {
				mt = new MajorTriad(key);
			} else {
				mt = new DiminishedTriad(key);
			}
			break;
		}
		case DIMINISHED: {
			break;
		}
		case DORIAN: {
			if (chordRoot == 0 || chordRoot == MAJOR_SECOND || chordRoot == PERFECT_FIFTH) {
				mt = new MinorTriad(key);
			} else if (chordRoot == MINOR_THIRD || chordRoot == PERFECT_FOURTH || chordRoot == MINOR_SEVENTH) {
				mt = new MajorTriad(key);
			} else {
				mt = new DiminishedTriad(key);
			}
			break;
		}
		case LYDIAN: {
			if (chordRoot == 0 || chordRoot == MAJOR_SECOND || chordRoot == PERFECT_FIFTH) {
				mt = new MajorTriad(key);
			} else if (chordRoot == MAJOR_THIRD || chordRoot == MAJOR_SIXTH || chordRoot == MAJOR_SEVENTH) {
				mt = new MinorTriad(key);
			} else if (chordRoot == AUGMENTED_FOURTH) {
				mt = new DiminishedTriad(key);
			}
			break;
		}
		case MIXOLYDIAN: {
			break;
		}
		case PHYRGIAN: {
			break;
		}
		case LOCRIAN: {
			break;
		}
		}
		mt.alterRoot(mt.getRoot() + rootAlteration);
		return mt;
	}

	public int alterNoteToRange(int note) {
		while (note > 120) {
			note = note - 12;
		}
		return note;
	}

	public void doHarmonize(boolean harm) {
		this.doHarmonize = harm;
	}

	public boolean isHarmonized() {
		return this.doHarmonize;
	}

	public void doHarmonizeChord(boolean harm) {
		this.doHarmonizeChord = harm;
	}

	public boolean isChordHarmonized() {
		return doHarmonizeChord;
	}

	public void doHarmonizeDiatonically(boolean doHarm) {
		this.doHarmonizeChordDiatonically = doHarm;
	}

	public boolean isHarmonizedDiatonically() {
		return doHarmonizeChordDiatonically;
	}

	public void doMelodicallyInvert(boolean invert) {
		this.doMelodicallyInvert = invert;
	}

	public boolean isMelodicallyInverted() {
		return doMelodicallyInvert;
	}

	public boolean addHarmonicInterval(int hsteps) {
		if (hsteps <= 0) {
			return false;
		}
		if (hsteps > 12) {
			hsteps = hsteps - 12;
		}
		if (harmIntervals.contains(hsteps))
			return false;
		harmIntervals.add(hsteps);
		return true;
	}

	public void removeHarmonicInterval(int hsteps) {
		if (hsteps <= 0)
			return;
		if (hsteps > 12)
			hsteps = hsteps - 12;
		if (harmIntervals.contains(hsteps))
			harmIntervals.remove(hsteps);
	}

	public void setTonic(int tonic) {
		while (tonic > 12) {
			tonic = tonic - 12;
		}
		while (tonic < 0) {
			tonic = tonic + 12;
		}
		this.tonic = tonic;
	}

	public void setScaleType(ScaleTypes scale) {
		this.scaleType = scale;
	}

	public void setHarmonizationInterval(int inter) {
		while (inter > 12) {
			inter = inter - 12;
		}
		while (inter < 0) {
			inter = inter + 12;
		}

	}

	public void panic() {
		for (int i = 0; i < 128; i++) {
			for (int j = 0; j < 17; j++) {
				try {
					ShortMessage sm = new ShortMessage(ShortMessage.NOTE_OFF, j, i, 0);
					prox.getReceiver().send(sm, -1);
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				} catch (MidiUnavailableException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (isRecording) {
			if (e.getSource() instanceof SequencedPlayback.Ticker) {
				ticksSinceLastSeq++;
				SequencedPlayback.Ticker tik = (SequencedPlayback.Ticker) e.getSource();
				SequencedPlayback seq = tik.getSequencer();
				if (lastSequenced == null) {
					return;
				}
				if (!(lastSequenced.getChannel() == current.getChannel()
						&& lastSequenced.getCommand() == current.getCommand()
						&& lastSequenced.getData1() == current.getData1()
						&& lastSequenced.getData2() == current.getData2())) {
					seq.addSequencedEvent(new SequencedEvent(current, ticksSinceLastSeq * 10));
				}
			}
		}
	}
}
