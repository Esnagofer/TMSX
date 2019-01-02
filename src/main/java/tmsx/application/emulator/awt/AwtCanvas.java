package tmsx.application.emulator.awt;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import tmsx.domain.model.hardware.tms9918a.TMS9918A;
import tmsx.domain.model.hardware.tms9918a.TMS9918ACanvas;

/**
 * The Class AwtCanvas.
 */
public class AwtCanvas implements TMS9918ACanvas {

	/** The graphics. */
	private Graphics graphics;
	
	private BufferedImage img;

	/**
	 * Instantiates a new awt canvas.
	 *
	 * @param graphics the graphics
	 */
	protected AwtCanvas(Graphics graphics) {
		super();
		this.graphics = graphics;
		this.img = new BufferedImage(
			TMS9918A.VDP_WIDTH * TMS9918A.IMG_SCALE, 
			TMS9918A.VDP_HEIGHT * TMS9918A.IMG_SCALE, 
			BufferedImage.TYPE_INT_ARGB
		);
	}

	/**
	 * Sets the pixel.
	 *
	 * @param px the px
	 * @param py the py
	 * @param color the color
	 */
	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.tms9918a.TMS9918ACanvas#setPixel(int, int, int)
	 */
	@Override
	public void setPixel(int px, int py, int color) {
		// TODO Auto-generated method stub

	}

	/**
	 * Paint.
	 */
	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.tms9918a.TMS9918ACanvas#paint()
	 */
	@Override
	public void paint() {
		// TODO Auto-generated method stub

	}

	/**
	 * Refresh.
	 */
	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.tms9918a.TMS9918ACanvas#refresh()
	 */
	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	/**
	 * New instance.
	 *
	 * @param graphics the graphics
	 * @return the awt canvas
	 */
	public static AwtCanvas newInstance(Graphics graphics) {
		return new AwtCanvas(graphics);
	}

}
