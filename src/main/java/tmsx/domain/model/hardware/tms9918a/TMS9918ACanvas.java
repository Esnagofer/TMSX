package tmsx.domain.model.hardware.tms9918a;

/**
 * The Interface TMS9918ACanvas.
 * 
 * @author esnagofer
 * 
 */
public interface TMS9918ACanvas {

	/**
	 * Sets the pixel.
	 *
	 * @param px the px
	 * @param py the py
	 * @param color the color
	 */
	public void setPixel(int px, int py, int color);
	
	/**
	 * Paint.
	 */
	public void paint();

	/**
	 * Refresh.
	 */
	public void refresh();
	
}
