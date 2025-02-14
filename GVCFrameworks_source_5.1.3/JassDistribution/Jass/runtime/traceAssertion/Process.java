/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import java.lang.reflect.Method;
import java.util.HashSet;
import jass.runtime.traceAssertion.traceMonitor.*;
import jass.runtime.util.ToJava;

/**
 * A Process is a special kind of an automaton.
 * The automaton model is extended by a so called Lookahead Transition.
 * 
 * The automaton must be constructed from bottom up. The constrution 
 * primitives are prefix and choice. This comes with some limitations 
 * to the automata structure. Choice may be used to split the operational
 * path of a process.
 *
 * This reduces the automata structure to a tree representation.
 * However a combination can be realised by using a process referer.
 *
 * @author Michael Plath
 * @version %I%, %G%
 *
 * @see jass.runtime.traceAssertion.LookaheadTransition.
 */
public class Process implements Communicator, Freezable {


    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.Process";
    public static final String NEWLINE = System.getProperty("line.separator");
    private StringBuffer       code;
    private String	       lineIndent;
    MethodReference	       actualMethodReference;
    State		       actualState;
    TraceStack		       actualTraceStack;
    Parameter[]		       actualParameters;

    // SetOfCommunications alphabet;
    ProcessContext	       context;
    State		       initialState;
    String		       name;
    ProcessObserver	       processes;

    // String[] referedProcesses;
    SetOfMethods	       initialCommunications;
    MappedClass		       mappedClass;
    public final static String CODE_PROCESS = "process";
    public final static String CODE_STATE = "s";
    public final static String CODE_TRANSITION = "t";

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    /**
     * Constructor declaration
     *
     */
    public Process() {}

    /**
     * Get the source code to to create an object of this class.
     *
     * @return  the String of Java source code to create an object of 
     *          this class
     */
    public static String constructorString() {
	return "new " + CLASSNAME + "()";
    } 

    /*
     * ....................................................................
     * . Private Methods                                                  .
     * ....................................................................
     */

    /**
     * Append indented code to the internal field.
     *
     * @param newCode to be appended to the code
     */
    private void addCode(String newCode) {
	code.append(lineIndent).append(newCode);
    } 

    /**
     * Append indented code to the internal field including a new line.
     *
     * @param newCode to be appended to the code
     *
     */
    private void addCodeln(String newCode) {
	addCode(newCode);
	code.append(NEWLINE);
    } 

    /**
     * Clone this process from a given state.
     *
     * @param state state to start from
     *
     * @return cloned state
     *
     */
    private State cloneProcess(State state) {
	State	     clonedState = (State) state.clone();

	// clone automaton from bottom up, since this is the way the automaton was constructed.
	Transition[] listOutgoing = state.getOutgoing();

	for (int i = 0; i < listOutgoing.length; ++i) {
	    Transition t = listOutgoing[i];
	    Transition clonedTransition = (Transition) t.clone();

	    if (!t.isLoop()) {
		State clonedSuccState = 
		    cloneProcess(listOutgoing[i].getSuccState());

		clonedSuccState.addIncoming(clonedTransition);
	    } else {
		clonedState.addIncoming(clonedTransition);
	    } 

	    clonedState.addOutgoing(clonedTransition);
	} 

	return clonedState;
    } 

    /**
     * Method declaration
     *
     *
     * @param id  an id to be included in the resulting String
     *
     * @return a Code state name (?)
     *
     */
    private String codeState(int id) {
	return CODE_STATE + id + "_" + getName();
    } 

    /**
     * Method declaration
     *
     *
     * @param methodReference the method
     * @param traceStack the stack trace
     *
     * @throws TraceException
     */
    private void throwTraceException(MethodReference methodReference, 
				     TraceStack traceStack) {
	throw new TraceException(getProcessObserver(), 
				 getMappedClass().getSource(), this, 
				 methodReference, traceStack);
    } 

    /*
     * ....................................................................
     * . Protected Methods                                                .
     * ....................................................................
     */

    /**
     * Method declaration
     *
     *
     * @param space the space
     * @param state the state
     *
     * @return the dump
     *
     */
    protected String dump(String space, State state) {
	String s = "";

	s += state.toString();

	if (state == getActualState()) {
	    s = "(" + s + ")";
	} 

	Transition[] outgoing = state.getOutgoing();

	switch (outgoing.length) {

	case 0: {
	    break;
	} 

	case 1: {
	    Transition t = outgoing[0];

	    s += dump(t);

	    if (!t.isLoop()) {
		s += dump(space, outgoing[0].getSuccState());
	    } 

	    break;
	} 

	default: {
	    s += NEWLINE;
	    space += "  ";

	    for (int i = 0; i < outgoing.length; ++i) {
		Transition t = outgoing[i];

		s += space + dump(t);

		if (!t.isLoop()) {
		    s += dump(space, t.getSuccState()) + NEWLINE;
		} 
	    } 
	} 
	}

	return s;
    } 

