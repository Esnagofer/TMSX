package tmsx.domain.model.hardware.ay38910;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * The AY-3-8912 sound chip emulator.
 * http://www.howell1964.freeserve.co.uk/parts/ay3891x_datasheet.htm
 *
 * @author <A HREF="mailto:razvan.surdulescu@post.harvard.edu">Razvan Surdulescu</A> (c) 2001 - 2006
 * @author <A HREF="mailto:erikduijs@yahoo.com">Erik Dujis</A> (c) 2001
 * @author <A HREF="mailto:ensjo@nautilus.com.br">Emerson JosÈ Silveira da Costa ("Ensjo")</a> (c) 2005
 * <BR>
 * You may use and distribute this software for free provided you include
 * this copyright notice. You may not sell this software, use the author
 * names for publicity reasons or modify the code without permission from
 * the authors.
 */
public class AY38910 extends Thread {

	/** The sampling frequency for playing sounds with the speaker or AY chip. */
	public static final float SAMPLE_FREQ = 48000.0f;
	/** The size of the buffer used for generating sounds. */
	public static final int LINE_BUF_SIZE = 1024;
	
	/** Source data line (audio output channel) */
	private SourceDataLine m_line;

	/** The sound buffer as it is currently being filled and played. */
	protected byte[] m_buffer = new byte[LINE_BUF_SIZE];
	
	/** The current index into the sound buffer. */
	protected int m_index = 0;
		
	private static final int AY8912_FREQ = 221660;
	private static final int FREQ_SCALE = (int) (AY8912_FREQ / SAMPLE_FREQ);
	
	private static int MAX_CHANNEL_VOLUME = 0x0f;
	
	/*
	 The frequency of each square wave generated by the three tone
	 generators (channels A, B, C) is obtained by combining the contents
	 of the Coarse and Fine Tune registers.
	 
	 FTC = fine tune control (provides the lower 8 bits)
	 CTC = coarse tune control (provides the higher 4 bits)
	 */
	private static final int R_FTC_A = 0, R_CTC_A = 1;
	private static final int R_FTC_B = 2, R_CTC_B = 3;
	private static final int R_FTC_C = 4, R_CTC_C = 5;
	
	/*
	 The frequency of the noise source is the 5 bit noise period value
	 (the lower 5 bits of the noise register).
	 */
	private static final int R_NOISE = 6;
	
	/*
	 The bits of the mixer register are as follows:
	 
	 b7 = input enable I/O port B
	 b6 = input enable I/O port A
	 
	 b5 = noise enable channel C
	 b4 = noise enable channel B
	 b3 = noise enable channel A
	 
	 b2 = tone enable channel C
	 b1 = tone enable channel B
	 b0 = tone enable channel A
	 */
	private static final int R_MIXER = 7;
	
	/*
	 The amplitudes of the signals generated by each of the three D/A
	 converters (channels A, B, C) is determined by the contents of the
	 lower 5 bits of the amplitude registers:
	 
	 b4 = amplitude mode (modulate envelope or pitch)
	 b3 - b0 = fixed amplitude level
	 */
	private static final int R_AMP_A = 8, R_AMP_B = 9, R_AMP_C = 10;
	
	/*
	 The frequency of the envelope is the 16 bit envelope period value.
	 
	 FTC = fine tune control (provides the lower 8 bits)
	 CTC = coarse tune control (provides the higher 8 bits)
	 */
	private static final int R_FPC_E = 11, R_CPC_E = 12;
	
	private static final int R_ENVELOPE = 13;

	private static final int R_RS232_A = 14, R_RS232_B = 15;

	/** Envelope actions. */
	private static final int ENV_ATTACK = 0;
	private static final int ENV_DECAY = 1;
	private static final int ENV_SUSTAIN_LOW = 2;
	private static final int ENV_SUSTAIN_HIGH = 3;
	private static final int ENV_REPEAT = 4;
	private static final int ENV_RESTART = 5;
	private static final int ENV_ERROR = 6;

