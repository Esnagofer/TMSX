package tmsx.domain.model.emulator.cartridgeloaders;

import tmsx.domain.model.hardware.standard.z80.Z80Memory;

/**
 * Implements cartridge loader and slot logic for cartridges that are 
 * of type "Konami5" (see http://bifi.msxnet.org/msxnet/tech/megaroms).
 * 
 * @author tjitze.rienstra
 *
 */
public class Konami5Mapper extends BlockMapper implements CartridgeLoader {

	/**
	 * Instantiates a new konami 5 mapper.
	 *
	 * @param name the name
	 */
	public Konami5Mapper(String name) {
		super(name, (short)0x4000, (short)0x2000, 4, 32, 0, (short)0x1000);
	}

	/* (non-Javadoc)
	 * @see emu.cartridgeloaders.CartridgeLoader#getSlot()
	 */
	@Override
	public Z80Memory getSlot() {
		return this;
	}

}
