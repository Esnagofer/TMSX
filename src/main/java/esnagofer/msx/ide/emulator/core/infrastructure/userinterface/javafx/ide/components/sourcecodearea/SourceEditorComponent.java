package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.sourcecodearea;

import java.util.function.IntFunction;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;

import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.BreakPoint;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.sourcecodearea.view.SourceCodeArea;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.sourcecodearea.view.SourceCodeAreaBreakPointManager;
import esnagofer.msx.ide.lib.Validate;
import esnagofer.msx.ide.lib.javafx.JavafxComponent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class SourceEditorComponent extends JavafxComponent {

	private SourceCodeArea sourceCodeArea;

	private StackPane stackPane;
	
	private SourceCodeAreaBreakPointManager breakPointManager;
	
	protected SourceEditorComponent(String text) {
		initRoot(text);
		initBreakPointManager();
	}
	
	private void initRoot(String text) {
		Validate.isNotNull(text);
		sourceCodeArea = SourceCodeArea.valueOf(text);
		stackPane = new StackPane(new VirtualizedScrollPane<>(sourceCodeArea));
		setRoot(stackPane);		
	}
	
	private void initBreakPointManager() {
		breakPointManager = new SourceCodeAreaBreakPointManager();
		IntFunction<Node> numberFactory = LineNumberFactory.get(sourceCodeArea);
		IntFunction<Node> arrowFactory = breakPointManager;
		IntFunction<Node> graphicFactory = editorLine -> {
			HBox hbox = new HBox(
				numberFactory.apply(editorLine),
				arrowFactory.apply(editorLine));
			hbox.setAlignment(Pos.CENTER_LEFT);
			return hbox;
		};
		sourceCodeArea.setParagraphGraphicFactory(graphicFactory);
	}

	public void addBreakPoint(BreakPoint breakPoint) {
		breakPointManager.addBreakPoint(breakPoint);
	}
	
	public void removeBreakPoint(BreakPoint breakPoint) {
		breakPointManager.removeBreakPoint(breakPoint);
	}
	
	public static void register(Scene scene) {
		Validate.isNotNull(scene);
        scene.getStylesheets().add(SourceEditorComponent.class.getResource("/css/z80sjasm-keywords.css").toExternalForm());		
	}
	
	public static SourceEditorComponent valueOf(String text) {
		return new SourceEditorComponent(text);
	}

	public static SourceEditorComponent valueOf() {
		return new SourceEditorComponent("");
	}

}
