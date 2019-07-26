package esnagofer.msx.ide.emulator.core.application.usecase;

import esnagofer.msx.ide.lib.cqs.Command;

public class CreateIdeCommand extends Command {
	
	public static CreateIdeCommand valueOf() {
		return new CreateIdeCommand();
	}
	
}
