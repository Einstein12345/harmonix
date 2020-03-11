package edu.moravian.schirripad;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

import com.synthbot.audioplugin.vst.vst2.JVstHost2;

import edu.moravian.schirripad.rpi.ControlBoard;

public class MidiSetupEmbedded {

	public void init() throws MidiUnavailableException, IOException {
		// TODO Specify midi output
		// Get the synthesizer file location
		File amsynth = new File("/usr/local/lib/amsynth_vst.so");
		ModReciever mr = null;
		JVstHost2 vst = null;
		// If the synth is installed, load it, otherwise send it directly to MIDI out
		if (amsynth.exists()) {
			System.out.println("Found synth.so, attempting to generate VST host...");
			try {
				vst = JVstHost2.newInstance(amsynth);
				mr = new ModReciever(new ModMidiDevice(null, vst));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Failed to find synth.so");
			mr = new ModReciever(null, vst);
		}
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

		new ControlBoard(mr);

	}

}