	/**
	 * The list of all possible envelope shapes.
	 *
	 * An envelope shape starts in a particular action and advances
	 * to a subsequent action with each clock tick. The current action
	 * dictates what to do next. ENV_REPEAT means go back to the
	 * previous action; ENV_RESTART means go back to the beginning
	 * of the envelope.
	 */
	private static final int[][] ENVELOPES = {
		new int[]{ENV_DECAY, ENV_SUSTAIN_LOW, ENV_REPEAT},
		new int[]{ENV_DECAY, ENV_SUSTAIN_LOW, ENV_REPEAT},
		new int[]{ENV_DECAY, ENV_SUSTAIN_LOW, ENV_REPEAT},
		new int[]{ENV_DECAY, ENV_SUSTAIN_LOW, ENV_REPEAT},
		new int[]{ENV_ATTACK, ENV_SUSTAIN_LOW, ENV_REPEAT},
		new int[]{ENV_ATTACK, ENV_SUSTAIN_LOW, ENV_REPEAT},
		new int[]{ENV_ATTACK, ENV_SUSTAIN_LOW, ENV_REPEAT},
		new int[]{ENV_ATTACK, ENV_SUSTAIN_LOW, ENV_REPEAT},
		new int[]{ENV_DECAY, ENV_REPEAT, ENV_ERROR},
		new int[]{ENV_DECAY, ENV_SUSTAIN_LOW, ENV_REPEAT},
		new int[]{ENV_DECAY, ENV_ATTACK, ENV_RESTART},
		new int[]{ENV_DECAY, ENV_SUSTAIN_HIGH, ENV_REPEAT},
		new int[]{ENV_ATTACK, ENV_REPEAT, ENV_ERROR},
		new int[]{ENV_ATTACK, ENV_SUSTAIN_HIGH, ENV_REPEAT},
		new int[]{ENV_ATTACK, ENV_DECAY, ENV_RESTART},
		new int[]{ENV_ATTACK, ENV_SUSTAIN_LOW, ENV_REPEAT}
	};
	
	/** Pitch value for sound channel. */
	private int m_pitchA, m_pitchB, m_pitchC;
	
	/** Period value for noise. */
	private int m_periodN;
	
	/** Mixer register. */
	private int m_mixer;
	
	/** Volume value for sound channel. */
	private int m_amplitudeA, m_amplitudeB, m_amplitudeC;
	
	/** Period value for envelope. */
	private int m_periodE;

	/** Envelope type register. */
	private int m_envelopeType;

	/** Indicates whether a particular channel is in envelope or pitch mode. */
	private boolean m_useEnvelopeA, m_useEnvelopeB, m_useEnvelopeC;

	/** Channel A/B/C frequency counter */
	private int _counterA = 0, _counterB = 0, _counterC = 0;
	
	/** Turn channel A/B/C on/off */
	private boolean _generatorA = false, _generatorB = false, _generatorC = false;
	
	/** Noise frequency counter */
	private int _counterN = 0;
	
	/** Turn noise on/off */
	private boolean _generatorN = false;

	/** Envelope frequency counter */
	private int _counterE = 0;

	/** Envelope tick counter */
	private int _tickE = 0;

	/** Envelope state */
	private int _envelopeState = 0;
	
	public void reset() {
	}
	
