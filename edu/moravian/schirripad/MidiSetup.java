package edu.moravian.schirripad;

import java.io.File;
import java.util.Scanner;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

public class MidiSetup {

	private Transmitter t;

	public void init() throws MidiUnavailableException {

		// Load config for midi devices
		File conf = new File("~/.harmonix");

		MidiDevice.Info[] devs = MidiSystem.getMidiDeviceInfo();
		int i = 0;
		for (MidiDevice.Info info : devs) {
			i++;
			System.out.println(i + ": " + info.getName() + ":" + info.getVendor());
		}
		Scanner sc = new Scanner(System.in);
		System.out.println("Which device? (Input)");
		int devIt = sc.nextInt();
		System.out.println("Which device? (Output)");
		int recIt = sc.nextInt();
		MidiDevice md = MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[devIt - 1]);
		md.open();

		System.out.println(md.getDeviceInfo().getName());

		try {
			t = md.getTransmitter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sc.close();
		ModReciever mr = new ModReciever(MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[recIt - 1]));

		ControlFrame cf = new ControlFrame(mr);

		t.setReceiver(mr);

	}

}
