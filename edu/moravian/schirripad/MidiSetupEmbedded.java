package edu.moravian.schirripad;

import java.io.IOException;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

public class MidiSetupEmbedded {

	public void init() throws MidiUnavailableException, IOException {
		// TODO Specify midi output
		// Define JUCE as midi device output
		ModReciever mr = new ModReciever(MidiSystem.getMidiDevice(null));

		MidiDevice.Info[] devs = MidiSystem.getMidiDeviceInfo();
		Transmitter[] ts = new Transmitter[devs.length];
		for (int i = 0; i < ts.length; i++) {
			MidiDevice md = MidiSystem.getMidiDevice(devs[i]);
			md.open();
			try {
				ts[i] = md.getTransmitter();
				ts[i].setReceiver(mr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ControlFrame cf = new ControlFrame(mr);
	}

}
