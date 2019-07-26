package esnagofer.msx.ide.emulator.core.domain.model.ide;

import javax.inject.Inject;

import esnagofer.msx.ide.lib.Validate;
import esnagofer.msx.ide.lib.domain.model.core.DomainEventManager;

public class IdeFactory {

	private static volatile Ide INSTANCE;
	
	private DomainEventManager domainEventManager;

	public IdeFactory() {}

	@Inject
	public IdeFactory(DomainEventManager domainEventManager) {
		Validate.isNotNull(domainEventManager);
		this.domainEventManager = domainEventManager;
	}
	
	public synchronized Ide createIde() {
		if (INSTANCE == null) {
			INSTANCE = Ide.valueOf(domainEventManager);
			domainEventManager.publish(IdeCreatedEvent.valueOf(INSTANCE));
		}
		return INSTANCE;
	}

}
