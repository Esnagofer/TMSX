package tmsx.application.emulator.awt;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import tmsx.domain.model.hardware.screen.Color;
import tmsx.domain.model.hardware.screen.Screen;
import tmsx.domain.model.hardware.tms9918a.TMS9918A;


/**
 * The Class AwtScreen.
 */
public class AwtScreen implements Screen {

	/** The component. */
	private Component component;
	
	/** The graphics. */
	private Graphics graphics;
	
	/** The img. */
	private BufferedImage img;

	/**
	 * Instantiates a new awt canvas.
	 *
	 * @param component the graphics
	 */
	protected AwtScreen(Component component) {
		super();
		this.component = component;
		this.img = new BufferedImage(
			TMS9918A.VDP_WIDTH * TMS9918A.IMG_SCALE, 
			TMS9918A.VDP_HEIGHT * TMS9918A.IMG_SCALE, 
			BufferedImage.TYPE_INT_ARGB
		);
	}

	/**
	 * Sets the graphics.
	 *
	 * @param graphics the new graphics
	 */
	public void setGraphics(Graphics graphics) {
		this.graphics = graphics;
	}
	
	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.tms9918a.TMS9918ACanvas#setPixel(int, int, int)
	 */
	@Override
	public void setPixel(int px, int py, Color color) {
		if (color.getAlpha() == 0) return;
		px <<= TMS9918A.IMG_SCALE - 1; py <<= TMS9918A.IMG_SCALE - 1;
		if (px + 1 >= TMS9918A.VDP_WIDTH * TMS9918A.IMG_SCALE || 
			py + 1 >= TMS9918A.VDP_HEIGHT * TMS9918A.IMG_SCALE || 
			px < 0 || py < 0
		) return;
		for (int xs = 0; xs < TMS9918A.IMG_SCALE; xs++) {
			for (int ys = 0; ys < TMS9918A.IMG_SCALE; ys++) {
				img.setRGB(px + xs, py + ys, color.getRGB());
			}
		}
	}

	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.tms9918a.TMS9918ACanvas#refresh()
	 */
	@Override
	public void refresh() {
		component.repaint();
	}

	/**
	 * Paint.
	 */
	@Override
	public void paint() {
		if (graphics == null) {
			throw new IllegalStateException("AwtScreen: 'graphics' not set");
		}
		graphics.drawImage(img, 0, 0, null);		
	}
	
	/**
	 * New instance.
	 *
	 * @param component the graphics
	 * @return the awt canvas
	 */
	public static Screen newInstance(Component component) {
		return new AwtScreen(component);
	}

}
