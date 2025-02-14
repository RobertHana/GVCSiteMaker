/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public abstract class TraceAssertion implements Communicator, Runnable {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    /* constants */
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.TraceAssertion";
    public final static String CLASS_IDENTIFIERPREFIX = "JassTA_";
    public static final String PROCESS_MAIN = "MAIN";
    public static final int    BASICPROCESS_NONE = 0;
    public static final int    BASICPROCESS_DEADLOCK = 1;
    public static final int    BASICPROCESS_CHAOS = 2;
    int			       basicProcess = BASICPROCESS_NONE;
    ProcessObserver	       processes;
    Communication	       actualCommunication;
    ProcessContext	       mainContext;
    TraceStack		       traceStack;

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public TraceAssertion() {}

    /*
     * ....................................................................
     * . Protected Methods                                                .
     * ....................................................................
     */

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */
    /* abstract methods */

    /**
     * Method declaration
     *
     *
     * @param processname
     *
     * @return
     *
     * @see
     */
    public abstract String getClassname(String processname);

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public abstract SetOfMethods getAlphabet();

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public abstract String getName();

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public abstract Source getSource();

    /* non-abstract methods */

    /**
     * Method declaration
     *
     *
     * @param finalState
     *
     * @see
     */
    public void basicProcessReached(StateForNewBasicProcess finalState) {

	// System.out.println("##BASIC:" + process);
	if (finalState instanceof StateDeadlock) {
	    basicProcess = BASICPROCESS_DEADLOCK;
	} else if (finalState instanceof StateChaos) {
	    basicProcess = BASICPROCESS_CHAOS;
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param mr
     *
     * @return
     *
     * @see
     */
    public boolean canCommunicate(MethodReference mr) {
	boolean canCommunicate;

	switch (basicProcess) {

	case BASICPROCESS_CHAOS: {

	    // every communication is possible
	    canCommunicate = true;

	    break;
	} 

	case BASICPROCESS_DEADLOCK: {

	    // no communication is possible
	    canCommunicate = false;

	    break;
	} 

	default: {
	    canCommunicate = mainContext.canCommunicate(mr);
	} 
	}

	return canCommunicate;
    } 

    /**
     * Method declaration
     *
     *
     * @param mr
     * @param parameters
     *
     * @see
     */
    public void communicate(MethodReference mr, Parameter[] parameters) {
	communicate(traceStack, mr, parameters);
    } 

    /**
     * Method declaration
     *
     *
     * @param traceStack
     * @param mr
     * @param parameters
     *
     * @see
     */
    public void communicate(TraceStack traceStack, MethodReference mr, 
			    Parameter[] parameters) {

	// System.out.println("##" + toString() + " receives " + mr);
	// System.out.println(dump());
	switch (basicProcess) {

	case BASICPROCESS_CHAOS: {

	    // every communication is possible
	    // System.out.println("abgefangen:" + mr);
	    break;
	} 

	case BASICPROCESS_DEADLOCK: {

	    // no communication is possible
	    throw new TraceException(getProcessObserver(), getSource(), null, 
				     mr, traceStack);
	} 

	default: {
	    mainContext.communicate(traceStack, mr, parameters);
	} 
	}
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public ProcessContext getMainContext() {
	return mainContext;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public ProcessObserver getProcessObserver() {
	return processes;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public TraceStack getTraceStack() {
	return traceStack;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void init() {
	processes = new ProcessObserver(this);
	traceStack = new TraceStack(processes);
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void run() {
	Process processMAIN = null;
	String  classnameMAIN = getClassname(PROCESS_MAIN);

	try {
	    Class       classMAIN = Class.forName(classnameMAIN);
	    MappedClass clazz = (MappedClass) classMAIN.newInstance();

	    processMAIN = clazz.constructProcess();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	} 

	processes.define(processMAIN);

	mainContext = new ProcessContext(processes, new Process[] {
	    processMAIN
	});

	traceStack.newProcessContext(mainContext.toString());
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String dump() {
	String s = "";

	s += getName();

	Process[] listProcess = processes.processes();

	for (int i = 0; i < listProcess.length; ++i) {
	    Process process = listProcess[i];

	    s += process.dump();
	} 

	return s;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

