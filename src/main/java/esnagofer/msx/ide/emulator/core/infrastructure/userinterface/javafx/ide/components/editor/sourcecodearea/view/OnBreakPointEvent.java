package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.editor.sourcecodearea.view;

import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.BreakPoint;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.BreakPointAction;

@FunctionalInterface
public interface OnBreakPointEvent {
	
	void notifyEvent(BreakPointAction breakPointAction, BreakPoint breakPoint);
	
}
