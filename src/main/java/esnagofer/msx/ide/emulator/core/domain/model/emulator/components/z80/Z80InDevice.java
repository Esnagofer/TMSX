package esnagofer.msx.ide.emulator.core.domain.model.emulator.components.z80;

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
