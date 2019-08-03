package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.sourcecodearea.view;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.BreakPoint;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SourceCodeAreaBreakPointManager implements IntFunction<Node> {
	
	private final Node invisibleNode = new ImageView(new Image(getClass().getResourceAsStream("/icons/enabled-break-point-16x16.png")));
	
    private final Image enabledBreakPoint = new Image(getClass().getResourceAsStream("/icons/enabled-break-point-16x16.png"));
    
    private final Image disabledBreakPoint = new Image(getClass().getResourceAsStream("/icons/disabled-break-point-16x16.png"));
    
    private final Image invalidBreakPoint = new Image(getClass().getResourceAsStream("/icons/invalid-break-point-16x16.png"));
    
    private Map<Integer, BreakPoint> breakPoints = new HashMap<>();

    public void addBreakPoint(BreakPoint breakPoint) {
    	breakPoints.put(breakPoint.line(), breakPoint);
    }

    public void removeBreakPoint(BreakPoint breakPoint) {
    	breakPoints.remove(breakPoint.line());
    }

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
    	}
        return node;
    }
    
}
