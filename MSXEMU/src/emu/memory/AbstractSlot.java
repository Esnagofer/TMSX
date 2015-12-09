package emu.memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import emu.Tools;

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
 *
 */

public abstract class AbstractSlot {

	/**
	 * Abstract method for reading a byte at given address.
	 * 
	 * @param addr Address
	 * @return Value
	 */
	public abstract byte rdByte(short addr);

	/**
	 * Abstract method for writing a byte at given address.
	 * 
	 * @param addr Address
	 * @param value Value
	 */
	public abstract void wrtByte(short addr, byte value);

	/**
	 * Returns true if given address can be written to (i.e. is RAM as opposed to ROM).
	 *  
	 * @param addr
	 */
	public abstract boolean isWritable(short addr);

	/**
	 * Write a byte at given address (provided by lsb and msb bytes)
	 * 
	 * @param lsb LSB of address
	 * @param msb MSB of address
	 * @param value Value
	 */
	public final void writeByte(byte lsb, byte msb, byte value) {
		wrtByte((short)((msb << 8) | (lsb & 0xff)), value);
	}

	/**
	 * Write a short at given address (in LH order)
	 * 
	 * @param addr Address
	 * @param word Value
	 */
	public void writeShortLH(short addr, short word) {
		wrtByte(addr, Tools.getLSB(word));
		wrtByte((short)(addr + 1), Tools.getMSB(word));
	}

	/**
	 * Read a short at given address (LH order)
	 * 
	 * @param addr
	 * @return
	 */
	public short readWordLH(short addr) {
		byte fst = rdByte(addr);
		byte snd = rdByte((short)(addr+1));
		return (short) ((fst & 0xff) | (snd << 8));
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
	public void load(String fileName, short start, int xsize) throws IOException {
		File file=new File(fileName);
		byte[] contents = new byte[xsize];
		FileInputStream in = new FileInputStream(file);
		if (in.read(contents) == -1) throw new RuntimeException("Wrong ROM file size");
		if (in.read() != -1) throw new RuntimeException("Wrong ROM file size");
		in.close();
		for (int i = 0; i < xsize; i++) wrtByte(start++, contents[i]);
	}

}







