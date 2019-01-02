package tmsx.domain.model.emulator.cartridgeloaders;

import java.io.IOException;

import tmsx.domain.model.emulator.memory.ROMSlot;
import tmsx.domain.model.hardware.z80.Z80Memory;

/**
 * Implements cartridge loader and slot logic for cartridges that are 
 * directly mapped at the area 0x4000-0xC000. This works for most 16K/32K
 * roms.
 * 
 * @author tjitze.rienstra
 *
 */
public class FlatMapper extends ROMSlot implements CartridgeLoader {

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
	public Z80Memory getSlot() {
		return this;
	}

}
