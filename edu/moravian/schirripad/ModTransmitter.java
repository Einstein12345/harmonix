package edu.moravian.schirripad;

import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

public class ModTransmitter implements Transmitter {
	private Receiver r;

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public Receiver getReceiver() {
		// TODO Auto-generated method stub
		return r;
	}

	@Override
	public void setReceiver(Receiver r) {
		// TODO Auto-generated method stub
		this.r = r;
	}

	public void sendMessage(ShortMessage sm) {
		r.send(sm, -1);
	}

}
