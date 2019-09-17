package edu.moravian.schirripad.rpi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import edu.moravian.schirripad.ModReciever;
import edu.moravian.schirripad.ScaleTypes;

public class ControlBoard implements GpioPinListenerDigital, ActionListener {
	private GpioController gp = GpioFactory.getInstance();

	private GpioPinDigitalInput midiReset, doHarmonize, doInvert, doHarmonizeDiatonically, setTonic,
			setHarmonicInterval, setScaleType, tonicC, tonicCS, tonicD, tonicDS, tonicE, tonicF, tonicFS, tonicG,
			tonicGS, tonicA, tonicAS, tonicB;

	private boolean settingTonic, settingHarmonicInterval, settingScaleType;

	private GpioPinDigitalOutput doHarmLed, doHarmDiatonicLed, doInvertLed, setHarmIntLed, setTonicLed, isRunningLed,
			setScaleLed;

	private ModReciever md;

	private Timer ledFlasher = new Timer(500, this);

	/**
	 * Interface for foot Control Board <br>
	 * 19 Foot Switches, 8 LEDs
	 * 
	 * @param md
	 *            ModReciever to control
	 */
	public ControlBoard(ModReciever md) {
		this.md = md;
		// PIN Diagram
		// 0 - MIDI Reset
		// 1 DoHarm
		// 2 DoInvert
		// 3 DoHarmDiatonically
		// 4 Set Tonic
		// 5 Set Harm Interval
		// 6 Set ScaleType
		// 7 C/Octave Major
		// 8 C#/m2 Minor
		// 9 D/M2 Harmonic Minor
		// 10 D#/m3 Melodic Minor A
		// 11 E/M3 Melodic Minor B
		// 12 F/P4 Dorian
		// 13 F#/TT Phrygian
		// 14 G/P5 Lydian
		// 15 G#/m6 Mixolydian
		// 16 A/M6 Locrian
		// 17 A#/m7
		// 18 B/M7
		// 19 DOHARM LED
		// 20 DOHARMDIATONIC LED
		// 21 DOINVERT LED
		// 22 SETHARMINT LED
		// 23 SETTONIC LED
		// 24 RUNNING LED
		// 25 SETSCALE LED
		// POWER LED OFF 5v

		// Set Up Input Pins
		midiReset = gp.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN);
		midiReset.addListener(this);
		midiReset.setName("RESET");
		doHarmonize = gp.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN);
		doHarmonize.addListener(this);
		doHarmonize.setName("doHarmonize");
		doInvert = gp.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
		doInvert.addListener(this);
		doInvert.setName("doInvert");
		doHarmonizeDiatonically = gp.provisionDigitalInputPin(RaspiPin.GPIO_03, PinPullResistance.PULL_DOWN);
		doHarmonizeDiatonically.addListener(this);
		doHarmonizeDiatonically.setName("doHarmonizeDiatonically");
		setTonic = gp.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
		setTonic.addListener(this);
		setTonic.setName("setTonic");
		setHarmonicInterval = gp.provisionDigitalInputPin(RaspiPin.GPIO_05, PinPullResistance.PULL_DOWN);
		setHarmonicInterval.addListener(this);
		setHarmonicInterval.setName("setHarmonicInterval");
		setScaleType = gp.provisionDigitalInputPin(RaspiPin.GPIO_06, PinPullResistance.PULL_DOWN);
		setScaleType.addListener(this);
		setScaleType.setName("setScaleType");
		tonicC = gp.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.PULL_DOWN);
		tonicC.addListener(this);
		tonicC.setName("tonicC");
		tonicCS = gp.provisionDigitalInputPin(RaspiPin.GPIO_08, PinPullResistance.PULL_DOWN);
		tonicCS.addListener(this);
		tonicCS.setName("tonicCS");
		tonicD = gp.provisionDigitalInputPin(RaspiPin.GPIO_09, PinPullResistance.PULL_DOWN);
		tonicD.addListener(this);
		tonicD.setName("tonicD");
		tonicDS = gp.provisionDigitalInputPin(RaspiPin.GPIO_10, PinPullResistance.PULL_DOWN);
		tonicDS.addListener(this);
		tonicDS.setName("tonicDS");
		tonicE = gp.provisionDigitalInputPin(RaspiPin.GPIO_11, PinPullResistance.PULL_DOWN);
		tonicE.addListener(this);
		tonicE.setName("tonicE");
		tonicF = gp.provisionDigitalInputPin(RaspiPin.GPIO_12, PinPullResistance.PULL_DOWN);
		tonicF.addListener(this);
		tonicF.setName("tonicF");
		tonicFS = gp.provisionDigitalInputPin(RaspiPin.GPIO_13, PinPullResistance.PULL_DOWN);
		tonicFS.addListener(this);
		tonicFS.setName("tonicFS");
		tonicG = gp.provisionDigitalInputPin(RaspiPin.GPIO_14, PinPullResistance.PULL_DOWN);
		tonicG.addListener(this);
		tonicG.setName("tonicG");
		tonicGS = gp.provisionDigitalInputPin(RaspiPin.GPIO_15, PinPullResistance.PULL_DOWN);
		tonicGS.addListener(this);
		tonicGS.setName("tonicGS");
		tonicA = gp.provisionDigitalInputPin(RaspiPin.GPIO_16, PinPullResistance.PULL_DOWN);
		tonicA.addListener(this);
		tonicA.setName("tonicA");
		tonicAS = gp.provisionDigitalInputPin(RaspiPin.GPIO_17, PinPullResistance.PULL_DOWN);
		tonicAS.addListener(this);
		tonicAS.setName("tonicAS");
		tonicB = gp.provisionDigitalInputPin(RaspiPin.GPIO_18, PinPullResistance.PULL_DOWN);
		tonicB.addListener(this);
		tonicB.setName("tonicB");

		// Set LED Pins
		doHarmLed = gp.provisionDigitalOutputPin(RaspiPin.GPIO_19);
		doHarmDiatonicLed = gp.provisionDigitalOutputPin(RaspiPin.GPIO_20);
		doInvertLed = gp.provisionDigitalOutputPin(RaspiPin.GPIO_21);
		setHarmIntLed = gp.provisionDigitalOutputPin(RaspiPin.GPIO_22);
		setTonicLed = gp.provisionDigitalOutputPin(RaspiPin.GPIO_23);
		isRunningLed = gp.provisionDigitalOutputPin(RaspiPin.GPIO_24);
		setScaleLed = gp.provisionDigitalOutputPin(RaspiPin.GPIO_25);

		// Test LEDS
		doHarmLed.high();
		_wait();
		doHarmLed.low();
		doHarmDiatonicLed.high();
		_wait();
		doHarmDiatonicLed.low();
		doInvertLed.high();
		_wait();
		doInvertLed.low();
		setHarmIntLed.high();
		_wait();
		setHarmIntLed.low();
		setTonicLed.high();
		_wait();
		setTonicLed.low();
		isRunningLed.high();
		_wait();
		isRunningLed.low();
		setScaleLed.high();
		_wait();
		setScaleLed.low();

		// Set Pin defaults
		doHarmLed.high();
		doHarmDiatonicLed.low();
		doInvertLed.low();
		setHarmIntLed.low();
		setTonicLed.low();
		isRunningLed.high();
		setScaleLed.low();

		// Add shutdown hook to ensure all LEDs are powered OFF on shutdown
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				doHarmLed.low();
				doHarmDiatonicLed.low();
				doInvertLed.low();
				setHarmIntLed.low();
				setTonicLed.low();
				isRunningLed.low();
				setScaleLed.low();
			}
		});
	}

	// Arbitrary wait method for LED testing
	private void _wait() {
		for (int i = 1000; i > 0; i--)
			;
	}

	@Override
	// Handle GPIO state changes
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent e) {
		switch (e.getPin().getName()) {
		// Toggle Harmonization
		case "doHarmonize":
			md.doHarmonize(!md.isHarmonized());
			break;
		// Toggle Diatonic Harmonization
		case "doHarmonizeDiatonically":
			md.doHarmonizeDiatonically(!md.isHarmonizedDiatonically());
			break;
		// Toggle Inversion
		case "doInvert":
			md.doMelodicallyInvert(!md.isMelodicallyInverted());
			break;
		// MIDI panic
		case "RESET":
			md.panic();
			break;
		// Set settingTonic true
		case "setTonic":
			settingTonic = true;
			if (settingHarmonicInterval)
				settingHarmonicInterval = false;
			if (settingScaleType)
				settingScaleType = false;
			break;
		// Set settingHarmonicInterval true
		case "setHarmonicInterval":
			settingHarmonicInterval = true;
			if (settingTonic)
				settingTonic = false;
			if (settingScaleType)
				settingScaleType = false;
			break;
		// Set settingScaleType true
		case "setScaleType":
			settingScaleType = true;
			if (settingTonic)
				settingTonic = false;
			if (settingHarmonicInterval)
				settingHarmonicInterval = false;
			break;
		case "tonicC":
			// Set tonic note to C
			if (settingTonic) {
				md.setTonic(ModReciever.C);
				settingTonic = false;
				// Set harmonic interval to an Octave
			} else if (settingHarmonicInterval) {
				md.setHarmonizationInterval(ModReciever.OCTAVE);
				settingHarmonicInterval = false;
				// Set scale type to Major
			} else if (settingScaleType) {
				md.setScaleType(ScaleTypes.MAJOR);
				settingScaleType = false;
			}
			break;
		// Basically same as case "tonicC"
		case "tonicCS":
			if (settingTonic) {
				md.setTonic(ModReciever.CS);
				settingTonic = false;
			} else if (settingHarmonicInterval) {
				md.setHarmonizationInterval(ModReciever.MINOR_SECOND);
				settingHarmonicInterval = false;
			} else if (settingScaleType) {
				md.setScaleType(ScaleTypes.MINOR);
				settingScaleType = false;
			}
			break;
		case "tonicD":
			if (settingTonic) {
				md.setTonic(ModReciever.D);
				settingTonic = false;
			} else if (settingHarmonicInterval) {
				md.setHarmonizationInterval(ModReciever.MAJOR_SECOND);
				settingHarmonicInterval = false;
			} else if (settingScaleType) {
				md.setScaleType(ScaleTypes.HARMONIC_MINOR);
				settingScaleType = false;
			}
			break;
		case "tonicDS":
			if (settingTonic) {
				md.setTonic(ModReciever.DS);
				settingTonic = false;
			} else if (settingHarmonicInterval) {
				md.setHarmonizationInterval(ModReciever.MINOR_THIRD);
				settingHarmonicInterval = false;
			} else if (settingScaleType) {
				md.setScaleType(ScaleTypes.MELODIC_MINOR_ASCENDING);
				settingScaleType = false;
			}
			break;
		case "tonicE":
			if (settingTonic) {
				md.setTonic(ModReciever.E);
				settingTonic = false;
			} else if (settingHarmonicInterval) {
				md.setHarmonizationInterval(ModReciever.MAJOR_THIRD);
				settingHarmonicInterval = false;
			} else if (settingScaleType) {
				md.setScaleType(ScaleTypes.MELODIC_MINOR_DESCENDING);
				settingScaleType = false;
			}
			break;
		case "tonicF":
			if (settingTonic) {
				md.setTonic(ModReciever.F);
				settingTonic = false;
			} else if (settingHarmonicInterval) {
				md.setHarmonizationInterval(ModReciever.PERFECT_FOURTH);
				settingHarmonicInterval = false;
			} else if (settingScaleType) {
				md.setScaleType(ScaleTypes.DORIAN);
				settingScaleType = false;
			}
			break;
		case "tonicFS":
			if (settingTonic) {
				md.setTonic(ModReciever.FS);
				settingTonic = false;
			} else if (settingHarmonicInterval) {
				md.setHarmonizationInterval(ModReciever.TRITONE);
				settingHarmonicInterval = false;
			} else if (settingScaleType) {
				md.setScaleType(ScaleTypes.PHYRGIAN);
				settingScaleType = false;
			}
			break;
		case "tonicG":
			if (settingTonic) {
				md.setTonic(ModReciever.G);
				settingTonic = false;
			} else if (settingHarmonicInterval) {
				md.setHarmonizationInterval(ModReciever.PERFECT_FIFTH);
				settingHarmonicInterval = false;
			} else if (settingScaleType) {
				md.setScaleType(ScaleTypes.LYDIAN);
				settingScaleType = false;
			}
			break;
		case "tonicGS":
			if (settingTonic) {
				md.setTonic(ModReciever.GS);
				settingTonic = false;
			} else if (settingHarmonicInterval) {
				md.setHarmonizationInterval(ModReciever.MINOR_SIXTH);
				settingHarmonicInterval = false;
			} else if (settingScaleType) {
				md.setScaleType(ScaleTypes.MIXOLYDIAN);
				settingScaleType = false;
			}
			break;
		case "tonicA":
			if (settingTonic) {
				md.setTonic(ModReciever.A);
				settingTonic = false;
			} else if (settingHarmonicInterval) {
				md.setHarmonizationInterval(ModReciever.MAJOR_SIXTH);
				settingHarmonicInterval = false;
			} else if (settingScaleType) {
				md.setScaleType(ScaleTypes.MAJOR);
				settingScaleType = false;
			}
			break;
		case "tonicAS":
			if (settingTonic) {
				md.setTonic(ModReciever.AS);
				settingTonic = false;
			} else if (settingHarmonicInterval) {
				md.setHarmonizationInterval(ModReciever.MINOR_SEVENTH);
				settingHarmonicInterval = false;
			} else if (settingScaleType) {
				md.setScaleType(ScaleTypes.MAJOR);
				settingScaleType = false;
			}
			break;
		case "tonicB":
			if (settingTonic) {
				md.setTonic(ModReciever.B);
				settingTonic = false;
			} else if (settingHarmonicInterval) {
				md.setHarmonizationInterval(ModReciever.MAJOR_SEVENTH);
				settingHarmonicInterval = false;
			} else if (settingScaleType) {
				md.setScaleType(ScaleTypes.MAJOR);
				settingScaleType = false;
			}
			break;
		}

		// Always reset setting LEDs to low
		if (!settingTonic && setTonicLed.isHigh())
			setTonicLed.low();
		if (!settingHarmonicInterval && setHarmIntLed.isHigh())
			setHarmIntLed.low();
		if (!settingScaleType && setScaleLed.isHigh())
			setScaleLed.low();

		// Only run ledFlasher if a setting is being changed to save resources
		if ((settingTonic || settingHarmonicInterval || settingScaleType) && !ledFlasher.isRunning())
			ledFlasher.start();
		if (!settingTonic && !settingHarmonicInterval && !settingScaleType && ledFlasher.isRunning())
			ledFlasher.stop();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Flash LEDs if settings are being changed
		if (settingTonic) {
			if (setTonicLed.isHigh())
				setTonicLed.low();
			else
				setTonicLed.high();
		}
		if (settingHarmonicInterval) {
			if (setHarmIntLed.isHigh())
				setHarmIntLed.low();
			else
				setTonicLed.high();
		}
		if (settingScaleType) {
			if (setScaleLed.isHigh())
				setScaleLed.low();
			else
				setScaleLed.high();
		}

	}

}
