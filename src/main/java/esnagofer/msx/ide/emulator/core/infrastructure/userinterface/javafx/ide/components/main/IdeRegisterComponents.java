package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.main;

import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.sourcecodearea.SourceEditorComponent;
import javafx.scene.Scene;

public class IdeRegisterComponents {

	public static void inScene(Scene scene) {
		SourceEditorComponent.register(scene);
	}

}
