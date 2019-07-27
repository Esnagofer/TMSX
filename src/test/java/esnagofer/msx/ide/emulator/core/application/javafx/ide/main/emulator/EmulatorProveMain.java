package esnagofer.msx.ide.emulator.core.application.javafx.ide.main.emulator;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import esnagofer.msx.ide.emulator.core.domain.model.emulator.Emulator;
import esnagofer.msx.ide.emulator.core.domain.model.emulator.EmulatorCartLoader;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.emulator.JavafxEmulator;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.emulator.JavafxEmulatorFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EmulatorProveMain extends Application {

	private ExecutorService executorService;

	private EmulatorProveController controller;

	private Emulator emulator;

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		controller = new EmulatorProveController();
		loader.setController(controller);
		Parent root = loader.load(getClass().getResourceAsStream("/fxml/emulator.text.fxml"));
		Scene scene = new Scene(root, 640, 480);
		stage.setTitle("FXML Welcome");
		stage.setScene(scene);
		stage.show();
		emulator = JavafxEmulatorFactory.valueOf(
			JavafxEmulator.valueOf(scene, controller.emulatorCanvas(), 2)
		);
		EmulatorCartLoader.loadRomWithLoaderFromThisFile(
			emulator, 
			new File("C:\\Users\\user.user-PC\\Desktop\\test.scroll.rom"), 
			"Flat"
		);
		executorService = Executors.newSingleThreadExecutor();
		executorService.execute(() -> {
			emulator.start(false);
		}); 
		stage.setOnCloseRequest(event -> {
			Platform.exit();
			System.exit(0);
		});
	}

	public static void run() {
		launch();
	}

}
