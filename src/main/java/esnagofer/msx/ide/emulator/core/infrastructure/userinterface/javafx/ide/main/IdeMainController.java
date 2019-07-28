package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.main;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.project.ProjectDirectorySelectedUIEvent;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.project.ProjectDirectorySelector;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;

public class IdeMainController implements javafx.fxml.Initializable {
	
	private IdeMain application;
	
	@FXML
	private MenuItem menuProjectOpen;
	
	@FXML
	private TabPane tabPaneHardwareDebug;
	
	@FXML
	private MenuItem menuProjectCompile;
	
	@FXML
	private MenuItem menuProjectDebug;
	
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tabPaneHardwareDebug.setVisible(false);
		menuProjectCompile.setDisable(true);
		menuProjectDebug.setDisable(true);
	}
	
}
