package tmsx.domain.model.hardware.memory;

/**
 * Implements an empty memory slot, without writeable addresses, and returning
 * 0xff (the default 'pull-up' value) when reading any address.
 * 
 * @author tjitze
 * @author esnagofer
 *
 */
public class EmptyMemory extends AbstractMemory {

	/**
	 * Instantiates a new empty slot.
	 *
	 * @param name
	 *            the name
	 */
	public EmptyMemory(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tmsx.domain.model.emulator.memory.AbstractSlot#rdByte(short)
	 */
	@Override
	public byte rdByte(short addr) {
		return (byte) 0x00;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tmsx.domain.model.emulator.memory.AbstractSlot#wrtByte(short, byte)
	 */
	@Override
	public final void wrtByte(short addr, byte value) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tmsx.domain.model.emulator.memory.AbstractSlot#isWritable(short)
	 */
	@Override
	public boolean isWritable(short addr) {
		return false;
	}

}