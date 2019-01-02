package tmsx.domain.model.emulator.memory;

import tmsx.domain.model.hardware.standard.z80.Z80Memory;

/**
 * The Class RAMSlot.
 */
public class RAMSlot extends Z80Memory {

	/** The size. */
	private int size = 0xFFFF + 1;
	
	/** The mem. */
	public final byte[] mem;
	
	/**
	 * Instantiates a new RAM slot.
	 *
	 * @param name the name
	 */
	public RAMSlot(String name) {
		super(name);
		mem = new byte[size];
		//for (int i = 0; i < size; i++) mem[i] = (byte)0xf0;
	}
	
	/**
	 * Instantiates a new RAM slot.
	 *
	 * @param s the s
	 * @param name the name
	 */
	public RAMSlot(int s, String name) {
		super(name);
		this.size = s;
		mem = new byte[size];
		//for (int i = 0; i < size; i++) mem[i] = (byte)0xf0;
	}
	
	/* (non-Javadoc)
	 * @see emu.memory.AbstractSlot#rdByte(short)
	 */
	public byte rdByte(short addr) {
		return mem[addr & 0xffff];
	}
	
	/* (non-Javadoc)
	 * @see emu.memory.AbstractSlot#wrtByte(short, byte)
	 */
	public final void wrtByte(short addr, byte value) {
		mem[addr & 0xffff] = value;
	}

	/* (non-Javadoc)
	 * @see emu.memory.AbstractSlot#isWritable(short)
	 */
	@Override
	public boolean isWritable(short addr) {
		return true;
	}
	
	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
}
