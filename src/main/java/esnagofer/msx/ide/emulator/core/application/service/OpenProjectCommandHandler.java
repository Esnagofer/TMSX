package esnagofer.msx.ide.emulator.core.application.service;

import javax.inject.Inject;

import esnagofer.msx.ide.emulator.core.application.usecase.OpenProjectCommand;
import esnagofer.msx.ide.emulator.core.domain.model.ide.Ide;
import esnagofer.msx.ide.emulator.core.domain.model.project.Project;
import esnagofer.msx.ide.emulator.core.domain.model.project.ProjectId;
import esnagofer.msx.ide.emulator.core.domain.model.project.ProjectNotFoundEvent;
import esnagofer.msx.ide.emulator.core.domain.model.project.ProjectRepository;
import esnagofer.msx.ide.lib.Validate;
import esnagofer.msx.ide.lib.cqs.CommandExecutionHandler;
import esnagofer.msx.ide.lib.domain.model.core.DomainEventManager;

class OpenProjectCommandHandler implements CommandExecutionHandler<OpenProjectCommand> {

	private Ide ide;
	
	private ProjectRepository projectRepository;

	private DomainEventManager domainEventManager;
	
	public OpenProjectCommandHandler() {}

	@Inject
	public OpenProjectCommandHandler(
		Ide ide,
		ProjectRepository projectRepository,
		DomainEventManager domainEventManager
	) {
		Validate.isNotNull(ide);
		Validate.isNotNull(projectRepository);
		Validate.isNotNull(domainEventManager);
		this.ide = ide;
		this.projectRepository = projectRepository;
		this.domainEventManager = domainEventManager;
	}
	
	@Override
	public void execute(OpenProjectCommand command) {
		ProjectId projectId = ProjectId.valueOf(command.projectId());
		if (!projectRepository.contains(projectId)) {
			domainEventManager.publish(ProjectNotFoundEvent.valueOf(projectId));
		} else {
			Project project = projectRepository.get(projectId);
			ide.assignCurrentProject(project);					
		}
	}

}
