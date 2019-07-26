package esnagofer.msx.ide.emulator.core.infrastructure;

import javax.inject.Inject;
import javax.inject.Named;

import esnagofer.msx.ide.emulator.core.application.usecase.CreateIdeCommand;
import esnagofer.msx.ide.lib.Validate;
import esnagofer.msx.ide.lib.cqs.CommandExecutionHandler;

@Named
public class ApplicationBoot {

	private CommandExecutionHandler<CreateIdeCommand> createIdeCommandHandler;
	
	@Inject
	public ApplicationBoot(CommandExecutionHandler<CreateIdeCommand> createIdeCommandHandler) {
		Validate.isNotNull(createIdeCommandHandler);
		this.createIdeCommandHandler = createIdeCommandHandler;
	}

	public void run() {
		createIdeCommandHandler.execute(CreateIdeCommand.valueOf());
	}
	
}
