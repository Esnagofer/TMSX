package esnagofer.msx.ide.emulator.core.domain.model.emulator.components.cartridgeloaders;

import java.io.IOException;

import esnagofer.msx.ide.emulator.core.domain.model.emulator.components.memory.Memory;
import esnagofer.msx.ide.emulator.core.domain.model.emulator.components.memory.RomMemory;

/**
 * Implements cartridge loader and slot logic for cartridges that are 
 * directly mapped at the area 0x4000-0xC000. This works for most 16K/32K
 * roms.
 * 
 * @author tjitze.rienstra
 *
 */
public class FlatMapper extends RomMemory implements CartridgeLoader {

	/**
	 * Instantiates a new flat mapper.
	 *
	 * @param name the name
	 */
	public FlatMapper(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see emu.cartridgeloaders.CartridgeLoader#load(java.lang.String, int)
	 */
	@Override
	public void load(String fileName, int romSize) throws IOException {
		load(fileName, 0x4000, romSize);
	}

	/* (non-Javadoc)
	 * @see emu.cartridgeloaders.CartridgeLoader#getSlot()
	 */
	@Override
	public Memory getSlot() {
		return this;
	}

}
