package esnagofer.msx.ide.emulator.core.domain.model.ide;

import esnagofer.msx.ide.emulator.core.domain.model.project.Project;
import esnagofer.msx.ide.lib.Validate;
import esnagofer.msx.ide.lib.domain.model.core.DomainEvent;

public class IdeCurrentProjectAssignedEvent extends DomainEvent {

	private Project project;
	
	public IdeCurrentProjectAssignedEvent(Project project) {
		Validate.isNotNull(project);
		this.project = project;
	}
	
	public Project project() {
		return project;
	}

}
