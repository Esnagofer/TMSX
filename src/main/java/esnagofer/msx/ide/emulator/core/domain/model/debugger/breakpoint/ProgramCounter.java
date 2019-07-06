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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProgramCounter other = (ProgramCounter) obj;
		if (value != other.value)
			return false;
		return true;
	}

	
}
