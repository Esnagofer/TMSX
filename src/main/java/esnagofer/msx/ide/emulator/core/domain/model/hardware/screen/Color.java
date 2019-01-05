package esnagofer.msx.ide.emulator.core.domain.model.hardware.screen;

/**
 * The Class TMS9918AColor.
 */
public class Color {

	/** The value. */
	private int value;
	
    /**
     * Color.
     *
     * @param r the r
     * @param g the g
     * @param b the b
     */
    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    /**
     * Instantiates a new TMS 9918 A color.
     *
     * @param r the r
     * @param g the g
     * @param b the b
     * @param a the a
     */
    public Color(int r, int g, int b, int a) {
    	super();
        value = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);
        testColorValueRange(r,g,b,a);
    }
    
    /**
     * Test color value range.
     *
     * @param r the r
     * @param g the g
     * @param b the b
     * @param a the a
     */
    private static void testColorValueRange(int r, int g, int b, int a) {
        boolean rangeError = false;
        String badComponentString = "";

        if ( a < 0 || a > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Alpha";
        }
        if ( r < 0 || r > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Red";
        }
        if ( g < 0 || g > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Green";
        }
        if ( b < 0 || b > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Blue";
        }
        if ( rangeError == true ) {
        throw new IllegalArgumentException("Color parameter outside of expected range:"
                                           + badComponentString);
        }
    }

    /**
     * Gets the rgb.
     *
     * @return the rgb
     */
    public int getRGB() {
        return value;
    }

    /**
     * Gets the alpha.
     *
     * @return the alpha
     */
    public int getAlpha() {
        return (getRGB() >> 24) & 0xff;
    }

}
