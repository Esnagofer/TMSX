package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.editor.tab;

import esnagofer.msx.ide.emulator.core.domain.model.project.SourceNode;
import esnagofer.msx.ide.lib.Validate;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

/**
 * TODO:	SourceNode Observer in Name property --> change label
 * @author user
 *
 */
public class SourceTabComponent {

	private Tab sourceTab;
	
	private SourceNode sourceNode;
	
	protected SourceTabComponent(SourceNode sourceNode) {
		Validate.isNotNull(sourceNode);
		this.sourceNode = sourceNode;
		initTab();
	}

	private void initTab() {
		sourceTab = new Tab();
		sourceTab.setClosable(true);	
		setTabLabel();
	}
	
	private void setTabLabel() {
		sourceTab.setGraphic(new Label(sourceNode.name()));		
	}
	
	public Tab sourceTab() {
		return sourceTab;
	}
	
	public SourceNode sourceNode() {
		return sourceNode;
	}
	
	public static SourceTabComponent valueOf(SourceNode sourceNode) {
		return new SourceTabComponent(sourceNode);
	}

	
}
