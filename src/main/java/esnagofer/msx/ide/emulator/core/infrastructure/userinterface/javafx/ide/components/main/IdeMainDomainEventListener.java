package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.main;

import javax.enterprise.event.Observes;

import esnagofer.msx.ide.emulator.core.domain.model.ide.IdeCreatedEvent;
import esnagofer.msx.ide.emulator.core.domain.model.ide.IdeCurrentProjectAssignedEvent;

public class IdeMainDomainEventListener {

    public void onIdeCtetatedEvent(@Observes IdeCreatedEvent event, IdeMain javafxMain) {
        javafxMain.run();
    } 
    
    public void onIdeCurrentProjectAssignedEvent(@Observes IdeCurrentProjectAssignedEvent event) {
    	IdeMainController.instance().assignProjectSource(
			"Jander", 
			event.project().sourceNodes
		);
    
    }
    
}
