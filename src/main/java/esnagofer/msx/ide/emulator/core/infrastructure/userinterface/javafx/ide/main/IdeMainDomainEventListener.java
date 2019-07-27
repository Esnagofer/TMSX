package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.main;

import javax.enterprise.event.Observes;

import esnagofer.msx.ide.emulator.core.domain.model.ide.IdeCreatedEvent;

public class IdeMainDomainEventListener {

    public void onEvent(@Observes IdeCreatedEvent event, IdeMain javafxMain) {
        javafxMain.run();
    } 
    
}
