package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.editor.tab;

import java.util.HashMap;
import java.util.Map;

import esnagofer.msx.ide.emulator.core.domain.model.project.SourceNode;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.BreakPoint;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.editor.sourcecodearea.SourceEditorComponent;
import esnagofer.msx.ide.lib.Validate;
import esnagofer.msx.ide.lib.javafx.JavafxComponent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class SourceTabPaneComponent extends JavafxComponent {

	private TabPane sourceTabPane;
	
	private Map<SourceNode,Tab> sourceNodeTabMap = new HashMap<>();

	private Map<SourceNode,SourceEditorComponent> sourceNodeEditorMap = new HashMap<>();

	protected SourceTabPaneComponent(TabPane sourceTabPane) {
		Validate.isNotNull(sourceTabPane);
		this.sourceTabPane = sourceTabPane;
		setRoot(sourceTabPane);
	}

	public static SourceTabPaneComponent valueOf(TabPane sourceTabPane) {
		return new SourceTabPaneComponent(sourceTabPane);
	}
	
	public void focusTab(SourceNode sourceNode) {
		if (sourceNodeTabMap.containsKey(sourceNode)) {
			sourceTabPane.getSelectionModel().select(sourceNodeTabMap.get(sourceNode));
		}
	}
	
	public void selectTab(SourceNode sourceNode, Integer line) {
		selectTab(sourceNode);
		sourceNodeEditorMap.get(sourceNode).selectLine(line);			
	}
	
	public void selectTab(SourceNode sourceNode) {
		Validate.isNotNull(sourceNode);
		if (!sourceNodeTabMap.containsKey(sourceNode)) {
			SourceEditorComponent sourceEditorComponent = SourceEditorComponent.valueOf(sourceNode);
			sourceEditorComponent.breakPointManager().addBreakPoint(BreakPoint.valueOfEnabled(10));
			sourceEditorComponent.breakPointManager().addBreakPoint(BreakPoint.valueOfDisabled(11));
			sourceEditorComponent.breakPointManager().addBreakPoint(BreakPoint.valueOfInvalid(12));
			Tab tabdata = new Tab();
			Label tabLabel = new Label(sourceNode.name());
			tabdata.setGraphic(tabLabel);
			tabdata.setClosable(true);
			tabdata.setContent(sourceEditorComponent.root());
			sourceTabPane.getTabs().add(tabdata);
			sourceNodeTabMap.put(sourceNode, tabdata);	
			sourceNodeEditorMap.put(sourceNode, sourceEditorComponent);
			tabdata.setOnClosed(event -> {
				sourceNodeTabMap.remove(sourceNode);				
				sourceNodeEditorMap.remove(sourceNode);
			});
		}
		focusTab(sourceNode);
	}

}
