package esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.breakpoint;

import esnagofer.msx.ide.emulator.core.domain.model.emulator.components.z80.Z80;
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
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pc == null) ? 0 : pc.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BreakPointForPC other = (BreakPointForPC) obj;
		if (pc == null) {
			if (other.pc != null)
				return false;
		} else if (!pc.equals(other.pc))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPointForBase#computeBreakFlow()
	 */
	@Override
	public boolean computeBreakFlow() {
		mustBreakFlow = (pc.value() ==  z80.getPC());
		return mustBreakFlow;
	}
	
}
