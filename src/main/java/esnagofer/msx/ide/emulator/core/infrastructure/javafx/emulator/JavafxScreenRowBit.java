package esnagofer.msx.ide.emulator.core.infrastructure.javafx.emulator;

import java.util.HashMap;
import java.util.Map;

import esnagofer.msx.ide.lib.Validate;

/**
 * The Class RowBit.
 */
class JavafxScreenRowBit {

	private static Map<Integer, JavafxScreenRowBit> cache = new HashMap<>();
	
	/** The row. */
	private Integer row;
	
	/** The bit. */
	private Integer bit;

	/**
	 * Instantiates a new row bit.
	 *
	 * @param row the row
	 * @param bit the bit
	 */
	protected JavafxScreenRowBit(Integer row, Integer bit) {
		Validate.isNotNull(row);
		Validate.isNotNull(bit);
		this.row = row;
		this.bit = bit;
	}
	
	private static int hashCode(Integer row, Integer bit) {
		final int prime = 31;
		int result = 1;
		result = prime * result + bit;
		result = prime * result + row;
		return result;		
	}
	
	@Override
	public int hashCode() {
		return hashCode(row, bit);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JavafxScreenRowBit other = (JavafxScreenRowBit) obj;
		if (bit != other.bit)
			return false;
		if (row != other.row)
			return false;
		return true;
	}

	/**
	 * Row.
	 *
	 * @return the int
	 */
	public int row() {
		return row;
	}

	/**
	 * Bit.
	 *
	 * @return the int
	 */
	public int bit() {
		return bit;
	}

	/**
	 * Value of.
	 *
	 * @param row the row
	 * @param bit the bit
	 * @return the row bit
	 */
	public static JavafxScreenRowBit valueOf(Integer row, Integer bit) {
		JavafxScreenRowBit rowBit = cache.get(JavafxScreenRowBit.hashCode(row, bit));
		if (rowBit == null) {
			rowBit = new JavafxScreenRowBit(row, bit);
		}
		return rowBit;
	}
	
}
