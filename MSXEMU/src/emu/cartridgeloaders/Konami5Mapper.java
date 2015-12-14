package emu.cartridgeloaders;

import emu.memory.AbstractSlot;

/**
 * Implements cartridge loader and slot logic for cartridges that are 
 * of type "Konami5" (see http://bifi.msxnet.org/msxnet/tech/megaroms).
 * 
 * @author tjitze.rienstra
 *
 */
public class Konami5Mapper extends BlockMapper implements CartridgeLoader {

	public Konami5Mapper(String name) {
		super(name, (short)0x4000, (short)0x2000, 4, 32, 0, (short)0x1000);
	}

	@Override
	public AbstractSlot getSlot() {
		return this;
	}

}
