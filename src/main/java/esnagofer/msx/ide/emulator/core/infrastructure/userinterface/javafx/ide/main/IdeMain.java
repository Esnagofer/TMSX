package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.main;

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
	
	private IdeMain instance;

	private IdeMainController controller;

	private Stage stage;
	
	private static UIEventManager eventManager;
	
	@Inject
	public IdeMain(UIEventManager eventManager) {
		super();
		Validate.isNotNull(eventManager);
		this.eventManager = eventManager;
	}
	
	public IdeMain() {}
	
	@Override
	public void start(Stage stage) throws Exception {	
		Validate.isNotNull(stage);
		this.stage = stage;
		FXMLLoader loader = new FXMLLoader();
		controller = new IdeMainController(this);
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
	
	public IdeMain instance() {
		return instance;
	}
	
	public IdeMainController controller() {
		return controller;
	}

	public Stage stage() {
		return stage;
	}
	
	public UIEventManager eventManager() {
		return eventManager;
	}
	
}
