package esnagofer.msx.ide.emulator.core.domain.model.debugger;

import esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPointService;
import esnagofer.msx.ide.emulator.core.domain.model.debugger.execute.ExecuteService;

/**
 * The Interface Debugger.
 */
public interface Debugger {

	/**
	 * Start.
	 */
	void start();
	
	/**
	 * Stop.
	 */
	void stop();
	
	/**
	 * Break point.
	 *
	 * @return the break point service
	 */
	BreakPointService breakPoint();
	
	/**
	 * Execute.
	 *
	 * @return the execute service
	 */
	ExecuteService execute();
		
}
