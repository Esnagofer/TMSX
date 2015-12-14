package emu.cartridgeloaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import emu.Tools;
import emu.memory.AbstractSlot;
import emu.memory.ROMSlot;

/**
 * Implements cartridge loader and slot logic for cartridges that are 
 * directly mapped at the area 0x4000-0xC000. This works for most 16K/32K
 * roms.
 * 
 * @author tjitze.rienstra
 *
 */
public class FlatMapper extends ROMSlot implements CartridgeLoader {

	public FlatMapper(String name) {
		super(name);
	}

	@Override
	public void load(String fileName, int romSize) throws IOException {
		load(fileName, 0x4000, romSize);
	}

	@Override
	public AbstractSlot getSlot() {
		return this;
	}

}
