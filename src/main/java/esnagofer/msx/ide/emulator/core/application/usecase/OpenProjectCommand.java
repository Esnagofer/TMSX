package esnagofer.msx.ide.emulator.core.application.usecase;

import esnagofer.msx.ide.lib.cqs.Command;

public class OpenProjectCommand extends Command {

	private String projectId;
	
	protected OpenProjectCommand(String projectId) {
		this.projectId = projectId;
	}
	
	public String projectId() {
		return projectId;
	}

	public static OpenProjectCommand valueOf(String projectId) {
		return new OpenProjectCommand(projectId);
	}
	
}
