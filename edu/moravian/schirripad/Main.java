package edu.moravian.schirripad;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class Main {
	
	public static void main(String[] args) throws Exception{
		JPanel p = new JPanel();
		p.setVisible(false);
		MidiSetup ms = new MidiSetup();
		ms.init();
	}
	
}
