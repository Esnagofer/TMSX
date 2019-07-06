package esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint;

import java.util.Optional;
import java.util.Set;

/**
 * The Interface BreakPointService.
 */
public interface BreakPointManager {

	/**
	 * Break points.
	 *
	 * @return the list
	 */
	Set<BreakPoint> breakPoints();
	
	/**
	 * Last flow stopped at.
	 *
	 * @return the optional
	 */
	Optional<BreakPoint> lastFlowStoppedAt();
	
	/**
	 * Adds the.
	 *
	 * @param bp the bp
	 */
	void add(BreakPoint bp);

	/**
	 * Removes the.
	 *
	 * @param pc the pc
	 */
	void remove(BreakPoint pc);
	
}
