package esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.execute;

public interface ExecuteService {

	ExecuteStatus status();

	void run();
	
	void stepInto();
	
	void stepOut();
	
	void stepOver();
	
}
