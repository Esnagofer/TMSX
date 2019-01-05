package esnagofer.msx.ide.emulator.core.domain.model.hardware.memory;

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

public interface Memory {

	/**
	 * Abstract method for reading a byte at given address.
	 * 
	 * @param addr Address
	 * @return Value
	 */
	public byte rdByte(short addr);

	/**
	 * Abstract method for writing a byte at given address.
	 * 
	 * @param addr Address
	 * @param value Value
	 */
	public void wrtByte(short addr, byte value);

	/**
	 * Returns true if given address can be written to (i.e. is RAM as opposed to ROM).
	 *  
	 *
	 * @param addr the addr
	 * @return true, if is writable
	 */
	public boolean isWritable(short addr);

	/**
	 * Write a byte at given address (provided by lsb and msb bytes).
	 *
	 * @param lsb LSB of address
	 * @param msb MSB of address
	 * @param value Value
	 */
	public void writeByte(byte lsb, byte msb, byte value);
	
	/**
	 * Write a short at given address (in LH order).
	 *
	 * @param addr Address
	 * @param word Value
	 */
	public void writeShortLH(short addr, short word);

	/**
	 * Read a short at given address (LH order).
	 *
	 * @param addr the addr
	 * @return the short
	 */
	public short readWordLH(short addr);
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();

}
