package esnagofer.msx.ide.emulator.core.domain.model.components.memory;

import esnagofer.msx.ide.lib.Tools;

/**
 * 
 * An object of this class holds a 64 KB memory space.
 * It provides read/write operations as well as some
 * management functionality. It is abstract and can
 * be extended to provide specific functionality, such
 * as RAM, ROM, or a memory space with underlying page
 * switching mechanism.
 * 
 * @author tjitze
 * @author esnagofer
 *
 */

public class AbstractMemory implements Memory {

	/** The name. */
	private final String name;
	
	/**
	 * Instantiates a new abstract slot.
	 *
	 * @param name the name
	 */
	protected AbstractMemory(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.memory.Memory#rdByte(short)
	 */
	@Override
	public byte rdByte(short addr) {
		throw new UnsupportedOperationException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.memory.Memory#wrtByte(short, byte)
	 */
	@Override
	public void wrtByte(short addr, byte value) {
		throw new UnsupportedOperationException("Not implemented");	
	}

	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.memory.Memory#isWritable(short)
	 */
	@Override
	public boolean isWritable(short addr) {
		throw new UnsupportedOperationException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.memory.Memory#writeByte(byte, byte, byte)
	 */
	@Override
	public void writeByte(byte lsb, byte msb, byte value) {
		wrtByte((short)((msb << 8) | (lsb & 0xff)), value);
	}

	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.memory.Memory#writeShortLH(short, short)
	 */
	@Override
	public void writeShortLH(short addr, short word) {
		wrtByte(addr, Tools.getLSB(word));
		wrtByte((short)(addr + 1), Tools.getMSB(word));
	}

	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.memory.Memory#readWordLH(short)
	 */
	@Override
	public short readWordLH(short addr) {
		byte fst = rdByte(addr);
		byte snd = rdByte((short)(addr+1));
		return (short) ((fst & 0xff) | (snd << 8));
	}
	
	/**
	 * New instance.
	 *
	 * @param name the name
	 * @return the abstract memory
	 */
	public static AbstractMemory newInstance(String name) {
		return new AbstractMemory(name);
	}

}
