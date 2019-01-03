/*
 * 
 */
package msx.emulator.core.domain.model.hardware.keyboard;

/**
 * The Interface Keyboard.
 */
public interface Keyboard {
	
	/**
	 * Stop key pressed.
	 */
	public void stopKeyPressed();
	
	/**
	 * Stop key depressed.
	 */
	public void stopKeyDepressed();

	/**
	 * Gets the row value.
	 *
	 * @param row the row
	 * @return the row value
	 */
	public byte getRowValue(int row);

	/**
	 * Sets the capslock.
	 *
	 * @param state the new capslock
	 */
	public void setCapslock(boolean state);

}
