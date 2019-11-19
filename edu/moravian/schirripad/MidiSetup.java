package edu.moravian.schirripad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

public class MidiSetup {

	private Transmitter t;
	private Hashtable<String, String> configuration;

	public void init() throws MidiUnavailableException, IOException {

		// Load config for midi devices
		File home = new File(System.getProperty("user.home"));
		File conf = new File(home, ".harmonix");
		if (!conf.exists()) {
			System.out.println("Configuration not found, creating...");
			if (!conf.createNewFile()) {
				System.err.println("Failed to create file");
			}
			;
		}
		configuration = new Hashtable<String, String>();
		Scanner confSc = new Scanner(new FileInputStream(conf));
		while (confSc.hasNextLine()) {
			String in = confSc.nextLine();
			String[] split = in.split(":");
			if (split.length != 2) {
				System.out.println("Invalid configuration line, " + in);
				System.out.println("Should be <key>:<value>");
				continue;
			}
			configuration.put(split[0], split[1]);
		}
		confSc.close();
		MidiDevice.Info inDev = null, outDev = null;
		MidiDevice.Info[] devs = MidiSystem.getMidiDeviceInfo();
		String inDevName = "", outDevName = "";
		if (configuration.containsKey("outdevicename")) {
			outDevName = configuration.get("outdevicename");
		}
		if (configuration.containsKey("indevicename")) {
			inDevName = configuration.get("indevicename");
		}
		for (MidiDevice.Info info : devs) {
			if (info.getName().equals(inDevName)) {
				inDev = info;
				continue;
			}
			if (info.getName().equals(outDevName)) {
				outDev = info;
				continue;
			}
		}

		if (inDev == null || outDev == null) {
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
			sc.close();
			inDev = devs[devIt - 1];
		}
		MidiDevice md = MidiSystem.getMidiDevice(inDev);
		md.open();

		System.out.println(md.getDeviceInfo().getName());

		try {
			t = md.getTransmitter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ModReciever mr = new ModReciever(MidiSystem.getMidiDevice(outDev));

		ControlFrame cf = new ControlFrame(mr);

		t.setReceiver(mr);

	}

}