    /**
     * Method declaration
     *
     *
     * @param t the transition
     *
     * @return the dump
     *
     */
    protected String dump(Transition t) {
	return " " + t.toString() + " ";
    } 

    /**
     * Method declaration
     *
     *
     * @param mappedMethod  the method
     * @param parameters  parameters for that method
     *
     * @return  the evaluation result
     *
     * @see
     */
    public boolean invokeAssociatedMethod(MappedMethod mappedMethod, 
					  Parameter[] parameters) {
	boolean  evaluation = false;
	Object[] parameterValues = new Object[parameters.length];
	Class[]  parameterClasses = new Class[parameters.length];
	String[] parameterTypenames = mappedMethod.getParameter();

	try {
	    for (int iParameter = 0; iParameter < parameters.length; 
		    ++iParameter) {
		Parameter parameter = parameters[iParameter];

		parameterValues[iParameter] = parameter.getValue();

		String parameterTypename = parameterTypenames[iParameter];
		Class  parameterClass;

		parameterClass = parameter.hasPrimitiveType() 
				 ? parameter.getValueClass() 
				 : Class.forName(parameterTypename);
		parameterClasses[iParameter] = parameterClass;

		// System.out.println("Parameter" + iParameter + ": " + parameter);
	    } 

	    Method method = 
		getMappedClass().getClass().getDeclaredMethod(mappedMethod.getMethodname(), 
		    parameterClasses);
	    Object object = method.invoke(mappedClass, parameterValues);

	    evaluation = ((Boolean) object).booleanValue();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	} 

	return evaluation;
    } 

    /*
     * static Method getMethod(Class mappedClass, String name, Class[] parameter)
     * {
     * Method method = null;
     * boolean methodFound = true;
     * try
     * {
     * method = mappedClass.getDeclaredMethod(name, parameter);
     * }
     * catch(NoSuchMethodException e)
     * {
     * methodFound = false;
     * }
     * return method;
     * }
     */

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */

    /**
     * Method declaration
     *
     *
     * @param methodReference the method event
     *
     * @return true, if the process can communicate that event
     *
     */
    public boolean canCommunicate(MethodReference methodReference) {
	boolean can = actualState.getTransition(methodReference) != null;

	// System.out.println("Can " + getName() + " communicate " + methodReference + "? " + can);
	return can;
    } 

    /*
     * public Object clone()
     * {
     * Process cloned = new Process();
     * cloned.alphabet = alphabet;
     * cloned.context = null;
     * cloned.name = name;
     * cloned.processes = processes;
     * cloned.referedProcesses = referedProcesses;
     * 
     * // This does not clone just the initial state, but clones recursivly the whole
     * // automaton from bottom up.
     * 
     * cloned.initialState = cloneProcess(initialState);
     * return cloned;
     * }
     */

    /**
     * Method declaration
     *
     *
     * @return the process code
     *
     */
    public String codeProcess() {
	return CODE_PROCESS + name;
    } 

