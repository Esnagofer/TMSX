package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.project;

import java.io.File;
import java.util.Optional;

import esnagofer.msx.ide.lib.Validate;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ProjectDirectorySelector {

	private static ProjectDirectorySelector INSTANCE;
	
	private DirectoryChooser directoryChooser;
	
	private Stage stage;	
	
	private File lastDirectory;
	
	protected ProjectDirectorySelector(Stage stage) {
		Validate.isNotNull(stage);
		this.directoryChooser = new DirectoryChooser();
        this.stage = stage;
	}
	
	public Optional<File> select() {
		directoryChooser.setInitialDirectory(lastDirectory);
		File selectedDirectory = directoryChooser.showDialog(stage);
		lastDirectory = selectedDirectory;
		return Optional.ofNullable(selectedDirectory);
	}

	public static ProjectDirectorySelector valueOf(Stage stage) {
		if (INSTANCE == null) {
			INSTANCE = new ProjectDirectorySelector(stage);
		}
		return INSTANCE;
	}

}
