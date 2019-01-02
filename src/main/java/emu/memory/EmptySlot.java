package emu.memory;

/**
 * Implements an empty memory slot, without writeable 
 * addresses, and returning 0xff (the default 'pull-up'
 * value) when reading any address.
 * 
 * @author tjitze
 *
 */
public class EmptySlot extends AbstractSlot {

	public EmptySlot(String name) { super(name); }
	
	public byte rdByte(short addr) { return (byte)0x00; }
	
	public final void wrtByte(short addr, byte value) { }

	@Override
	public boolean isWritable(short addr) { return false; }
	
}