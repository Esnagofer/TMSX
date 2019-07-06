package esnagofer.msx.ide.emulator.core.domain.model.debugger;

import esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPointManager;

/**
 * The Interface Debugger.
 */
public interface Debugger {

	void startDebugger();
	
	void stopDebugger();

	DebuggerStatus status();

	void run();
	
	void stepInto();
	
	void stepOut();
	
	void stepOver();

	BreakPointManager breakPoint();
	
}
