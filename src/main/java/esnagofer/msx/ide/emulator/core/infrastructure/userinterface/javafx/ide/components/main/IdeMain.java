package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.main;

import javax.inject.Inject;

import esnagofer.msx.ide.lib.Validate;
import esnagofer.msx.ide.lib.userinterface.UIEventManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class IdeMain extends Application {
	
	private IdeMainController controller;
	
	private IdeMain instance;

	private Stage stage;
	
	private static UIEventManager eventManager;
	
	@Inject
	public IdeMain(
		UIEventManager eventManager
	) {
		Validate.isNotNull(eventManager);
		this.eventManager = eventManager;
	}
	
	public IdeMain() {}
	
	@Override
	public void start(Stage stage) throws Exception {	
		Validate.isNotNull(stage);
		this.stage = stage;
		FXMLLoader loader = new FXMLLoader();
		controller = new IdeMainController(eventManager, stage);
		loader.setController(controller);
		Parent root = loader.load(getClass().getResourceAsStream("/fxml/main.fxml"));
		Scene scene = new Scene(root, 640, 480);
		IdeRegisterComponents.inScene(scene);
		controller.init(scene);
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
	
	public IdeMain instance() {
		return instance;
	}
	
	public IdeMainController controller() {
		return controller;
	}

	public Stage stage() {
		return stage;
	}
	
	public static UIEventManager eventManager() {
		return eventManager;
	}
	
}
