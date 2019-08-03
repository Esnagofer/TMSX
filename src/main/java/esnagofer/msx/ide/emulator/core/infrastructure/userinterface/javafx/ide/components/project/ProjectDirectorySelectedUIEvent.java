package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.project;

import java.io.File;

import esnagofer.msx.ide.lib.Validate;
import esnagofer.msx.ide.lib.userinterface.UIEvent;

public class ProjectDirectorySelectedUIEvent extends UIEvent {

	private File directoryProject;
	
	protected ProjectDirectorySelectedUIEvent(File directoryProject) {
		Validate.isNotNull(directoryProject);
		this.directoryProject = directoryProject;
	}

	public static ProjectDirectorySelectedUIEvent valueOf(File directoryProject) {
		return new ProjectDirectorySelectedUIEvent(directoryProject);
	}

	public File directoryProject() {
		return directoryProject;
	}
	
}
