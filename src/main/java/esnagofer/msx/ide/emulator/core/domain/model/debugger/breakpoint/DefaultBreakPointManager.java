package esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The Class DefaultBreakPointManager.
 */
public class DefaultBreakPointManager implements BreakPointManager {

	/** The breakpoints. */
	private Set<BreakPoint> breakpoints;

	/** The last flow stopped at this break point. */
	private BreakPoint lastFlowStoppedAt;
	
	/**
	 * Instantiates a new local break point service.
	 */
	public DefaultBreakPointManager() {
		super();
		breakpoints = new HashSet<>();
	}
	
	/**
	 * Must break flow.
	 *
	 * @return true, if successful
	 */
	public boolean mustBreakFlow() {
		lastFlowStoppedAt = null;
		for (BreakPoint thisBreakPoint: breakpoints) {
			if (thisBreakPoint.isEnabled() && thisBreakPoint.mustBreakFlow()) {
				lastFlowStoppedAt = thisBreakPoint;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Last flow stopped at this break point.
	 *
	 * @return the optional
	 */
	@Override
	public Optional<BreakPoint> lastFlowStoppedAt() {
		return Optional.ofNullable(lastFlowStoppedAt);
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPointService#breakPoints()
	 */
	@Override
	public Set<BreakPoint> breakPoints() {
		return Collections.unmodifiableSet(breakpoints);
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPointService#add(esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPoint)
	 */
	@Override
	public void add(BreakPoint bp) {
		if (!breakpoints.contains(bp)) {
			breakpoints.add(bp);
		}
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPointService#remove(esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.ProgramCounter)
	 */
	@Override
	public void remove(BreakPoint bp) {
		if (breakpoints.contains(bp)) {
			breakpoints.remove(bp);
		}		
	}

}
