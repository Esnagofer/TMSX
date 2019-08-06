package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide;

import esnagofer.msx.ide.lib.Validate;

public class BreakPoint {

	public static enum Status {
		ENABLED,
		DISABLED,
		INVALID
	}
	
	private Integer line;
	
	private Status status;
	
	protected BreakPoint(Integer line, Status status) {
		Validate.isNotNull(line);
		Validate.isIntPositive(line);
		Validate.isNotNull(status);
		this.line = line;
		this.status = status;
	}

	public Integer line() {
		return line;
	}
	
	public Status status() {
		return status;
	}

	public static BreakPoint valueOfEnabled(Integer line) {
		return new BreakPoint(line, BreakPoint.Status.ENABLED);
	}

	public static BreakPoint valueOfDisabled(Integer line) {
		return new BreakPoint(line, BreakPoint.Status.DISABLED);
	}

	public static BreakPoint valueOfInvalid(Integer line) {
		return new BreakPoint(line, BreakPoint.Status.INVALID);
	}
	
}
