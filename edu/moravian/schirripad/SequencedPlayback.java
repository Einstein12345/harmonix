package edu.moravian.schirripad;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.Timer;

import edu.moravian.schirripad.chords.Triad;

public class SequencedPlayback {
	private LinkedList<SequencedEvent> events = new LinkedList<SequencedEvent>();
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

	public SequencedPlayback(int delay, Receiver rec, LinkedList<SequencedEvent> events) {
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

	public LinkedList<SequencedEvent> fillChords(LinkedList<SequencedEvent> ev, int key, ScaleTypes scaleType, int voices,
			int maxInterval) throws InvalidMidiDataException {
		// TODO Create chordal passages using voice leading/partwriting rules.
		if (voices <= 1)
			return ev;
		if (maxInterval < ModReciever.OCTAVE)
			maxInterval = ModReciever.OCTAVE;
		LinkedList<SequencedEvent> nuevo = new LinkedList<SequencedEvent>();
		int lroot = -1, lthird = -1, lfifth = -1;
		int[] lvoices = new int[voices];
		int[] nvoices = new int[voices];
		for (SequencedEvent e : ev) {
			ShortMessage cur = e.getMessage();
			Triad t = ModReciever.harmonizeDiatonically(key, cur.getData1(), scaleType);
			int root = t.getRoot();
			int third = t.getThird();
			int fifth = t.getFifth();
			ShortMessage cm1 = null, cm2 = null, cm3 = null;

			if (lroot == -1 && lthird == -1 && lfifth == -1) {
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
				int[] vdelts = new int[voices];
				for (int i = 0; i < vdelts.length; i++) {
					vdelts[i] = Math.abs(nvoices[i] - lvoices[i]);
				}
				// Check which voices are the closest to the next chord member, and adjust them
				// as such, as well as dynamically populate an array with the voices required,
				// based on the set max distance apart
			}
			for (int i = 0; i < nvoices.length; i++) {
				lvoices[i] = nvoices[i];
				nvoices[i] = -1;
			}
			SequencedEvent s1 = new SequencedEvent(cm1, e.getLength());
			SequencedEvent s2 = new SequencedEvent(cm2, e.getLength());
			SequencedEvent s3 = new SequencedEvent(cm3, e.getLength());
			nuevo.add(s1);
			nuevo.add(s2);
			nuevo.add(s3);
		}
		return nuevo;
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

				SequencedEvent seq = evIter.next();
				rec.send(seq.getMessage(), -1);
				delaySinceLast = 0;
				// Only check length if its a NOTE_ON event, otherwise do not reset
				// delayTilNext, so the next event fires immediately after.
				if (seq.getMessage().getCommand() == ShortMessage.NOTE_ON)
					delayTilNext = seq.getLength();
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
