package esnagofer.msx.ide.emulator.core.application.service;

import javax.inject.Inject;

import esnagofer.msx.ide.emulator.core.application.usecase.OpenProjectCommand;
import esnagofer.msx.ide.emulator.core.domain.model.ide.IdeFactory;
import esnagofer.msx.ide.emulator.core.domain.model.project.ProjectRepository;
import esnagofer.msx.ide.lib.cqs.CommandExecutionHandler;

class OpeProjectCommandHandler implements CommandExecutionHandler<OpenProjectCommand> {

	private IdeFactory ideFactory;

	public OpeProjectCommandHandler() {}

	
	@Inject
	public OpeProjectCommandHandler(
		IdeFactory ideFactory,
		ProjectRepository projectRepository
	) {
		this.ideFactory = ideFactory;
	}
	
	@Override
	public void execute(OpenProjectCommand command) {
		ideFactory.createIde();
	}

}
