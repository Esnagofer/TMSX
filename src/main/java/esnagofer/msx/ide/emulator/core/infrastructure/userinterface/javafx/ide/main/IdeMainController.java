package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.main;

import java.io.File;
import java.util.Optional;

import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.project.ProjectDirectorySelectedUIEvent;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.project.ProjectDirectorySelector;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class IdeMainController {
	
	private IdeMain application;
	
	@FXML
	private MenuItem menuProjectOpen;

	public IdeMainController(IdeMain application) {
        this.application = application;
	}
	
	@FXML
	public void menuProjectOpenAction(Event e) {
		Optional<File> projectDirectory = ProjectDirectorySelector.valueOf(application.stage()).select();
		if (projectDirectory.isPresent()) {
			application.eventManager().publish(
				ProjectDirectorySelectedUIEvent.valueOf(projectDirectory.get())
			);
		}
	}
	
}
