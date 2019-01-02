package tmsx.domain.model.emulator.cartridgeloaders;

import tmsx.domain.model.hardware.z80.Z80Memory;

/**
 * Implements cartridge loader and slot logic for cartridges that are 
 * of type "Konami4" (see http://bifi.msxnet.org/msxnet/tech/megaroms).
 * 
 * @author tjitze.rienstra
 *
 */
public class Konami4Mapper extends BlockMapper implements CartridgeLoader {

	/**
	 * Instantiates a new konami 4 mapper.
	 *
	 * @param name the name
	 */
	public Konami4Mapper(String name) {
		super(name, (short)0x4000, (short)0x2000, 4, 32, 1, (short)0);
	}

	/* (non-Javadoc)
	 * @see emu.cartridgeloaders.CartridgeLoader#getSlot()
	 */
	@Override
	public Z80Memory getSlot() {
		return this;
	}

}
