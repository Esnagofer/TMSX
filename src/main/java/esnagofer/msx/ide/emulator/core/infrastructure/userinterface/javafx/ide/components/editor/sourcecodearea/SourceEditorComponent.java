package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.editor.sourcecodearea;

import java.util.Optional;
import java.util.function.IntFunction;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.TwoDimensional.Position;

import esnagofer.msx.ide.emulator.core.domain.model.project.SourceNode;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.BreakPointManager;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.editor.sourcecodearea.view.SourceCodeArea;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.editor.sourcecodearea.view.SourceCodeAreaBreakPointManager;
import esnagofer.msx.ide.lib.Validate;
import esnagofer.msx.ide.lib.javafx.JavafxComponent;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class SourceEditorComponent extends JavafxComponent {

	private SourceNode sourceNode;
	
	private SourceCodeArea sourceCodeArea;

	private StackPane stackPane;
	
	private SourceCodeAreaBreakPointManager breakPointManager;

	private VirtualizedScrollPane<?> sourceCodeScrollPane;
	
	protected SourceEditorComponent(SourceNode sourceNode) {
		Validate.isNotNull(sourceNode);
		this.sourceNode = sourceNode;
		initRoot(sourceNode.content());
		initBreakPointManager();
	}
	
	private void initRoot(Optional<String> text) {
		String finalText = "";
		if (text.isPresent()) {
			finalText = text.get();
		}
		sourceCodeArea = SourceCodeArea.valueOf(finalText);
//		sourceCodeArea.setDisable(true);
		sourceCodeScrollPane = new VirtualizedScrollPane<>(sourceCodeArea);
		stackPane = new StackPane(sourceCodeScrollPane);
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

	public BreakPointManager breakPointManager() {
		return breakPointManager;
	}

	public SourceNode sourceNode() {
		return sourceNode;
	}

	public void selectLine(Integer line) {
		Validate.isNotNull(line);
		Validate.isIntPositive(line);
		Position linePosition = sourceCodeArea.position(line - 1, 0);
		sourceCodeArea.moveTo(linePosition.toOffset());
		Platform.runLater(() -> {			
			sourceCodeScrollPane.scrollYToPixel(10 * line);
		});
	}
	
	public static void register(Scene scene) {
		Validate.isNotNull(scene);
        scene.getStylesheets().add(SourceEditorComponent.class.getResource("/css/z80sjasm-keywords.css").toExternalForm());		
	}
	
	public static SourceEditorComponent valueOf(SourceNode sourceNode) {
		return new SourceEditorComponent(sourceNode);
	}

}
