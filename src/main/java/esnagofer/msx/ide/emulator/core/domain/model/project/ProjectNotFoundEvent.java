package esnagofer.msx.ide.emulator.core.domain.model.project;

import esnagofer.msx.ide.lib.Validate;
import esnagofer.msx.ide.lib.domain.model.core.DomainEvent;

public class ProjectNotFoundEvent extends DomainEvent {

	private ProjectId projectId;
	
	protected ProjectNotFoundEvent(ProjectId projectId) {
		Validate.isNotNull(projectId);
		this.projectId = projectId;
	}
	
	public ProjectId project() {
		return projectId;
	}

	public static ProjectNotFoundEvent valueOf(ProjectId projectId) {
		return new ProjectNotFoundEvent(projectId);
	}

}
