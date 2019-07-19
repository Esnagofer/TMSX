package esnagofer.msx.ide.emulator.core.application.javafx.main;

import java.io.File;
import java.util.concurrent.Executors;

import esnagofer.msx.ide.emulator.core.application.javafx.emulator.JavafxEmulatorFactory;
import esnagofer.msx.ide.emulator.core.application.javafx.emulator.JavafxEmulator;
import esnagofer.msx.ide.emulator.core.domain.model.emulator.Emulator;
import esnagofer.msx.ide.emulator.core.domain.model.emulator.EmulatorCartLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		MainController controller = new MainController();
		loader.setController(controller);
		Parent root = loader.load(getClass().getResourceAsStream("/fxml/main.fxml"));
		Scene scene = new Scene(root, 640, 480);
		stage.setTitle("FXML Welcome");
		stage.setScene(scene);
		stage.show();
		Emulator emulator = JavafxEmulatorFactory.valueOf(
			JavafxEmulator.valueOf(scene, controller.emulatorCanvas(), 2)
		);
		EmulatorCartLoader.loadRomWithLoaderFromThisFile(
			emulator, 
			new File("C:\\Users\\user.user-PC\\Desktop\\test.scroll.rom"), 
			"Flat"
		);
		Executors.newSingleThreadExecutor().execute(() -> {
			emulator.start(false);
		}); 
	}

	public static void run() {
		launch();
	}
	
}
