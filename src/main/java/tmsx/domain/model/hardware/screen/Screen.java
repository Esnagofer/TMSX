package tmsx.domain.model.hardware.screen;

/**
 * The Interface TMS9918ACanvas.
 * 
 * @author esnagofer
 * 
 */
public interface Screen {

	/**
	 * Sets the pixel.
	 *
	 * @param px the px
	 * @param py the py
	 * @param color the color
	 */
	public void setPixel(int px, int py, Color color);
	
	/**
	 * Refresh.
	 */
	public void refresh();

	/**
	 * Paint.
	 */
	public void paint();
	
}