	/**
	 * Return value of given PSG register.
	 */
	public int in(int register) {
		switch (register) {
		case R_FTC_A:
			return m_pitchA & 0xff;
		case R_CTC_A:
			return (m_pitchA & 0xf00) >> 8;
		case R_FTC_B:
			return m_pitchB & 0xff;
		case R_CTC_B:
			return (m_pitchB & 0xf00) >> 8;
		case R_FTC_C:
			return m_pitchC & 0xff;
		case R_CTC_C:
			return (m_pitchC & 0xf00) >> 8;
		case R_NOISE:
			return m_periodN;
		case R_MIXER:
			return m_mixer;
		case R_AMP_A:
			return m_amplitudeA;
		case R_AMP_B:
			return m_amplitudeB;
		case R_AMP_C:
			return m_amplitudeC;
		case R_FPC_E:
			return m_periodE & 0xff;
		case R_CPC_E:
			return (m_periodE & 0xff00) >> 8;
		case R_ENVELOPE:
			return m_envelopeType;
		case R_RS232_A:
		case R_RS232_B:
			return 0xff; // TODO: returning 0x00 here leads to joystick being detected. 0xff seems to work, but need to check meaning of bits
		default:
			return 0;
		}
	}
	
	/**
	 * Set value of given PSG register.
	 */
	public void out(int register, int value) {
		switch (register) {
		case R_FTC_A:
			m_pitchA = ((m_pitchA & 0x0f00) | value);
			break;
		case R_CTC_A:
			m_pitchA = (((value & 0x0f) << 8) | (m_pitchA & 0xff));
			break;
		case R_FTC_B:
			m_pitchB = ((m_pitchB & 0x0f00) | value);
			break;
		case R_CTC_B:
			m_pitchB = (((value & 0x0f) << 8) | (m_pitchB & 0xff));
			break;
		case R_FTC_C:
			m_pitchC = ((m_pitchC & 0x0f00) | value);
			break;
		case R_CTC_C:
			m_pitchC = (((value & 0x0f) << 8) | (m_pitchC & 0xff));
			//System.out.println("pitch c = " + m_pitchC);
			break;
		case R_NOISE:
			m_periodN = (value & 0x1f);
			break;
		case R_MIXER:
			m_mixer = value;
			break;
		case R_AMP_A:
			m_amplitudeA = (value & 0x0f);
			m_useEnvelopeA = ((value & 0x10) != 0);
			break;
		case R_AMP_B:
			m_amplitudeB = (value & 0x0f);
			m_useEnvelopeB = ((value & 0x10) != 0);
			break;
		case R_AMP_C:
			m_amplitudeC = (value & 0x0f);
			m_useEnvelopeC = ((value & 0x10) != 0);
			break;
		case R_FPC_E:
			m_periodE = ((m_periodE & 0xff00) | value);
			break;
		case R_CPC_E:
			m_periodE = ((value << 8) | (m_periodE & 0xff));
			break;
		case R_ENVELOPE:
			m_envelopeType = value & 0x0f;

			// Reset the envelope counters
			_envelopeState = 0;
			_counterE = 0;
			_tickE = 0;
			break;
		case R_RS232_A:
			// We ignore I/O ports
			break;
		case R_RS232_B:
			// We ignore I/O ports
			break;
		default:
			//m_logger.log(ILogger.C_ERROR, "Unknown AY register: " + port16);
			break;
		}
	}
	
