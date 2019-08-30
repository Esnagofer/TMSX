package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.editor.sourcecodearea.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntFunction;

import org.fxmisc.richtext.LineNumberFactory;

import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.BreakPoint;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.BreakPointAction;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.BreakPointManager;
import esnagofer.msx.ide.lib.Validate;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

public class SourceCodeAreaBreakPointManager implements IntFunction<Node>, BreakPointManager {
	
    private final Image noBreakPoint = new Image(getClass().getResourceAsStream("/icons/no-break-point-16x16.png"));
    
    private final Image enabledBreakPoint = new Image(getClass().getResourceAsStream("/icons/enabled-break-point-16x16.png"));
    
    private final Image disabledBreakPoint = new Image(getClass().getResourceAsStream("/icons/disabled-break-point-16x16.png"));
    
    private final Image invalidBreakPoint = new Image(getClass().getResourceAsStream("/icons/invalid-break-point-16x16.png"));
    
    private Map<Integer, BreakPoint> breakPoints = new HashMap<>();
    
    private SourceCodeArea sourceCodeArea;
    
    private Optional<OnBreakPointEvent> onBreakPointEvent;
    
    public SourceCodeAreaBreakPointManager(SourceCodeArea sourceCodeArea) {
    	Validate.isNotNull(sourceCodeArea);
    	this.sourceCodeArea = sourceCodeArea;
    	init();
	}

	private void init() {
		IntFunction<Node> numberFactory = LineNumberFactory.get(sourceCodeArea);
		IntFunction<Node> arrowFactory = this;
		IntFunction<Node> graphicFactory = editorLine -> {
			HBox hbox = new HBox(
				numberFactory.apply(editorLine),
				arrowFactory.apply(editorLine));
			hbox.setAlignment(Pos.CENTER_LEFT);
			return hbox;
		};
		sourceCodeArea.setParagraphGraphicFactory(graphicFactory);		
	}

	@Override
    public Node apply(final int lineNumber) {
    	ImageView node = new ImageView(noBreakPoint);
    	if (breakPoints.keySet().contains(Integer.valueOf(lineNumber))) {
    		BreakPoint breakPoint = breakPoints.get(lineNumber);
    		switch (breakPoint.status()) {
    		case ENABLED: node = new ImageView(enabledBreakPoint); break;
    		case DISABLED: node = new ImageView(disabledBreakPoint);  break;
    		default:
    			node = new ImageView(invalidBreakPoint);
    		}
    	}
    	node.setOnMouseEntered(event -> {
    		ImageView.class.cast(event.getSource()).setCursor(Cursor.HAND);
    	});
    	node.setOnMouseExited(event -> {
    		ImageView.class.cast(event.getSource()).setCursor(Cursor.DEFAULT);
    	});
    	node.setVisible(true);
    	node.setOnMouseReleased(event -> {
    		if (event.getButton() == MouseButton.SECONDARY) {
    			if (onBreakPointEvent.isPresent()) {
    				Node eventNode = Node.class.cast(event.getSource());
    				if (eventNode.isVisible()) {
    					onBreakPointEvent.get().notifyEvent(BreakPointAction.REMOVE, breakPoints.get(lineNumber));
    				} else {
    					onBreakPointEvent.get().notifyEvent(BreakPointAction.ADD, BreakPoint.valueOfEnabled(lineNumber));
    				}
    			}
    		}
    	});
        return node;
    }
	
	@Override
	public void setOnBreakPointEvent(OnBreakPointEvent onBreakPointEvent) {
		Validate.isNotNull(onBreakPointEvent);
		this.onBreakPointEvent = Optional.ofNullable(onBreakPointEvent);
	}
    
	@Override
    public void addBreakPoint(BreakPoint breakPoint) {
    	breakPoints.put(breakPoint.line(), breakPoint);
    }
    
	@Override
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
