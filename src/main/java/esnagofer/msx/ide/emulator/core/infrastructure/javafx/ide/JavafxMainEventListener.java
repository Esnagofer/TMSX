package esnagofer.msx.ide.emulator.core.infrastructure.javafx.ide;

import javax.enterprise.event.Observes;

import esnagofer.msx.ide.emulator.core.domain.model.ide.IdeCreatedEvent;

public class JavafxMainEventListener {

    public void onEvent(@Observes IdeCreatedEvent event, JavafxMain javafxMain) {
        javafxMain.run();
    } 
    
}
