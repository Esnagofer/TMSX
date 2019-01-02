package tmsx.domain.model.emulator.memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import tmsx.domain.model.hardware.z80.Z80Memory;
import tmsx.infrastructure.Tools;

/**
 * The Class ROMSlot.
 */
public class ROMSlot extends Z80Memory {

	/** The size. */
	private int size = 0x10000;
	
	/** The mem. */
	public final byte[] mem;
	
	/**
	 * Instantiates a new ROM slot.
	 *
	 * @param name the name
	 */
	public ROMSlot(String name) {
		super(name);
		mem = new byte[size];
	}
	
	/**
	 * Instantiates a new ROM slot.
	 *
	 * @param s the s
	 * @param name the name
	 */
	public ROMSlot(int s, String name) {
		super(name);
		this.size = s;
		mem = new byte[size];
	}
	
	/* (non-Javadoc)
	 * @see emu.memory.AbstractSlot#rdByte(short)
	 */
	public byte rdByte(short addr) {
		return (addr & 0xffff) < size? mem[addr & 0xffff]: (byte)0x00;
	}
	
	/* (non-Javadoc)
	 * @see emu.memory.AbstractSlot#wrtByte(short, byte)
	 */
	public final void wrtByte(short addr, byte value) {
		//mem[addr & 0xffff] = value;
	}

	/* (non-Javadoc)
	 * @see emu.memory.AbstractSlot#isWritable(short)
	 */
	@Override
	public boolean isWritable(short addr) {
		return false;
	}
	
	/**
	 * Load given file into memory, starting at given address. Note
	 * that this only works if this concrete class provides a writeable
	 * memory space.
	 *
	 * @param fileName the file name
	 * @param start the start
	 * @param xsize the xsize
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void load(String fileName, int start, int xsize) throws IOException {
		int oldStart = start;
		File file=new File(fileName);
		byte[] contents = new byte[xsize];
		
		try (FileInputStream in = new FileInputStream(file)) {
			if (in.read(contents) == -1) throw new RuntimeException("Wrong ROM file size");
			if (in.read() != -1) throw new RuntimeException("Wrong ROM file size");
			in.close();			
		}
		
		for (int i = 0; i < xsize; i++) { mem[start] = contents[i]; start++; }
		System.out.println("Wrote slot " + getName() + " from "+ Tools.toHexString((short)oldStart) + " to " + Tools.toHexString((short)start) + ". File: " + fileName);
	}

}
