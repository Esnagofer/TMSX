package esnagofer.msx.ide.emulator.core.domain.model.project;

import esnagofer.msx.ide.lib.domain.model.core.Aggregate;

public class Project extends Aggregate<ProjectId> {

	public static final String VERSION = "v1";
	
	public Project(ProjectId id) {
		super(id);
	}

}
