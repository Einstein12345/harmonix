package edu.moravian.schirripad;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.Timer;

import edu.moravian.schirripad.chords.Triad;

//TODO Allow each SequencedPlayback object to have its own special VST 
// reciever, so as to allow the sequenced playback to be played using 
// different instrument that the main melody
public class SequencedPlayback {
	private SequencedList events;
	private Iterator<SequencedEvent> evIter = null;
	private Receiver rec;
	private Timer timer;
	private int delay;
	private LinkedList<ActionListener> recs = new LinkedList<>();

	public SequencedPlayback(int delay, Receiver rec) {
		setDelay(delay);
		this.rec = rec;
		init();
	}

	public SequencedPlayback(int delay, Receiver rec, SequencedList events) {
		this.events = events;
		setDelay(delay);
		this.rec = rec;
		init();
	}

	private void init() {
		timer = new Ticker(10, new EventSequencer(), this);
		timer.start();
	}

	public void addTickListener(ActionListener a) {
		timer.addActionListener(a);
		recs.add(a);
	}

	public void removeTickListener(ActionListener e) {
		timer.removeActionListener(e);
		recs.remove(e);
	}

	public void setDelay(int delay) {
		if (delay <= 0)
			delay = 100;
		this.delay = delay;
	}

	public int getDelay() {
		return delay;
	}

	public void addSequencedEvent(SequencedEvent se) {
		events.add(se);
	}

	public SequencedList fillChords(LinkedList<SequencedEvent> ev, int key, ScaleTypes scaleType, int voices,
			int maxInterval) throws InvalidMidiDataException {
		// TODO Create chordal passages using voice leading/partwriting rules.
		if (voices <= 1)
			return (SequencedList) ev;
		if (maxInterval < ModReciever.OCTAVE)
			maxInterval = ModReciever.OCTAVE;
		SequencedList nuevo = new SequencedList(voices);
		int lroot = -1, lthird = -1, lfifth = -1;
		int[] lvoices = new int[voices];
		int[] nvoices = new int[voices];
		for (SequencedEvent e : ev) {
			ShortMessage cur = e.getMessage();
			Triad t = ModReciever.harmonizeDiatonically(key, cur.getData1(), scaleType);
			int root = t.getRoot();
			int third = t.getThird();
			int fifth = t.getFifth();

			if (lroot == -1 && lthird == -1 && lfifth == -1) {
				// If this is the first chord, simply populate the voices
				int oct = 0;
				for (int i = 5; i < voices + 5; i++) {
					if (i % 3 == 0) {
						oct += 12;
					}
					if (i % 2 == 0)
						nvoices[i - 5] = third + oct;
					else if (i % 3 == 0)
						nvoices[i - 5] = fifth + oct;
					else
						nvoices[i - 5] = root + oct;
				}
			} else {
				// Place voices in correct positions based on note deltas
				int oct = 0;
				for (int i = 0; i < voices; i++) {
					// Get the relative note numbers
					int lnote = (lvoices[i] % 12);
					int aroot = root % 12;
					int athird = third % 12;
					int afifth = fifth % 12;
					// Calculate note deltas
					int droot = -1, dthird = -1, dfifth = -1;
					droot = Math.abs(lnote - aroot);
					dthird = Math.abs(lnote - athird);
					dfifth = Math.abs(lnote - afifth);
					// Compare deltas, choose smallest
					if (droot < dthird && droot < dfifth) {
						// Assign voice position to root
						nvoices[i] = root + oct;
					} else if (dthird < droot && dthird < dfifth) {
						// Assign voice position to third
						nvoices[i] = third + oct;
					} else if (dfifth < droot && dfifth < dthird) {
						// Assign voice position to fifth
						nvoices[i] = fifth + oct;
					}
					// Increase octave by one every 3rd voice
					if (i % 3 == 0)
						oct += 12;
				}

			}
			for (int i = 0; i < nvoices.length; i++) {
				// Set the old voice to be what the new one is
				lvoices[i] = nvoices[i];
				// Create and assign a new ShortMessage
				ShortMessage next = new ShortMessage();
				next.setMessage(cur.getCommand(), cur.getChannel(), nvoices[i], cur.getData2());
				// Add this message as a sequencedevent
				nuevo.add(new SequencedEvent(next, e.getLength()));
				// Clean the new voices array
				nvoices[i] = -1;
			}
			// Assign the old triad with the new triads values
			lthird = third;
			lfifth = fifth;
			lroot = root;
		}
		return nuevo;
	}

	private class SequencedList extends LinkedList<SequencedEvent> {
		private int voices = 1;

		public SequencedList(int voices) {
			if (voices != 0)
				this.voices = voices;
		}

		public int getVoices() {
			return voices;
		}
	}

	// Reads from list and fires events based on length defined in SequencedEvent
	private class EventSequencer implements ActionListener {
		private long delaySinceLast = 0;
		private int delayTilNext = 0;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (delaySinceLast >= delayTilNext) {
				if (evIter == null || !evIter.hasNext()) {
					evIter = events.iterator();
				}

				int voices = events.getVoices();
				int len = -1;
				for (int i = 0; i < voices; i++) {
					SequencedEvent seq = evIter.next();
					if (seq.getMessage().getCommand() == ShortMessage.NOTE_ON && len == -1) {
						len = seq.getLength();
					}
					rec.send(seq.getMessage(), -1);
				}
				delaySinceLast = 0;
				// Only check length if its a NOTE_ON event, otherwise do not reset
				// delayTilNext, so the next event fires immediately after.
				if (len != -1) {
					delayTilNext = len;
				}
			}
			delaySinceLast += 10;
		}

	}

	public class Ticker extends Timer {
		private SequencedPlayback seq;

		public Ticker(int arg0, ActionListener arg1, SequencedPlayback seq) {
			super(arg0, arg1);
			this.seq = seq;
		}

		public SequencedPlayback getSequencer() {
			return seq;
		}

	}

	public static class SequencedEvent {
		private int length;
		private ShortMessage sm;

		public SequencedEvent(ShortMessage sm, int lengthMs) {
			this.sm = sm;
			if (lengthMs <= 0)
				lengthMs = 1000;
			this.length = lengthMs;
		}

		public int getLength() {
			return length;
		}

		public ShortMessage getMessage() {
			return sm;
		}
	}

}
