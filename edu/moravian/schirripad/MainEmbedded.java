package edu.moravian.schirripad;

import javax.swing.JPanel;

public class MainEmbedded {

	public static void main(String[] args) throws Exception {
		JPanel p = new JPanel();
		p.setVisible(false);
		MidiSetupEmbedded ms = new MidiSetupEmbedded();
		ms.init();
	}

}