	public int getSound() {

		int samples = 1;
		
		if (m_pitchA > 0) {
			_counterA += samples * FREQ_SCALE;
			if (_counterA >= m_pitchA) {
				_generatorA = !_generatorA;
				_counterA -= m_pitchA;
			}
		}

		if (m_pitchB > 0) {
			_counterB += samples * FREQ_SCALE;
			if (_counterB >= m_pitchB) {
				_generatorB = !_generatorB;
				_counterB -= m_pitchB;
			}
		}

		if (m_pitchC > 0) {
			_counterC += samples * FREQ_SCALE;
			if (_counterC >= m_pitchC) {
				_generatorC = !_generatorC;
				_counterC -= m_pitchC;
			}
		}

		if (m_periodN > 0) {
			_counterN += samples * FREQ_SCALE;
			// The noise counter runs at half the speed of the
			// tone counter, so double the noise period
			if (_counterN >= (m_periodN << 1)) {
				_counterN -= (m_periodN << 1);
				_generatorN = (Math.random() >= 0.5 ? true : false);
			}
		}

		// Envelope shape (from the ENVELOPES array)
		int[] envelopeShape = ENVELOPES[m_envelopeType];
		
		int amplitudeE = 0;

		if (m_periodE > 0) {
			_counterE += samples * FREQ_SCALE;
			// The envelope counter runs at half the speed of the
			// tone counter, so double the envelope period
			if (_counterE >= (m_periodE << 1)) {
				_counterE -= (m_periodE << 1);

				_tickE++;
				if (_tickE == MAX_CHANNEL_VOLUME) {
					_tickE = 0;

					_envelopeState++;
					switch (envelopeShape[_envelopeState]) {
					case ENV_REPEAT:
						_envelopeState--;
						break;
					case ENV_RESTART:
						_envelopeState = 0;
						break;
					case ENV_ERROR:
						//m_logger.log(ILogger.C_ERROR, "Illegal envelope state reached: " +
						//		envelopeShape[_envelopeState]);
						break;
					default:
						// fallthrough
						break;
					}
				}

				switch (envelopeShape[_envelopeState]) {
				case ENV_ATTACK:
					amplitudeE = _tickE;
					break;
				case ENV_DECAY:
					amplitudeE = MAX_CHANNEL_VOLUME - _tickE;
					break;
				case ENV_SUSTAIN_LOW:
					amplitudeE = 0;
					break;
				case ENV_SUSTAIN_HIGH:
					amplitudeE = MAX_CHANNEL_VOLUME;
					break;
				default:
					//m_logger.log(ILogger.C_ERROR, "Illegal envelope state reached: " +
					//		envelopeShape[_envelopeState]);
				break;
				}
			}
		}

		int amplitudeA = (m_useEnvelopeA ? amplitudeE : m_amplitudeA);
		int amplitudeB = (m_useEnvelopeB ? amplitudeE : m_amplitudeB);
		int amplitudeC = (m_useEnvelopeC ? amplitudeE : m_amplitudeC);

		int val = 0;
		
		if (((m_mixer & 0x1) == 0 && _generatorA) || ((m_mixer & 0x08) == 0 && _generatorN)) {
			val += amplitudeA;
		}
		
		if (((m_mixer & 0x2) == 0 && _generatorB) || ((m_mixer & 0x10) == 0 && _generatorN)) {
			val += amplitudeB;
		}
		
		if (((m_mixer & 0x4) == 0 && _generatorC) || ((m_mixer & 0x20) == 0 && _generatorN)) {
			val += amplitudeC;
		}

		// Scale val up close to a byte
		val = (val * 0xff) / (3 * MAX_CHANNEL_VOLUME);
		return val;
	}
	
	/**
	 * Initialize PSG by setting up a source data line (audio output stream).
	 * Call this before starting thread.
	 */
	public void init() {

		AudioFormat format = new AudioFormat(
				AudioFormat.Encoding.PCM_UNSIGNED, // encoding (pulse code modulation)
				SAMPLE_FREQ, // sample rate: number of samples taken per second, per channel
				8, // sample size in bits
				1, // channels (1 = mono, 2 = stereo)
				1, // frame size: number of channels * sample size per channel (in bytes)
				SAMPLE_FREQ, // frame rate: number of frames (set of samples for all channels) taken per second
				true); // big endian; relevant only if sample size > 8
		
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, 2 * LINE_BUF_SIZE);

		try {
			m_line = (SourceDataLine) AudioSystem.getLine(info);
			m_line.open();
			m_line.start();
		} catch (LineUnavailableException lue) {
			System.out.println("SourceDataLine unavailable");
			m_line = null;
		}

	}
	
    public void run() {

    	while (true) {
    		m_buffer[m_index] = (byte)(getSound() & 0xff);
    		m_index++;
    		if (m_index == m_buffer.length) {
    			m_line.write(m_buffer, 0, LINE_BUF_SIZE);
            	m_index = 0;
    		}
    	}
    }

}
