package emu.cartridgeloaders;

import java.io.IOException;

import emu.memory.AbstractSlot;

/**
 * A cartridge loader implements a load method, to load ROM images,
 * and a method that afterwards returns a slot object that implements
 * the necessary mapper logic.
 * 
 * @author tjitze.rienstra
 *
 */
public interface CartridgeLoader {

	public void load(String fileName, int romSize) throws IOException;
	
	public AbstractSlot getSlot();
}
