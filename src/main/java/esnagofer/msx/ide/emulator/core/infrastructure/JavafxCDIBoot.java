package esnagofer.msx.ide.emulator.core.infrastructure;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

public class JavafxCDIBoot {

	public static void main(String[] args) {
        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {
            container.select(ApplicationBoot.class).get().run();
        }
	}
		
}
