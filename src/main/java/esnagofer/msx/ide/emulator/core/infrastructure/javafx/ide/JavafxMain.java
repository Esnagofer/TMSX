package esnagofer.msx.ide.emulator.core.infrastructure.javafx.ide;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavafxMain extends Application {
	
	private static JavafxMain instance;

	private static JavafxMainController controller;
	
	@Override
	public void start(Stage stage) throws Exception {		
		FXMLLoader loader = new FXMLLoader();
		controller = new JavafxMainController();
		loader.setController(controller);
		Parent root = loader.load(getClass().getResourceAsStream("/fxml/main.fxml"));
		Scene scene = new Scene(root, 640, 480);
		stage.setTitle("FXML Welcome");
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(event -> {
			Platform.exit();
			System.exit(0);
		});
	}

	public void run() {
		instance= this;
		launch();
	}
	
	public static JavafxMain instance() {
		return instance;
	}
	
	public static JavafxMainController controller() {
		return controller;
	}

}
