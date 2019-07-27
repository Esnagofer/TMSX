package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.emulator;

import esnagofer.msx.ide.emulator.core.domain.model.emulator.components.screen.Color;
import esnagofer.msx.ide.emulator.core.domain.model.emulator.components.screen.Screen;
import esnagofer.msx.ide.lib.Validate;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;


public class JavafxScreen implements Screen {

	private Integer scale;
	
	private Canvas targetCanvas;
	
	WritableImage imageToRenderScene; 
	
	protected JavafxScreen(
		Canvas targetCanvas,
		Integer scale
	) {
		Validate.isNotNull(targetCanvas);
		Validate.isNotNull(scale);
		this.targetCanvas = targetCanvas;
		this.scale = scale;
		resetTemporalGC();
	}

	@Override
	public void setPixel(int px, int py, Color color) {
		if (color.alpha() == 0) return;
		javafx.scene.paint.Color blockColor = new javafx.scene.paint.Color(
			color.red()/255, 
			color.green()/255, 
			color.blue()/255, 
			color.alpha()/255
		);
//		imageToRenderScene.getPixelWriter().setColor(px, py, blockColor);				

		int xOrigin = px * scale;
		int yOrigin = py * scale;
		for (int y = yOrigin; y < yOrigin + scale; y++) {
			for (int x = xOrigin; x < xOrigin + scale; x++) {			
				imageToRenderScene.getPixelWriter().setColor(x, y, blockColor);				
			}
		}
		
	}

	private void resetTemporalGC() {
		imageToRenderScene = new WritableImage(
			(int)Math.round(targetCanvas.getWidth() * scale), 
			(int)Math.round(targetCanvas.getHeight() * scale)
		);
	}
	
	private void transferTemporalToTarget() {
		Platform.runLater(() -> {
			targetCanvas.getGraphicsContext2D().drawImage(imageToRenderScene, 0, 0);					
			resetTemporalGC();
		});
	}
	
	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.tms9918a.TMS9918ACanvas#refresh()
	 */
	@Override
	public void refresh() {
		transferTemporalToTarget();
	}

	/**
	 * Paint.
	 */
	@Override
	public void paint() {
		transferTemporalToTarget();
	}
	
	public static Screen newInstance(Canvas canvas, Integer scale) {
		return new JavafxScreen(canvas, scale);
	}
	
}
