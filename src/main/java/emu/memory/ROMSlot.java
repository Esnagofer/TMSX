package emu.memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import emu.Tools;

public class ROMSlot extends AbstractSlot {

	private int size = 0x10000;
	
	public final byte[] mem;
	
	public ROMSlot(String name) {
		super(name);
		mem = new byte[size];
	}
	
	public ROMSlot(int s, String name) {
		super(name);
		this.size = s;
		mem = new byte[size];
	}
	
	public byte rdByte(short addr) {
		return (addr & 0xffff) < size? mem[addr & 0xffff]: (byte)0x00;
	}
	
	public final void wrtByte(short addr, byte value) {
		//mem[addr & 0xffff] = value;
	}

	@Override
	public boolean isWritable(short addr) {
		return false;
	}
	
	/**
	 * Load given file into memory, starting at given address. Note
	 * that this only works if this concrete class provides a writeable
	 * memory space.
	 * 
	 * @param fileName
	 * @param addr
	 * @throws IOException
	 */
	public void load(String fileName, int start, int xsize) throws IOException {
		int oldStart = start;
		File file=new File(fileName);
		byte[] contents = new byte[xsize];
		FileInputStream in = new FileInputStream(file);
		if (in.read(contents) == -1) throw new RuntimeException("Wrong ROM file size");
		if (in.read() != -1) throw new RuntimeException("Wrong ROM file size");
		in.close();
		for (int i = 0; i < xsize; i++) { mem[start] = contents[i]; start++; }
		System.out.println("Wrote slot " + getName() + " from "+ Tools.toHexString((short)oldStart) + " to " + Tools.toHexString((short)start) + ". File: " + fileName);
	}

}
