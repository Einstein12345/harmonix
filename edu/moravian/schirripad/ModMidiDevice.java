package edu.moravian.schirripad;

import java.util.Arrays;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import com.synthbot.audioplugin.vst.vst2.JVstHost2;

public class ModMidiDevice implements MidiDevice {
	MidiDevice md = null;
	JVstHost2 vst = null;
	VSTReceiver rec = null;

	public ModMidiDevice(MidiDevice md, JVstHost2 vst) throws MidiUnavailableException {
		if (md == null && vst == null) {
			throw new MidiUnavailableException("Passed null MidiDevice and VST Host! One must be valid!");
		}
		this.md = md;
		this.vst = vst;
		if (vst != null)
			rec = new VSTReceiver(vst);
	}

	@Override
	public void close() {
		if (md != null)
			md.close();
	}

	@Override
	public Info getDeviceInfo() {
		if (md != null)
			return md.getDeviceInfo();
		return null;
	}

	@Override
	public int getMaxReceivers() {
		if (md != null)
			return md.getMaxReceivers();
		return 0;
	}

	@Override
	public int getMaxTransmitters() {
		if (md != null)
			return md.getMaxTransmitters();
		return 0;
	}

	@Override
	public long getMicrosecondPosition() {
		if (md != null)
			return md.getMicrosecondPosition();
		return 0;
	}

	@Override
	public Receiver getReceiver() throws MidiUnavailableException {
		if (md != null)
			return md.getReceiver();
		return rec;
	}

	@Override
	public List<Receiver> getReceivers() {
		if (md != null) {
			return md.getReceivers();
		}
		return Arrays.asList(rec);
	}

	@Override
	public Transmitter getTransmitter() throws MidiUnavailableException {
		if (md != null)
			return md.getTransmitter();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Transmitter> getTransmitters() {
		if (md != null) {
			return md.getTransmitters();
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen() {
		if (md != null)
			return md.isOpen();
		return true;
	}

	@Override
	public void open() throws MidiUnavailableException {
		if (md != null)
			md.open();
	}

	private class VSTReceiver implements Receiver {
		private JVstHost2 vst;

		public VSTReceiver(JVstHost2 vst) throws MidiUnavailableException {
			if (vst == null) {
				throw new MidiUnavailableException();
			}
			this.vst = vst;
		}

		@Override
		public void close() {
		}

		@Override
		public void send(MidiMessage arg0, long arg1) {
			vst.queueMidiMessage((ShortMessage) arg0);
		}

	}

}
