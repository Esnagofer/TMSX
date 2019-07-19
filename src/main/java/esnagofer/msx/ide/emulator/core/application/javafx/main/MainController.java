package esnagofer.msx.ide.emulator.core.application.javafx.main;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

public class MainController {

	@FXML
	private TextField txtEdit1;

	@FXML
	private Canvas emulatorCanvas;

	private Main main;

	@FXML
	public void btnClickmeClicked(Event e) {
		txtEdit1.setText("Clicked!!!!");
		GraphicsContext gc = emulatorCanvas.getGraphicsContext2D();
		gc.setFill(Paint.valueOf("ff0000"));
		gc.fillRoundRect(1000, 1000, 1001, 1001, 0, 0);
		int scale = 1;
		int px = 0;
		int py = 0;
		int x = px * scale;
		int y = py * scale;
		gc.fillRoundRect(x, y, px + scale, py + scale, 0, 0);

	}
	
	public Canvas emulatorCanvas() {
		return emulatorCanvas;
	}

}
