package esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint;

/**
 * The Interface BreakPoint.
 */
public interface BreakPoint {

	/**
	 * Enable.
	 */
	void enable();
	
	/**
	 * Disable.
	 */
	void disable();

	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	boolean isEnabled();
	
	/**
	 * Compute break flow.
	 */
	void computeBreakFlow();

	/**
	 * Must break flow.
	 *
	 * @return true, if successful
	 */
	boolean mustBreakFlow();
	
}
