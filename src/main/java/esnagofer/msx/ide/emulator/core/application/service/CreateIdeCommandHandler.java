package esnagofer.msx.ide.emulator.core.application.service;

import javax.inject.Inject;

import esnagofer.msx.ide.emulator.core.application.usecase.CreateIdeCommand;
import esnagofer.msx.ide.emulator.core.domain.model.ide.IdeFactory;
import esnagofer.msx.ide.lib.cqs.CommandExecutionHandler;

class CreateIdeCommandHandler implements CommandExecutionHandler<CreateIdeCommand> {

	private IdeFactory ideFactory;

	public CreateIdeCommandHandler() {}
	
	@Inject
	public CreateIdeCommandHandler(IdeFactory ideFactory) {
		this.ideFactory = ideFactory;
	}
	
	@Override
	public void execute(CreateIdeCommand command) {
		ideFactory.createIde();
	}

}
