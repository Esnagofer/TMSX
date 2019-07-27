package esnagofer.msx.ide.emulator.core.domain.model.project;

import esnagofer.msx.ide.lib.domain.model.core.IdentityString;

public class ProjectId extends IdentityString {
	
	public ProjectId(String projectId) {
		super(projectId);
	}

	public static ProjectId valueOf(String projectId) {
		return new ProjectId(projectId);
	}
	
}
