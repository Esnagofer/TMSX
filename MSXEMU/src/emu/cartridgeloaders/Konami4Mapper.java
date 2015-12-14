package emu.cartridgeloaders;

import emu.memory.AbstractSlot;

/**
 * Implements cartridge loader and slot logic for cartridges that are 
 * of type "Konami4" (see http://bifi.msxnet.org/msxnet/tech/megaroms).
 * 
 * @author tjitze.rienstra
 *
 */
public class Konami4Mapper extends BlockMapper implements CartridgeLoader {

	public Konami4Mapper(String name) {
		super(name, (short)0x4000, (short)0x2000, 4, 32, 1, (short)0);
	}

	@Override
	public AbstractSlot getSlot() {
		return this;
	}

}
