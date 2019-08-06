package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.editor.sourcecodearea.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.BreakPoint;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.BreakPointManager;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SourceCodeAreaBreakPointManager implements IntFunction<Node>, BreakPointManager {
	
    private final Image enabledBreakPoint = new Image(getClass().getResourceAsStream("/icons/enabled-break-point-16x16.png"));
    
    private final Image disabledBreakPoint = new Image(getClass().getResourceAsStream("/icons/disabled-break-point-16x16.png"));
    
    private final Image invalidBreakPoint = new Image(getClass().getResourceAsStream("/icons/invalid-break-point-16x16.png"));
    
    private Map<Integer, BreakPoint> breakPoints = new HashMap<>();
    
    @Override
    public Node apply(int lineNumber) {
    	Node node = new ImageView(enabledBreakPoint);
    	node.setVisible(false);
    	if (breakPoints.keySet().contains(Integer.valueOf(lineNumber))) {
    		BreakPoint breakPoint = breakPoints.get(lineNumber);
    		switch (breakPoint.status()) {
    		case ENABLED: node = new ImageView(enabledBreakPoint); break;
    		case DISABLED: node = new ImageView(disabledBreakPoint);  break;
    		default:
    			node = new ImageView(invalidBreakPoint);
    		}
    		node.setVisible(true);
    		node.setOnMouseClicked(event -> {
				Node eventNode = Node.class.cast(event.getSource());
				eventNode.setVisible(!eventNode.isVisible());
    		});
    	}
        return node;
    }
    
    public void addBreakPoint(BreakPoint breakPoint) {
    	breakPoints.put(breakPoint.line(), breakPoint);
    }
    
    public void removeBreakPoint(BreakPoint breakPoint) {
    	breakPoints.remove(breakPoint.line());
    }
    
	@Override
	public List<BreakPoint> breakPoints() {
		return new ArrayList<>(breakPoints.values());
	}

	@Override
	public void registerValidBreakPoints(List<BreakPoint> validBreakPoints) {
		throw new UnsupportedOperationException("Not implemented!");
	}

	@Override
	public void clear() {
		breakPoints.clear();
	}
    
}
