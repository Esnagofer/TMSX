package esnagofer.msx.ide.emulator.core.application.javafx.ide.main;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import esnagofer.msx.ide.emulator.core.application.javafx.emulator.JavafxEmulator;
import esnagofer.msx.ide.emulator.core.application.javafx.emulator.JavafxEmulatorFactory;
import esnagofer.msx.ide.emulator.core.domain.model.emulator.Emulator;
import esnagofer.msx.ide.emulator.core.domain.model.emulator.EmulatorCartLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class IdeMain extends Application {

	private ExecutorService executorService;

	private IdeMainController controller;

	private Emulator emulator;

	private void terminateEmulator(Emulator emulator) {
		emulator.terminate();
		executorService.shutdown();
		try {
			executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		controller = new IdeMainController();
		loader.setController(controller);
		Parent root = loader.load(getClass().getResourceAsStream("/fxml/main.fxml"));
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
			terminateEmulator(emulator);
			Platform.exit();
			System.exit(0);
		});
	}

	public static void run() {
		launch();
	}

}