    /**
     * Method declaration
     *
     *
     * @param traceStack the trace stack
     * @param methodReference the method
     * @param parameters the method's parameter
     *
     */
    public void communicate(TraceStack traceStack, 
			    MethodReference methodReference, 
			    Parameter[] parameters) {

	// System.out.println("##" + toString() + " receives " + methodReference);
	actualTraceStack = traceStack;
	actualMethodReference = methodReference;
	actualParameters = parameters;

	traceStack.setProcess(this);

	Transition transition;
	boolean    communicationFinished = false;
	boolean    communicationPossible = true;

	while (communicationPossible &&!communicationFinished) {
	    if (actualState instanceof ConditionalState) {
		ConditionalState conditionalState = 
		    (ConditionalState) actualState;

		conditionalState.evaluate(this);
	    } 

	    Communication communication;

	    if (methodReference instanceof InternalCommunication) {
		transition = 
		    ((InternalCommunication) methodReference).getTransition();
		communication = (Communication) methodReference;
	    } else {
		transition = actualState.getTransition(methodReference);

		if (transition == null) {
		    throwTraceException(methodReference, traceStack);
		} 

		communication = transition.getCommunication(methodReference);

		if (communication == null) {
		    communication = new InternalCommunication(transition);
		} 
	    } 

	    MappedMethod mappedMethodBefore = 
		communication.getMappedMethodBefore();

	    if (mappedMethodBefore != null) {
		boolean evaluation = 
		    invokeAssociatedMethod(mappedMethodBefore, parameters);

		if (evaluation == false) {
		    throw new ConditionException(getProcessObserver(), 
						 "Wrong parameter value(s): " 
						 + Parameter.toString(parameters), 
						 mappedMethodBefore, 
						 communication, traceStack);
		} 
	    } 

	    Condition condition = transition.getCondition();

	    if (condition != null && condition.evaluate(this) == false) {
		throw new ConditionException(getProcessObserver(), 
					     "Condition violated", 
					     condition.getMappedMethod(), 
					     communication, traceStack);
	    } 

	    Action action = transition.getAction();

	    if (action != null) {

		// System.out.println(getName() + " performs action " + action.toString());
		action.perform(this);
	    } 

	    MappedMethod mappedMethodAfter = 
		communication.getMappedMethodAfter();

	    if (mappedMethodAfter != null) {
		boolean evaluation = invokeAssociatedMethod(mappedMethodAfter, 
							    parameters);

		if (evaluation == false) {
		    throw new ConditionException(getProcessObserver(), 
						 "Wrong result: " 
						 + parameters[0], 
						 mappedMethodAfter, 
						 communication, traceStack);
		} 
	    } 

	    if (transition instanceof LookaheadTransition) {

		/*
		 * System.out.println
		 * (
		 * getName() + " of context " + context + " skiped lookahead "
		 * + (communication == null ? "" : communication.toString())
		 * );
		 */
	    } else {
		traceStack.communication(this, methodReference);

		communicationFinished = true;

		/*
		 * System.out.println(getName() + " of context " + context + " communicated " + communication.toString() + "");
		 */
	    } 

	    actualState = transition.getSuccState();

	    if (actualState.isFinal()) {
		communicationPossible = false;
	    } 

	    if (methodReference instanceof InternalCommunication) {
		communicationFinished = true;
	    } 
	} 

	if (actualState.isFinal()) {
	    if (actualState instanceof StateDeadlock 
		    &&!communicationFinished) {
		throwTraceException(methodReference, traceStack);
	    } 

	    terminate();
	} else {
	    boolean nearSTOP = 
		actualState.getPossibleCommunications().isEmpty();

	    if (nearSTOP) {
		if (!communicationFinished) {
		    throwTraceException(methodReference, traceStack);
		} 

		communicate(traceStack, 
			    new InternalCommunication(actualState.getOutgoing()[0]), 
			    new Parameter[0]);
	    } 
	} 

	// processes.announceStep(this);
    } 

    /**
     * Method declaration
     *
     *
     * @return the current method
     *
     */
    public MethodReference getActualReference() {
	return actualMethodReference;
    } 

    /**
     * Method declaration
     *
     *
     * @return the currents method's parameters
     *
     * @see getActualReference()
     */
    public Parameter[] getActualParameters() {
	return actualParameters;
    } 

    /**
     * Method declaration
     *
     *
     * @return the current state
     *
     */
    public State getActualState() {
	return actualState;
    } 

    /**
     * Method declaration
     *
     *
     * @return the current trace stack
     *
     */
    public TraceStack getActualTraceStack() {
	return actualTraceStack;
    } 

    /**
     * Method declaration
     *
     *
     * @return the current context
     *
     */
    public ProcessContext getContext() {
	return context;
    } 

    /**
     * Method declaration
     *
     *
     * @return the initial events
     *
     */
    public SetOfMethods getInitialCommunications() {
	return initialCommunications;
    } 

    /**
     * Method declaration
     *
     *
     * @returnthe mapped class
     *
     */
    public MappedClass getMappedClass() {
	return mappedClass;
    } 

    /**
     * Method declaration
     *
     *
     * @return the name
     *
     */
    public String getName() {
	return name;
    } 

    /**
     * Method declaration
     *
     *
     * @return the process observer
     *
     */
    public ProcessObserver getProcessObserver() {
	return processes;
    } 

    /**
     * Method declaration
     *
     *
     * @return the initial state
     *
     */
    public State getInitialState() {
	return initialState;
    } 

    // public String[] getReferedProcesses(){return referedProcesses;}

    /**
     * Method declaration
     *
     *
     * @return true, if the context is not null
     *
     */
    public boolean hasContext() {
	return context != null;
    } 

    /**
     * Method declaration
     *
     *
     * @return true, if the context is null
     *
     */
    public boolean isFree() {
	return !hasContext();
    } 

