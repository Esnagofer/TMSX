package esnagofer.msx.ide.emulator.core.application.service;

import javax.inject.Inject;

import esnagofer.msx.ide.emulator.core.application.usecase.OpenProjectCommand;
import esnagofer.msx.ide.emulator.core.domain.model.ide.Ide;
import esnagofer.msx.ide.emulator.core.domain.model.ide.IdeFactory;
import esnagofer.msx.ide.emulator.core.domain.model.project.Project;
import esnagofer.msx.ide.emulator.core.domain.model.project.ProjectId;
import esnagofer.msx.ide.emulator.core.domain.model.project.ProjectNotFoundEvent;
import esnagofer.msx.ide.emulator.core.domain.model.project.ProjectRepository;
import esnagofer.msx.ide.lib.Validate;
import esnagofer.msx.ide.lib.cqs.CommandExecutionHandler;
import esnagofer.msx.ide.lib.domain.model.core.DomainEventManager;

class OpenProjectCommandHandler implements CommandExecutionHandler<OpenProjectCommand> {

	private IdeFactory ideFactory;
	
	private ProjectRepository projectRepository;

	private DomainEventManager domainEventManager;
	
	public OpenProjectCommandHandler() {}

	@Inject
	public OpenProjectCommandHandler(
		IdeFactory ideFactory,
		ProjectRepository projectRepository,
		DomainEventManager domainEventManager
	) {
		Validate.isNotNull(ideFactory);
		Validate.isNotNull(projectRepository);
		Validate.isNotNull(domainEventManager);
		this.ideFactory = ideFactory;
		this.projectRepository = projectRepository;
		this.domainEventManager = domainEventManager;
	}
	
	@Override
	public void execute(OpenProjectCommand command) {
		Ide ide = ideFactory.get();
		ProjectId projectId = ProjectId.valueOf(command.projectId());
		if (!projectRepository.contains(projectId)) {
			domainEventManager.publish(ProjectNotFoundEvent.valueOf(projectId));
		} else {
			Project project = projectRepository.get(projectId);
			ide.assignCurrentProject(project);					
		}
	}

}
