package tmsx.domain.model.hardware.standard.z80;

/**
 * The Interface Z80OutDevice.
 */
@FunctionalInterface
public interface Z80OutDevice {

	/**
	 * Out.
	 *
	 * @param port the port
	 * @param value the value
	 */
	public abstract void out(byte port, byte value);
	
}
