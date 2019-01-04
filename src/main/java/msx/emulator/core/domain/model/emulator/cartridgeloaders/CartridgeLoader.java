package msx.emulator.core.domain.model.emulator.cartridgeloaders;

import java.io.IOException;

import msx.emulator.core.domain.model.hardware.memory.Memory;

/**
 * A cartridge loader implements a load method, to load ROM images,
 * and a method that afterwards returns a slot object that implements
 * the necessary mapper logic.
 * 
 * @author tjitze.rienstra
 *
 */
public interface CartridgeLoader {

	/**
	 * Load.
	 *
	 * @param fileName the file name
	 * @param romSize the rom size
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void load(String fileName, int romSize) throws IOException;
	
	/**
	 * Gets the slot.
	 *
	 * @return the slot
	 */
	public Memory getSlot();
}