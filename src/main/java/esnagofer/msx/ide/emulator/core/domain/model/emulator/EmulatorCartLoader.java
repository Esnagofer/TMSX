package esnagofer.msx.ide.emulator.core.domain.model.emulator;

import java.io.File;
import java.util.prefs.Preferences;

import esnagofer.msx.ide.emulator.core.domain.model.components.cartridgeloaders.CartridgeLoader;
import esnagofer.msx.ide.emulator.core.domain.model.components.cartridgeloaders.CartridgeLoaderRegistry;
import esnagofer.msx.ide.emulator.core.domain.model.components.cartridgeloaders.FlatMapper;
import esnagofer.msx.ide.emulator.core.domain.model.components.memory.RomMemory;

public class EmulatorCartLoader {

	private static void load(Emulator emulator, File cartFile, CartridgeLoader cartLoader) {
		try {
			emulator.setSlot(Emulator.SLOT_0, defaultBios());
			cartLoader.load(cartFile.getAbsolutePath(), (int)cartFile.length());
			emulator.setSlot(Emulator.SLOT_1, cartLoader.getSlot());
			emulator.reset();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static RomMemory defaultBios() {
		RomMemory bios = new RomMemory(0xC000, "system");
		Preferences prefs = Preferences.userRoot().node("MSXEMU");
		String romFile = prefs.get("msx_system_rom", "-");
		try {
			bios.load(romFile, (short)0x0000, 0x8000);
			return bios;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void loadRomFromThisFile(Emulator emulator, File file) {
		if (file.length() > 0x8000) {
			throw new IllegalStateException("Invalid cart file size");
		} else {
			CartridgeLoader loader = new FlatMapper("cart1");
			load(emulator, file, loader);
		}
	}


	public static void loadRomWithLoaderFromThisFile(Emulator emulator, File cartFile, String cartLoaderName) {
		CartridgeLoader loader = CartridgeLoaderRegistry.getInstance(cartLoaderName, "cart1");
		load(emulator, cartFile, loader);
	}
	
}
