package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide;

import java.util.List;

public interface BreakPointManager {

	void addBreakPoint(BreakPoint breakPoint);
	
	void removeBreakPoint(BreakPoint breakPoint);
	
	List<BreakPoint> breakPoints();
	
	void registerValidBreakPoints(List<BreakPoint> validBreakPoints);
	
	void clear();
}
