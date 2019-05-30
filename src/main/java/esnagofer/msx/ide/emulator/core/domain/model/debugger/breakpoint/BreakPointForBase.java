package esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint;

/**
 * The Class BreakPointForBase.
 */
public class BreakPointForBase implements BreakPoint {

	/** The enabled. */
	private boolean enabled = true;
	
	/** The must break flow. */
	protected boolean mustBreakFlow = false;
	
	/**
	 * Instantiates a new break point for base.
	 *
	 * @param enabled the enabled
	 */
	public BreakPointForBase(boolean enabled) {
		this.enabled = enabled;
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPoint#enable()
	 */
	@Override
	public void enable() {
		this.enabled = true;
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPoint#disable()
	 */
	@Override
	public void disable() {
		this.enabled = false;
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPoint#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPoint#computeBreakFlow()
	 */
	@Override
	public void computeBreakFlow() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPoint#mustBreakFlow()
	 */
	@Override
	public boolean mustBreakFlow() {
		return mustBreakFlow;
	}

}
