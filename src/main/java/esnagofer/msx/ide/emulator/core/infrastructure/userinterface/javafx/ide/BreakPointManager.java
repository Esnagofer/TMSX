package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide;

import java.util.List;

import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.editor.sourcecodearea.view.OnBreakPointEvent;

public interface BreakPointManager {

	void addBreakPoint(BreakPoint breakPoint);
	
	void removeBreakPoint(BreakPoint breakPoint);
	
	List<BreakPoint> breakPoints();
	
	void registerValidBreakPoints(List<BreakPoint> validBreakPoints);
	
	void clear();

	void setOnBreakPointEvent(OnBreakPointEvent onBreakPointEvent);
}
