package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.main;

import esnagofer.msx.ide.emulator.core.domain.model.project.SourceNode;
import esnagofer.msx.ide.lib.Validate;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

public class TreeItemSourceNode extends TreeItem<String>{

	private SourceNode sourceNode;
	
	public TreeItemSourceNode(SourceNode sourceNode) {
		super(sourceNode.name());
		Validate.isNotNull(sourceNode);
		this.sourceNode = sourceNode;
	}

	public TreeItemSourceNode(SourceNode sourceNode, Node graphic) {
		super(sourceNode.name(), graphic);
		Validate.isNotNull(sourceNode);
		this.sourceNode = sourceNode;
	}

	public SourceNode sourceNode() {
		return sourceNode;
	}
	
}
