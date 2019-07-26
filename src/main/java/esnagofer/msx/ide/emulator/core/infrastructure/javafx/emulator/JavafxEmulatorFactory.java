package esnagofer.msx.ide.emulator.core.infrastructure.javafx.emulator;

import esnagofer.msx.ide.emulator.core.domain.model.emulator.Emulator;
import esnagofer.msx.ide.emulator.core.domain.model.emulator.components.memory.RamMemory;
import esnagofer.msx.ide.emulator.core.domain.model.emulator.components.tms9918a.TMS9918A;
import esnagofer.msx.ide.lib.Validate;

public class JavafxEmulatorFactory {

	public static Emulator valueOf(
		JavafxEmulator javafxEmulator
	) {
		Validate.isNotNull(javafxEmulator);
		return Emulator.builder()
			.withKeyboard(javafxEmulator.keyboard())
			.withVdp(
				TMS9918A.newInstance(
					new RamMemory(0xFFFF, "vram"), 
					javafxEmulator.screen()
				)
			)
		.build();
	}

}
