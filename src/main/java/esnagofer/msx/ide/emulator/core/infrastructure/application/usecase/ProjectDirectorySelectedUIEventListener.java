package esnagofer.msx.ide.emulator.core.infrastructure.application.usecase;

import java.io.IOException;

import javax.enterprise.event.Observes;

import esnagofer.msx.ide.emulator.core.application.usecase.OpenProjectCommand;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.project.ProjectDirectorySelectedUIEvent;
import esnagofer.msx.ide.lib.cqs.CommandExecutionHandler;

public class ProjectDirectorySelectedUIEventListener {

    public void onEvent(
		@Observes ProjectDirectorySelectedUIEvent event, 
		CommandExecutionHandler<OpenProjectCommand> commandExecutionHandler
	) {
    	try {
			commandExecutionHandler.execute(
				OpenProjectCommand.valueOf(event.directoryProject().getCanonicalPath())
			);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    } 
    
}
