package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.cdi;

import javax.enterprise.inject.Produces;

import esnagofer.msx.ide.emulator.core.domain.model.ide.Ide;
import esnagofer.msx.ide.emulator.core.domain.model.ide.IdeFactory;

public class IdeProducer {

	@Produces
	public Ide ideProducer(IdeFactory ideFactory) {
		return ideFactory.get();
	}

}
