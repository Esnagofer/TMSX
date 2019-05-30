package esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint;

/**
 * The Class ProgramCounter.
 */
public class ProgramCounter {

	/** The value. */
	private short value;
	
	/**
	 * Instantiates a new program counter.
	 *
	 * @param value the value
	 */
	public ProgramCounter(short value) {
		if ((value < 0) || (value > 0xFFFF)) {
			throw new IllegalStateException(String.format("Invalid PC value: %i", value));
		}
	}
	
	/**
	 * Value.
	 *
	 * @return the short
	 */
	public short value() {
		return value;
	}

}
