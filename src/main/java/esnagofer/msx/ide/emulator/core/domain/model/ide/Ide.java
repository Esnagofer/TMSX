package esnagofer.msx.ide.emulator.core.domain.model.ide;

import java.util.Optional;

import esnagofer.msx.ide.emulator.core.domain.model.project.Project;
import esnagofer.msx.ide.lib.Validate;
import esnagofer.msx.ide.lib.domain.model.core.DomainEventManager;

public class Ide {

	private IdeStatus status;
	
	private Project currentProject;
	
	private DomainEventManager domainEventManager;
	
	protected Ide(DomainEventManager domainEventManager) {
		Validate.notNull("domainEventManager", domainEventManager);
		this.domainEventManager = domainEventManager;
		status = IdeStatus.ID_EST_INITIALIZED;
	}
	
	private void checkStatus() {
		Validate.isTrue("Ide is not OPEN", status.equals(IdeStatus.ID_EST_INITIALIZED));
	}
	
	public void assignCurrentProject(Project currentProject) {
		Validate.notNull("currentProject", currentProject);
		checkStatus();
		this.currentProject = currentProject;
		domainEventManager.publish(new IdeCurrentProjectAssignedEvent(currentProject));
	}
	
	public void close() {
		checkStatus();
		status = IdeStatus.ID_EST_CLOSED;
		domainEventManager.publish(new IdeClosedEvent());
	}
	
	public Optional<Project> currentProject() {
		return Optional.ofNullable(currentProject);
	}

	static Ide valueOf(DomainEventManager domainEventManager) {
		return new Ide(domainEventManager);
	}
	
}
