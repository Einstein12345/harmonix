package edu.moravian.schirripad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;

public class ControlFrame extends JFrame implements ActionListener {
	private ModReciever md;
	private JSlider harmIntervalSlider;
	private JList<Integer> harmIntervalsList;
	private DefaultListModel<Integer> harmIntervalListModel = new DefaultListModel<>();

	public ControlFrame(ModReciever md) {
		this.md = md;
		// Need:
		// Do Harmonize *
		// Harmonize Diatonically *
		// Melodic Inversion *
		// Set Harmonic Interval
		// Set Tonic
		// Set Scale Type
		System.out.println("Creating progress bar");
		JProgressBar prog = new JProgressBar();
		prog.setIndeterminate(true);
		prog.setVisible(true);
		setTitle("Harmonization Module");
		init();
		pack();
		prog.setVisible(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void init() {
		JPanel checks = new JPanel(); // Checkboxes go here
		JPanel setPanel = new JPanel(); // Panel to set tonic, scaletype, harmonic interval
		JPanel harmIntervalPanel = new JPanel(); // Panel to set harmonic interval size

		// Create Individual Checkbox panels, add them to Main Checks JPanel
		JPanel doHarmPanel = new JPanel();
		JCheckBox doHarm = new JCheckBox();
		doHarm.setSelected(true);
		doHarm.setActionCommand("doHarm");
		doHarm.addActionListener(this);
		JLabel doHarmLabel = new JLabel("Harmonize Input:");
		doHarmPanel.add(doHarmLabel);
		doHarmPanel.add(doHarm);

		// Create Interval Harmonization Selection Panel
		JPanel doHarmChordPanel = new JPanel();
		JCheckBox doHarmChord = new JCheckBox();
		doHarmChord.setActionCommand("doHarmChord");
		doHarmChord.addActionListener(this);
		JLabel doHarmChordLabel = new JLabel("Chordally Harmonize Input: ");
		doHarmChordPanel.add(doHarmChordLabel);
		doHarmChordPanel.add(doHarmChord);

		// Create Diatonic Harmonization Panel
		JPanel doHarmDiatonicPanel = new JPanel();
		JCheckBox doHarmDiatonic = new JCheckBox();
		doHarmDiatonic.setSelected(true);
		doHarmDiatonic.setActionCommand("doHarmDiatonic");
		doHarmDiatonic.addActionListener(this);
		JLabel doHarmDiatonicLabel = new JLabel("Harmonize Diatonically:");
		doHarmDiatonicPanel.add(doHarmDiatonicLabel);
		doHarmDiatonicPanel.add(doHarmDiatonic);

		// Create Melodic Inversion Harmonization Panel
		JPanel doMelodicInvertPanel = new JPanel();
		JCheckBox doMelodicInvert = new JCheckBox();
		doMelodicInvert.setSelected(false);
		doMelodicInvert.setActionCommand("doMelodicInvert");
		doMelodicInvert.addActionListener(this);
		JLabel doMelodicInvertLabel = new JLabel("Melodically Invert:");
		doMelodicInvertPanel.add(doMelodicInvertLabel);
		doMelodicInvertPanel.add(doMelodicInvert);

		// Add all Settings Panels to main Settings Panel
		checks.setLayout(new BoxLayout(checks, BoxLayout.PAGE_AXIS));
		// checks.add(new JLabel("Settings:"));
		checks.add(doHarmPanel);
		checks.add(doHarmChordPanel);
		checks.add(doHarmDiatonicPanel);
		checks.add(doMelodicInvertPanel);
		checks.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Settings"));

		// Create variable settings panel, add to main setPanel

		JPanel setTonicPanel = new JPanel();
		setTonicPanel.setLayout(new BoxLayout(setTonicPanel, BoxLayout.PAGE_AXIS));
		// Create List Of Tonics
		JRadioButton c = new JRadioButton("C");
		c.setActionCommand("c");
		c.addActionListener(this);
		c.setSelected(true);
		JRadioButton cS = new JRadioButton("C#/Db");
		cS.setActionCommand("cs");
		cS.addActionListener(this);
		JRadioButton d = new JRadioButton("D");
		d.setActionCommand("d");
		d.addActionListener(this);
		JRadioButton dS = new JRadioButton("D#/Eb");
		dS.setActionCommand("ds");
		dS.addActionListener(this);
		JRadioButton e = new JRadioButton("E");
		e.setActionCommand("e");
		e.addActionListener(this);
		JRadioButton f = new JRadioButton("F");
		f.setActionCommand("f");
		f.addActionListener(this);
		JRadioButton fS = new JRadioButton("F#/Gb");
		fS.setActionCommand("fs");
		fS.addActionListener(this);
		JRadioButton g = new JRadioButton("G");
		g.setActionCommand("g");
		g.addActionListener(this);
		JRadioButton gS = new JRadioButton("G#/Ab");
		gS.setActionCommand("gs");
		gS.addActionListener(this);
		JRadioButton a = new JRadioButton("A");
		a.setActionCommand("a");
		a.addActionListener(this);
		JRadioButton aS = new JRadioButton("A#/Bb");
		aS.setActionCommand("as");
		aS.addActionListener(this);
		JRadioButton b = new JRadioButton("B");
		b.setActionCommand("b");
		b.addActionListener(this);

		ButtonGroup tonics = new ButtonGroup();
		tonics.add(c);
		tonics.add(cS);
		tonics.add(d);
		tonics.add(dS);
		tonics.add(e);
		tonics.add(f);
		tonics.add(fS);
		tonics.add(g);
		tonics.add(gS);
		tonics.add(a);
		tonics.add(aS);
		tonics.add(b);

		// setTonicPanel.add(new JLabel("Set Tonic:"));
		setTonicPanel.add(c);
		setTonicPanel.add(cS);
		setTonicPanel.add(d);
		setTonicPanel.add(dS);
		setTonicPanel.add(e);
		setTonicPanel.add(f);
		setTonicPanel.add(fS);
		setTonicPanel.add(g);
		setTonicPanel.add(gS);
		setTonicPanel.add(a);
		setTonicPanel.add(aS);
		setTonicPanel.add(b);
		setTonicPanel
				.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Set Tonic"));

		JPanel setScaleTypePanel = new JPanel();
		setScaleTypePanel.setLayout(new BoxLayout(setScaleTypePanel, BoxLayout.PAGE_AXIS));

		// Create List Of Scale Types

		JRadioButton major = new JRadioButton("Major");
		major.setActionCommand("major");
		major.addActionListener(this);
		major.setSelected(true);
		JRadioButton minor = new JRadioButton("Minor");
		minor.setActionCommand("minor");
		minor.addActionListener(this);
		JRadioButton harmMinor = new JRadioButton("Harmonic Minor");
		harmMinor.setActionCommand("harmMinor");
		harmMinor.addActionListener(this);
		JRadioButton melMinorA = new JRadioButton("Melodic Minor: Ascending");
		melMinorA.setActionCommand("melMinorA");
		melMinorA.addActionListener(this);
		JRadioButton melMinorD = new JRadioButton("Melodic Minor: Descending");
		melMinorD.setActionCommand("melMinorD");
		melMinorD.addActionListener(this);
		JRadioButton dorian = new JRadioButton("Dorian");
		dorian.setActionCommand("dorian");
		dorian.addActionListener(this);
		JRadioButton lydian = new JRadioButton("Lydian");
		lydian.setActionCommand("lydian");
		lydian.addActionListener(this);
		JRadioButton phrygian = new JRadioButton("Phrygian");
		phrygian.setActionCommand("phrygian");
		phrygian.addActionListener(this);
		JRadioButton mixolydian = new JRadioButton("Mixolydian");
		mixolydian.setActionCommand("mixolydian");
		mixolydian.addActionListener(this);
		JRadioButton locrian = new JRadioButton("Locrian");
		locrian.setActionCommand("locrian");
		locrian.addActionListener(this);

		ButtonGroup scales = new ButtonGroup();
		scales.add(major);
		scales.add(minor);
		scales.add(harmMinor);
		scales.add(melMinorA);
		scales.add(melMinorD);
		scales.add(dorian);
		scales.add(lydian);
		scales.add(phrygian);
		scales.add(mixolydian);
		scales.add(locrian);

		// setScaleTypePanel.add(new JLabel("Set Scale Type: "));
		setScaleTypePanel.add(major);
		setScaleTypePanel.add(minor);
		setScaleTypePanel.add(harmMinor);
		setScaleTypePanel.add(melMinorA);
		setScaleTypePanel.add(melMinorD);
		setScaleTypePanel.add(dorian);
		setScaleTypePanel.add(lydian);
		setScaleTypePanel.add(phrygian);
		setScaleTypePanel.add(mixolydian);
		setScaleTypePanel.add(locrian);
		setScaleTypePanel.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Set Scale Type"));

		JPanel setHarmonicIntervalPanel = new JPanel();

		setPanel.add(setTonicPanel);
		setPanel.add(setScaleTypePanel);
		setPanel.add(setHarmonicIntervalPanel);
		setPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		harmIntervalPanel.getClass();
		JPanel harmIntervalSliderPanel = new JPanel();
		harmIntervalSlider = new JSlider(0, 12, ModReciever.PERFECT_FOURTH);
		harmIntervalSlider.setMajorTickSpacing(2);
		harmIntervalSlider.setMinorTickSpacing(1);
		harmIntervalSlider.setPaintTicks(true);
		harmIntervalSlider.setPaintLabels(true);
		harmIntervalSlider.setSnapToTicks(true);
		harmIntervalSlider.setToolTipText("Harmonic Interval (Halfsteps)");
		JButton setHarmInterval = new JButton("Add");
		setHarmInterval.setActionCommand("addHarmInterval");
		setHarmInterval.addActionListener(this);
		harmIntervalSliderPanel.add(harmIntervalSlider);
		harmIntervalSliderPanel.add(setHarmInterval);

		JPanel harmIntervalsListPanel = new JPanel();
		harmIntervalsList = new JList<Integer>(harmIntervalListModel);
		harmIntervalsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		harmIntervalsList.setLayoutOrientation(JList.VERTICAL);
		JScrollPane harmIntervalsListScrollPane = new JScrollPane(harmIntervalsList);
		harmIntervalsListScrollPane.setPreferredSize(new Dimension(50, 75));
		JButton remHarmInterval = new JButton("Remove");
		remHarmInterval.setActionCommand("remHarmInterval");
		remHarmInterval.addActionListener(this);

		harmIntervalsListPanel.add(harmIntervalsListScrollPane, BorderLayout.NORTH);
		harmIntervalsListPanel.add(remHarmInterval, BorderLayout.SOUTH);

		harmIntervalPanel.setLayout(new BorderLayout());
		harmIntervalPanel.add(harmIntervalSliderPanel, BorderLayout.NORTH);
		harmIntervalPanel.add(harmIntervalsListPanel, BorderLayout.SOUTH);
		harmIntervalPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
				"Add Harmonic Intervals"));

		setLayout(new BorderLayout());
		add(setPanel, BorderLayout.NORTH);
		add(checks, BorderLayout.CENTER);
		add(harmIntervalPanel, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "c":
			md.setTonic(ModReciever.C);
			break;
		case "cs":
			md.setTonic(ModReciever.CS);
			break;
		case "d":
			md.setTonic(ModReciever.D);
			break;
		case "e":
			md.setTonic(ModReciever.E);
			break;
		case "f":
			md.setTonic(ModReciever.F);
			break;
		case "fs":
			md.setTonic(ModReciever.FS);
			break;
		case "g":
			md.setTonic(ModReciever.G);
			break;
		case "gs":
			md.setTonic(ModReciever.GS);
			break;
		case "a":
			md.setTonic(ModReciever.A);
			break;
		case "as":
			md.setTonic(ModReciever.AS);
			break;
		case "b":
			md.setTonic(ModReciever.B);
			break;
		case "major":
			md.setScaleType(ScaleTypes.MAJOR);
			break;
		case "minor":
			md.setScaleType(ScaleTypes.MINOR);
			break;
		case "harmMinor":
			md.setScaleType(ScaleTypes.HARMONIC_MINOR);
			break;
		case "melMinorA":
			md.setScaleType(ScaleTypes.MELODIC_MINOR_ASCENDING);
			break;
		case "melMinorD":
			md.setScaleType(ScaleTypes.MELODIC_MINOR_DESCENDING);
			break;
		case "dorian":
			md.setScaleType(ScaleTypes.DORIAN);
			break;
		case "lydian":
			md.setScaleType(ScaleTypes.LYDIAN);
			break;
		case "phrygian":
			md.setScaleType(ScaleTypes.PHYRGIAN);
			break;
		case "locrian":
			md.setScaleType(ScaleTypes.LOCRIAN);
			break;
		case "mixolydian":
			md.setScaleType(ScaleTypes.MIXOLYDIAN);
			break;
		case "doHarm":
			md.doHarmonize(!md.isHarmonized());
			break;
		case "doHarmDiatonic":
			md.doHarmonizeDiatonically(!md.isHarmonizedDiatonically());
			break;
		case "doMelodicInvert":
			md.doMelodicallyInvert(!md.isMelodicallyInverted());
			break;
		case "doHarmChord":
			md.doHarmonizeChord(!md.isChordHarmonized());
			break;
		case "addHarmInterval":
			if (md.addHarmonicInterval(harmIntervalSlider.getValue()))
				harmIntervalListModel.addElement(harmIntervalSlider.getValue());
			break;
		case "remHarmInterval":
			md.removeHarmonicInterval(harmIntervalsList.getSelectedValue());
			harmIntervalListModel.remove(harmIntervalsList.getSelectedIndex());
			break;
		case "PANIC":
			md.panic();
			break;
		}
	}
}
