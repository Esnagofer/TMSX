package msx.emulator.core.domain.model.hardware.z80;

/**
 * The Interface Z80InDevice.
 */
@FunctionalInterface
public interface Z80InDevice {

	/**
	 * In.
	 *
	 * @param port the port
	 * @return the byte
	 */
	public abstract byte in(byte port);

}
