package esnagofer.msx.ide.emulator.core.infrastructure.javafx.emulator;

import esnagofer.msx.ide.emulator.core.domain.model.emulator.components.keyboard.Keyboard;
import esnagofer.msx.ide.emulator.core.domain.model.emulator.components.screen.Screen;
import esnagofer.msx.ide.lib.Validate;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;

public class JavafxEmulator {

	private JavafxScreen screen;
	
	private JavafxKeyboard keyboard;
	
	private JavafxEmulator(Scene nodeToBindKeyboard, Canvas nodeToRenderScreen, Integer scale) {
		Validate.notNull("canvas is not set", nodeToRenderScreen);
		Validate.notNull("scale is not set", scale);
		screen = new JavafxScreen(nodeToRenderScreen, scale);
		keyboard = new JavafxKeyboard();
		nodeToBindKeyboard.setOnKeyPressed(keyboard::keyPressedEventListener);
		nodeToBindKeyboard.setOnKeyReleased(keyboard::keyReleasedEventListener);
	}

	public Screen screen() {
		return screen;
	}

	public Keyboard keyboard() {
		return keyboard;
	}

	public static JavafxEmulator valueOf(Scene scene, Canvas nodeToRenderScreen, Integer scale) {
		return new JavafxEmulator(scene, nodeToRenderScreen, scale);
	}

}
