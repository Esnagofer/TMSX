package esnagofer.msx.ide.emulator.core.domain.model.debugger;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPoint;
import esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPointService;
import esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.BreakPointStatus;
import esnagofer.msx.ide.emulator.core.domain.model.debugger.breakpoint.ProgramCounter;
import esnagofer.msx.ide.emulator.core.domain.model.debugger.execute.ExecuteService;
import esnagofer.msx.ide.emulator.core.domain.model.debugger.execute.ExecuteStatus;
import esnagofer.msx.ide.emulator.core.domain.model.emulator.Emulator;

/**
 * The Class LocalDebugger.
 *
 * @author user
 */
public class LocalDebugger implements Debugger, BreakPointService, ExecuteService {

	/** The status. */
	private DebuggerStatus status;
	
	/** The next step. */
	private DebuggerStepExecute nextStep;
	
	/** The emulator. */
	private Emulator emulator;

	/** The previous SP. */
	private int initSP;

	/** The barrier. */
	CyclicBarrier barrier = new CyclicBarrier(2);

	/**
	 * Instantiates a new local debugger.
	 *
	 * @param emulator the emulator
	 */
	public LocalDebugger(Emulator emulator) {
		this.emulator = emulator;
		status = DebuggerStatus.DS_INACTIVE;
	}
	
	/**
	 * Signal barrier.
	 */
	private void waitForSignal() {
		try {
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Ds running.
	 * 
	 * Determina si existen BP para detener la ejecución: si así es 
	 * para a modo DS_WAITING.
	 * 
	 */
	private void dsBeforeRunning() {
//		status = DebuggerStatus.DS_RUNNING;
//		dsBeforeWaiting();
	}

	/**
	 * Ds waiting.
	 */
	private void dsBeforeWaiting() {
		waitForSignal();
		switch (nextStep) {
			case STEP_INTO: 
				dsBeforeExecForStepInto();
				break;
			case STEP_OUT:
				dsBeforeExecForStepOut();				
				break;
			case STEP_OVER:
				dsBeforeExecForStepOver();
				break;
			case RUN:
				status = DebuggerStatus.DS_RUNNING;
				dsBeforeRunning();
				break;
		}
	}

	/**
	 * Ds exec for step into.
	 */
	private void dsBeforeExecForStepInto() {
		status = DebuggerStatus.DS_WAITING;
	}

	/**
	 * Ds exec for step out.
	 */
	private void dsBeforeExecForStepOut() {
		status = DebuggerStatus.DS_EXEC_FOR_STEP_OUT;
		initSP = emulator.cpu().getSpAsInt();
	}

	/**
	 * Ds exec for step over.
	 */
	private void dsBeforeExecForStepOver() {
		status = DebuggerStatus.DS_EXEC_FOR_STEP_OVER;
		initSP = emulator.cpu().getSpAsInt();
	}

	/**
	 * Ds after exec for step over.
	 */
	private void dsAfterExecForStepOver() {
		if (emulator.cpu().getSpAsInt() == initSP) {
			status = DebuggerStatus.DS_WAITING;
		}		
	}
	
	/**
	 * Ds after exec for step out.
	 */
	private void dsAfterExecForStepOut() {
		if (emulator.cpu().getSpAsInt() > initSP) {
			status = DebuggerStatus.DS_WAITING;
		}		
	}
	
	/**
	 * Before execute.
	 */
	public void beforeExecute() {
		switch (status) {
		case DS_RUNNING: dsBeforeRunning(); break;
		case DS_WAITING: dsBeforeWaiting(); break;
		case DS_EXEC_FOR_STEP_INTO: dsBeforeExecForStepInto(); break;
		case DS_EXEC_FOR_STEP_OUT: dsBeforeExecForStepOut(); break;
		case DS_EXEC_FOR_STEP_OVER: dsBeforeExecForStepOver(); break;
		default:
			return;
		}		
	}
	
	/**
	 * After execute.
	 */
	public void afterExecute() {
		switch (status) {
		case DS_EXEC_FOR_STEP_OUT:
			dsAfterExecForStepOut();
			break;
		case DS_EXEC_FOR_STEP_OVER: 			
			dsAfterExecForStepOver();
			break;
		default:
			return;
		}	
	}
	
	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.Debugger#breakPoint()
	 */
	@Override
	public BreakPointService breakPoint() {
		return this;
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.Debugger#execute()
	 */
	@Override
	public ExecuteService execute() {
		return this;
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.execute.ExecuteService#status()
	 */
	@Override
	public ExecuteStatus status() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.execute.ExecuteService#run()
	 */
	@Override
	public void run() {
		nextStep = DebuggerStepExecute.RUN;
		waitForSignal();
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.execute.ExecuteService#stepInto()
	 */
	@Override
	public void stepInto() {
		nextStep = DebuggerStepExecute.STEP_INTO;
		waitForSignal();
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.execute.ExecuteService#stepOut()
	 */
	@Override
	public void stepOut() {
		nextStep = DebuggerStepExecute.STEP_OUT;
		waitForSignal();
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.execute.ExecuteService#stepOver()
	 */
	@Override
	public void stepOver() {
		nextStep = DebuggerStepExecute.STEP_OVER;
		waitForSignal();
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.breakpoint.BreakPointService#breakPoints()
	 */
	@Override
	public List<BreakPoint> breakPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.breakpoint.BreakPointService#get(esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.breakpoint.ProgramCounter)
	 */
	@Override
	public BreakPoint get(ProgramCounter pc) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.breakpoint.BreakPointService#add(esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.breakpoint.BreakPoint)
	 */
	@Override
	public void add(BreakPoint bp) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.breakpoint.BreakPointService#remove(esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.breakpoint.ProgramCounter)
	 */
	@Override
	public void remove(ProgramCounter pc) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.breakpoint.BreakPointService#status(esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.breakpoint.ProgramCounter, esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.breakpoint.BreakPointStatus)
	 */
	@Override
	public void status(ProgramCounter pc, BreakPointStatus status) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.Debugger#start()
	 */
	@Override
	public void start() {
		status = DebuggerStatus.DS_RUNNING;
	}
	
	/* (non-Javadoc)
	 * @see esnagofer.msx.ide.emulator.core.domain.model.emulator.debugger.Debugger#stop()
	 */
	@Override
	public void stop() {
		status = DebuggerStatus.DS_INACTIVE;
	}

}
