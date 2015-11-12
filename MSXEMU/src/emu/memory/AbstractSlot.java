package emu.memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import emu.Tools;

/**
 * 
 * An object of this class holds a 64 KB memory space.
 * It provides read/write operations as well as some
 * management functionality.
 * 
 * @author tjitze
 *
 */

public abstract class AbstractSlot { // implements IMemory {

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
	 * Read a byte from given address (provided by lsb and msb bytes)
	 * 
	 * @param lsb LSB of address
	 * @param msb MSB of address
	 * @return value Value
	 */
	public final byte rdByte(byte lsb, byte msb) {
		return rdByte((short)((msb << 8) | (lsb & 0xff)));
	}
	
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
	 * Load given file into memory, starting at given address
	 * 
	 * @param fileName
	 * @param addr
	 * @throws IOException
	 */
	public void load(String fileName, short addr) throws IOException {
		File file=new File(fileName);
		int size = (int)file.length();
		byte[] contents=new byte[size];
		FileInputStream in=new FileInputStream(file);
		in.read(contents);
		in.close();
		for (int i = 0; i < size; i++) {
			wrtByte(addr, contents[i]);
			addr++;
		}
	}
	
	//@Override
	public int readByte(int address) {
		return this.rdByte((short)(address & 0xffff)) & 0xff;
	}

	//@Override
	public int readWord(int address) {
		return this.readWordLH((short)(address & 0xffff)) & 0xffff;
	}

	//@Override
	public void writeByte(int address, int data) {
		this.wrtByte((short)(address & 0xffff), (byte)(data & 0xff));
	}

	//@Override
	public void writeWord(int address, int data) {
		this.writeShortLH((short)(address & 0xffff), (short)(data & 0xffff));
	}

	//public abstract byte[] getArray();


}







