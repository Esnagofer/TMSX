package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.richtextfx;

import java.util.function.IntFunction;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class JavafxBreakPointFactory implements IntFunction<Node> {
	
    private final Integer shownLine;

    private final Image image = new Image(getClass().getResourceAsStream("/icons/break-point-16x16.png"));
    

    public JavafxBreakPointFactory(Integer shownLine) {
        this.shownLine = shownLine;
    }

    @Override
    public Node apply(int lineNumber) {
    	Node node = new ImageView(image);
        node.setVisible(lineNumber == shownLine);
        return node;
    }
}
