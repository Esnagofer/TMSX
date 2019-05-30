package esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint;

import java.util.List;

public interface BreakPointService {

	List<BreakPoint> breakPoints();
	
	BreakPoint get(ProgramCounter pc);
	
	void add(BreakPoint bp);

	void remove(ProgramCounter pc);
	
	void status(ProgramCounter pc, BreakPointStatus status);

}
