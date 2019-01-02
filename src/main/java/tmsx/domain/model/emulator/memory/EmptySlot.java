package tmsx.domain.model.emulator.memory;

import tmsx.domain.model.hardware.standard.z80.Z80Memory;

/**
 * Implements an empty memory slot, without writeable 
 * addresses, and returning 0xff (the default 'pull-up'
 * value) when reading any address.
 * 
 * @author tjitze
 *
 */
public class EmptySlot extends Z80Memory {

	public EmptySlot(String name) { super(name); }
	
	public byte rdByte(short addr) { return (byte)0x00; }
	
	public final void wrtByte(short addr, byte value) { }

	@Override
	public boolean isWritable(short addr) { return false; }
	
}