    /**
     * Method declaration
     *
     *
     * @param context the context
     * @param processes the process observer
     *
     */
    public void run(ProcessContext context, ProcessObserver processes) {
	this.context = context;
	this.processes = processes;
	actualState = initialState;

	if (initialState.getPossibleCommunications().isEmpty()) {
	    communicate(new TraceStack(getProcessObserver()), 
			new InternalCommunication(initialState.getOutgoing()[0]), 
			new Parameter[0]);
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param state the new state
     *
     */
    public void setActualState(State state) {
	actualState = state;
    } 

    /**
     * Method declaration
     *
     *
     * @param state the initial state
     *
     */
    public void setInitialState(State state) {
	initialState = state;
    } 

    /**
     * Method declaration
     *
     *
     * @param clazz the mapped class
     *
     */
    public void setMappedClass(MappedClass clazz) {
	this.mappedClass = clazz;
    } 

    /**
     * Method declaration
     *
     *
     * @param name the name
     *
     */
    public void setName(String name) {
	this.name = name;
    } 

    /**
     * Method declaration
     *
     *
     */
    public void terminate() {
	context.terminated(this);

	context = null;
    } 

    /**
     * Method declaration
     *
     *
     * @param lineIndent indention to use
     *
     * @return Java code for this(?) process
     *
     */
    public String toJava(String lineIndent) {
	this.lineIndent = lineIndent;
	code = new StringBuffer();

	IdNumber stateId = new IdNumber(IdNumber.START_VALUE);

	addCodeln("// ** Creation of process '" + getName() + "' **");
	addCodeln("");
	toJava(getInitialState(), stateId);
	addCodeln("");
	addCodeln("// Define process'" + getName() + "'");
	addCodeln(Process.CLASSNAME + " " + codeProcess() + " = " 
		  + Process.constructorString() + ";");
	addCodeln(codeProcess() + ".setMappedClass(this);");

	// addCodeln(codeProcess() + ".setAlphabet");
	// addCodeln("(");
	// addCodeln(getAlphabet().toJava(lineIndent + "  "));
	// addCodeln(");");
	addCodeln(codeProcess() + ".setInitialState(" 
		  + codeState(stateId.START_VALUE) + ");");
	addCodeln(codeProcess() + ".setName(\"" + getName() + "\");");

	String referedProcessesString = "";

	/*
	 * referedProcessesString += "new String[]{";
	 * for(int iProcess = 0; iProcess < referedProcesses.length; ++iProcess)
	 * {
	 * referedProcessesString += "\"" + referedProcesses[iProcess] + "\"";
	 * if(iProcess + 1 < referedProcesses.length)
	 * {
	 * referedProcessesString += ", ";
	 * }
	 * }
	 * referedProcessesString += "}";
	 * 
	 * addCodeln(codeProcess() + ".setReferedProcesses(" + referedProcessesString + ");");
	 */
	return code.toString();
    } 

    /**
     * Method declaration
     *
     *
     * @param state
     * @param idNumber
     *
     * @return
     *
     * @see
     */
    protected int toJava(State state, IdNumber idNumber) {
	int stateId = idNumber.create();

	addCodeln(state.getClass().getName() + " " + codeState(stateId) 
		  + " = " + state.toJava() + ";");

	Transition[] transitionList = state.getOutgoing();

	for (int transitionNr = 0; transitionNr < transitionList.length; 
		++transitionNr) {
	    Transition transition = transitionList[transitionNr];
	    State      nextState = transition.getSuccState();
	    int	       nextStateId;

	    if (transition.isLoop()) {
		nextStateId = stateId;
	    } else {
		nextStateId = toJava(nextState, idNumber);
	    } 

	    final String codeTransition = CODE_TRANSITION + stateId + "To" 
					  + nextStateId + "_" + getName();

	    addCodeln("");
	    addCodeln(transition.getClass().getName() + " " + codeTransition 
		      + " =");

	    code.append(transition.toJava(lineIndent + " ") + ";") 
		.append(ToJava.NEWLINE);

	    addCodeln(codeState(stateId) + ".addOutgoing(" + codeTransition 
		      + ");");
	    addCodeln(codeState(nextStateId) + ".addIncoming(" 
		      + codeTransition + ");");
	} 

	return stateId;
    } 

    /**
     * Method declaration
     *
     *
     * @return the dump
     *
     */
    public String dump() {
	String s = "";

	s += (getName() == null ? "<untitled>" : getName()) + NEWLINE;

	if (initialState != null) {
	    s += dump("", getInitialState());
	} else {
	    s += "<undefined>";
	} 

	s += NEWLINE + NEWLINE;

	return s;
    } 

    /**
     * Method declaration
     *
     *
     * @return the String
     */
    public String toString() {
	return name == null ? "<untitled>" : name;
    } 

    // Interface Freezable

    /**
     * Method declaration
     */
    public synchronized void freeze() {
	try {
	    wait();
	} catch (InterruptedException e) {

	    // do nothing
	} 
    } 

    /**
     * Method declaration
     */
    public synchronized void thaw() {
	notify();
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

