package esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint;

import esnagofer.msx.ide.emulator.core.domain.model.hardware.z80.Z80;
import esnagofer.msx.ide.lib.Validate;

/**
 * The Class BreakPointForPC.
 */
public class BreakPointForPC extends BreakPointForBase implements BreakPoint {

	/** The z 80. */
	private Z80 z80;
	
	/** The pc. */
	private ProgramCounter pc;

	/**
	 * Instantiates a new break point for PC.
	 *
	 * @param z80 the z 80
	 * @param pc the pc
	 * @param enabled the enabled
	 */
	public BreakPointForPC(Z80 z80, ProgramCounter pc, boolean enabled) {
		super(enabled);
		this.z80 = z80;
		this.pc = pc;
		validateInvariants();
	}

	/**
	 * Validate invariants.
	 */
	private void validateInvariants() {
		Validate.isNotNull(z80);
		Validate.isNotNull(pc);
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPointForBase#computeBreakFlow()
	 */
	@Override
	public void computeBreakFlow() {
		mustBreakFlow = (pc.value() ==  z80.getPC());
	}
	
}
