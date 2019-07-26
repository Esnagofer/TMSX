package esnagofer.msx.ide.emulator.core.domain.model.ide;

import esnagofer.msx.ide.lib.Validate;
import esnagofer.msx.ide.lib.domain.model.core.DomainEvent;

public class IdeCreatedEvent extends DomainEvent {
	
	private Ide ide;
	
	protected IdeCreatedEvent(Ide ide) {
		Validate.notNull("ide", ide);
		this.ide = ide;
	}
	
	public Ide ide() {
		return ide;
	}
	
	public static IdeCreatedEvent valueOf(Ide ide) {
		return new IdeCreatedEvent(ide);
	}	
	
}
