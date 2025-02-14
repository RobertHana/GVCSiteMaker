/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

// import java.util.Iterator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import javax.swing.tree.*;
import jass.runtime.traceAssertion.traceMonitor.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ProcessContext implements Communicator, Freezable {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    SetOfMethods     expandedAlphabet;
    SetOfMethods     syncAlphabet;
    ProcessObserver  processes;
    DefaultTreeModel treeModel;
    ContextNode      treeNode;
    Hashtable	     tableCommunicatorToNode;

    // HashSet concurrentProcesses;
    FrameMonitor     traceMonitor;

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    /*
     * public ProcessContext(ProcessObserver processes, Process process)
     * {
     * init(processes);
     * expandedAlphabet = determineExpandedAlphabet(process.getName(), new HashSet());
     * syncAlphabet = expandedAlphabet;
     * }
     * public ProcessContext(ProcessObserver processes, Process process1, Process process2)
     * {
     * init(processes);
     * String processname1 = process1.getName();
     * String processname2 = process2.getName();
     * 
     * SetOfMethods expandedAlphabet1 = determineExpandedAlphabet(processname1, new HashSet());
     * SetOfMethods expandedAlphabet2 = determineExpandedAlphabet(processname2, new HashSet());
     * expandedAlphabet = new SetOfMethods();
     * expandedAlphabet.add(expandedAlphabet1);
     * expandedAlphabet.add(expandedAlphabet2);
     * syncAlphabet = new SetOfMethods();
     * syncAlphabet.add(expandedAlphabet1);
     * syncAlphabet.section(expandedAlphabet2);
     * }
     */

    /**
     * Constructor declaration
     *
     *
     * @param processes
     * @param processArray
     *
     * @see
     */
    public ProcessContext(ProcessObserver processes, Process[] processArray) {
	init(processes);

	int     numberOfProcesses = processArray.length;
	Process firstProcess = processArray[0];

	expandedAlphabet = determineExpandedAlphabet(processes, 
		firstProcess.getName(), new HashSet());
	syncAlphabet = new SetOfMethods();

	addCommunicator(firstProcess);

	switch (numberOfProcesses) {

	case 1: {

	    // all done
	    break;
	} 

	case 2: {
	    Process      secondProcess = processArray[1];
	    SetOfMethods secondExpandedAlphabet = 
		determineExpandedAlphabet(processes, secondProcess.getName(), 
					  new HashSet());

	    // First determine sync alphabet then extend expanded alphabet
	    syncAlphabet.add(expandedAlphabet);
	    syncAlphabet.section(secondExpandedAlphabet);
	    expandedAlphabet.add(secondExpandedAlphabet);
	    addCommunicator(secondProcess);

	    break;
	} 

	default: {
	    Process[] newArray = new Process[numberOfProcesses - 1];

	    for (int iProcess = 1; iProcess < processArray.length; 
		    ++iProcess) {
		newArray[iProcess - 1] = processArray[iProcess];
	    } 

	    ProcessContext newProcessContext = new ProcessContext(processes, 
		    newArray);
	    SetOfMethods   secondExpandedAlphabet = 
		newProcessContext.getExpandedAlphabet();

	    expandedAlphabet.add(secondExpandedAlphabet);
	    syncAlphabet.add(expandedAlphabet);
	    syncAlphabet.section(secondExpandedAlphabet);
	    addCommunicator(newProcessContext);
	} 
	}

	// System.out.println("###ROOT:" + isRoot() + "#" + hashCode());

	/*
	 * System.out.println("##" + this);
	 * System.out.println("##Expanded Alphabet:");
	 * System.out.println(expandedAlphabet);
	 * System.out.println("##Sync Alphabet:");
	 * System.out.println(syncAlphabet);
	 * System.out.println();
	 */
    }

    /*
     * ....................................................................
     * . Private Methods                                                  .
     * ....................................................................
     */

    /**
     * Method declaration
     *
     *
     * @param processes
     *
     * @see
     */
    private void init(ProcessObserver processes) {
	this.processes = processes;
	treeNode = new ContextNode(this);
	tableCommunicatorToNode = new Hashtable();
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
     * @param communicator
     *
     * @see
     */
    protected void addCommunicator(Communicator communicator) {
	ContextNode node = new ContextNode(communicator);

	addCommunicator(communicator, node);
    } 

    /**
     * Method declaration
     *
     *
     * @param communicator
     * @param node
     *
     * @see
     */
    protected void addCommunicator(Communicator communicator, 
				   ContextNode node) {
	if (treeModel != null) {
	    treeModel.insertNodeInto(node, getNode(), 
				     getNode().getChildCount());
	    traceMonitor.getProcessTree().expandPath(new TreePath(node.getPath()));
	    traceMonitor.getProcessTree().makeVisible(new TreePath(node.getPath()));
	} else {
	    treeNode.add(node);
	} 

	// System.out.println("##" + communicator + " added to " + this + "##");
	tableCommunicatorToNode.put(communicator, node);

	if (communicator instanceof Process) {
	    Process process = (Process) communicator;

	    process.run(this, processes);
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param methodReference
     *
     * @return
     *
     * @see
     */
    protected Communicator communicator(MethodReference methodReference) {
	Communicator[] communicatorArray = communicatorArray();
	int	       numberOfPotentialCommunicators = 
	    communicatorArray.length;

	// Iterator communicatorIteration = concurrentProcesses.iterator();
	boolean	       found = false;
	Communicator   potentialCommunicator = null;

	for (int iCommunicator = 0; 
		!found && iCommunicator < numberOfPotentialCommunicators; 
		++iCommunicator) 

	// while(!found && communicatorIteration.hasNext())
	{
	    potentialCommunicator = communicatorArray[iCommunicator];

	    // potentialCommunicator = (Communicator) communicatorIteration.next();
	    found = potentialCommunicator.canCommunicate(methodReference);
	} 

	return found ? potentialCommunicator : null;
    } 

    /**
     * Method declaration
     *
     *
     * @param process
     *
     * @return
     *
     * @see
     */
    protected Communicator communicator(Process process) {
	ContextNode node = communicatorToNode(process);

	return node.getObject();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    protected Communicator[] communicatorArray() {
	int	       communicatorCount = treeNode.getChildCount();
	Communicator[] communicatorArray = 
	    new Communicator[communicatorCount];

	for (int iCommunicator = 0; iCommunicator < communicatorCount; 
		++iCommunicator) {
	    ContextNode node = 
		(ContextNode) treeNode.getChildAt(iCommunicator);

	    communicatorArray[iCommunicator] = node.getObject();
	} 

	return communicatorArray;
    } 

    /**
     * Method declaration
     *
     *
     * @param c
     *
     * @return
     *
     * @see
     */
    protected ContextNode communicatorToNode(Communicator c) {
	return (ContextNode) tableCommunicatorToNode.get(c);
    } 

    /**
     * Method declaration
     *
     *
     * @param processes
     * @param processname
     * @param visitedProcesses
     *
     * @return
     *
     * @see
     */
    protected static SetOfMethods determineExpandedAlphabet(ProcessObserver processes, 
	    String processname, HashSet visitedProcesses) {

	// System.out.println(process.getName() + "#" + process.getAlphabet());
	SetOfMethods alphabet = new SetOfMethods();

	if (!visitedProcesses.contains(processname)) {
	    alphabet.add(processes.getAlphabet(processname));
	    visitedProcesses.add(processname);

	    String[] referedProcesses = 
		processes.getReferedProcesses(processname);

	    for (int iProcessName = 0; 
		    iProcessName < referedProcesses.length; ++iProcessName) {
		String referedProcessname = referedProcesses[iProcessName];

		alphabet.add(determineExpandedAlphabet(processes, 
						       referedProcessname, 
						       visitedProcesses));
	    } 
	} 

	return alphabet;
    } 

    /**
     * Method declaration
     *
     *
     * @param communicator
     *
     * @see
     */
    protected void removeCommunicator(Communicator communicator) {

	// System.out.println("##Remove " + communicator + " from " + this);
	ContextNode node = communicatorToNode(communicator);

	// System.out.println("##Communicator#Node:" + communicator + ":" + node);

	/*
	 * if(communicator instanceof Process)
	 * {
	 * System.out.print("Remove " + processes.getIdentification((Process) communicator).name + " ... ");
	 * }
	 */
	if (treeModel != null) {
	    treeModel.removeNodeFromParent(node);
	} else {
	    treeNode.remove(node);
	} 
    } 

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */

    /*
     * public void add(Process process)
     * {
     * addCommunicator(process);
     * HashSet visitedProcesses = new HashSet();
     * visitedProcesses.add(process);
     * SetOfMethods expandedAlphabetOfNewProcess = processes.getAlphabet(process);
     * String[] referedProcesses = processes.getReferedProcesses(process);
     * for(int iProcessName = 0; iProcessName < referedProcesses.length; ++iProcessName)
     * {
     * String referedProcessname = referedProcesses[iProcessName];
     * SetOfMethods expandedAlphabetOfReferer
     * = determineExpandedAlphabet(referedProcessname, visitedProcesses);
     * expandedAlphabetOfNewProcess.add(expandedAlphabetOfReferer);
     * }
     * if(expandedAlphabet == null)
     * {
     * expandedAlphabet = new SetOfMethods(expandedAlphabetOfNewProcess);
     * }
     * else
     * {
     * expandedAlphabet.add(expandedAlphabetOfNewProcess);
     * }
     * 
     * if(syncAlphabet == null)
     * {
     * syncAlphabet = new SetOfMethods(expandedAlphabetOfNewProcess);
     * }
     * else
     * {
     * syncAlphabet.section(expandedAlphabetOfNewProcess);
     * }
     * System.out.println(process.getName() + " added to context: " + toString());
     * System.out.println("-> expandedAlphabet updated to " + expandedAlphabet);
     * System.out.println("-> syncAlphabet updated to " + syncAlphabet);
     * System.out.println("");
     * }
     */

    /*
     * public void add(Process[] listProcess)
     * {
     * for(int iProcess = 0; iProcess < listProcess.length; ++iProcess)
     * {
     * add(listProcess[iProcess]);
     * }
     * }
     */

    /**
     * Method declaration
     *
     *
     * @param oldProcess
     * @param newProcessArray
     *
     * @return
     *
     * @see
     */
    public Communicator change(Process oldProcess, 
			       Process[] newProcessArray) {
	Communicator newCommunicator = null;

	if (newProcessArray.length == 1) {
	    Process process = newProcessArray[0];

	    process.run(this, processes);

	    newCommunicator = process;
	} else {
	    ProcessContext newProcessContext = new ProcessContext(processes, 
		    newProcessArray);

	    newProcessContext.getNode().setParent(getNode());

	    newCommunicator = newProcessContext;
	} 

	TreeNode    oldNode = 
	    treeNode.getChildAt(treeNode.getIndex(communicatorToNode(oldProcess)));
	ContextNode oldContextNode = (ContextNode) oldNode;

	oldContextNode.setObject(newCommunicator);
	tableCommunicatorToNode.remove(oldProcess);
	tableCommunicatorToNode.put(newCommunicator, oldNode);
	processes.getTraceAssertion().getTraceStack().newProcessContext(processes.getTraceAssertion().getMainContext().toString());

	return newCommunicator;
    } 

    /**
     * Method declaration
     *
     *
     * @param process
     *
     * @return
     *
     * @see
     */
    public boolean contains(Process process) {
	return treeNode.isNodeChild(communicatorToNode(process));
    } 

    /**
     * Method declaration
     *
     *
     * @param process
     *
     * @see
     */
    public void remove(Communicator process) {
	removeCommunicator(process);

	// System.out.println("##ROOT:" + processes.getTraceAssertion().getMainContext() + ":" + isEmpty() + ":" + isRoot());
	if (isEmpty() &&!isRoot()) {
	    ProcessContext parentContext = treeNode.getParentContext();

	    parentContext.remove(this);
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
    public boolean isEmpty() {
	return treeNode.isLeaf();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isRoot() {
	return treeNode.isRoot();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public SetOfMethods getExpandedAlphabet() {
	return expandedAlphabet;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public ContextNode getNode() {
	return treeNode;
    } 

    /*
     * public ProcessContext getRootContext()
     * {
     * ProcessContext root = this;
     * TreeNode node;
     * 
     * while((node = root.getNode().getParent()) != null)
     * {
     * ContextNode contextNode = (ContextNode) node;
     * root = (ProcessContext) contextNode.getParentContext().getObject();
     * }
     * return root;
     * }
     */

    /**
     * Method declaration
     *
     *
     * @param methodReference
     *
     * @return
     *
     * @see
     */
    public boolean canCommunicate(MethodReference methodReference) {
	boolean can;

	if (!isEmpty() && syncAlphabet.contains(methodReference)) {
	    Communicator[] communicatorArray = communicatorArray();
	    int		   communicatorCount = communicatorArray.length;

	    // Iterator communicatorIteration = concurrentProcesses.iterator();
	    can = true;

	    for (int iCommunicator = 0; 
		    can && iCommunicator < communicatorCount; 
		    ++iCommunicator) 

	    // while(can && communicatorIteration.hasNext())
	    {
		Communicator communicator = communicatorArray[iCommunicator];

		can = can && communicator.canCommunicate(methodReference);

		// can = can && ((Communicator) communicatorIteration.next()).canCommunicate(communication);
	    } 
	} else {
	    can = (communicator(methodReference) != null);
	} 

	// System.out.println("Can " + toString() + " communicate " + communication + "? " + can);
	return can;
    } 

    /**
     * Method declaration
     *
     *
     * @param traceStack
     * @param methodReference
     * @param parameters
     *
     * @see
     */
    public void communicate(TraceStack traceStack, 
			    MethodReference methodReference, 
			    Parameter[] parameters) {

	// System.out.println("##" + toString() + " receives " + methodReference);
	if (!isEmpty() && syncAlphabet.contains(methodReference)) {
	    Communicator[] communicatorArray = communicatorArray();
	    int		   communicatorCount = communicatorArray.length;

	    for (int iCommunicator = 0; iCommunicator < communicatorCount; 
		    ++iCommunicator) {
		Communicator communicator = communicatorArray[iCommunicator];

		communicator.communicate(traceStack, methodReference, 
					 parameters);
	    } 
	} else {
	    Communicator communicator = communicator(methodReference);

	    if (communicator == null) {
		throw new TraceException(processes, null, null, 
					 methodReference, traceStack);
	    } 

	    /*
	     * if(communicator instanceof Process)
	     * {
	     * Process process = (Process) communicator;
	     * 
	     * System.out.println
	     * (
	     * "Communicator found is " + processes.getIdentification(process).toString()
	     * );
	     * }
	     * else if(communicator instanceof ProcessContext)
	     * {
	     * System.out.println(((ProcessContext) communicator).toString());
	     * }
	     */
	    communicator.communicate(traceStack, methodReference, parameters);
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param traceMonitor
     * @param treeModel
     *
     * @see
     */
    public void setMonitoring(FrameMonitor traceMonitor, 
			      DefaultTreeModel treeModel) {
	this.traceMonitor = traceMonitor;
	this.treeModel = treeModel;
    } 

    /**
     * Method declaration
     *
     *
     * @param process
     *
     * @see
     */
    public void terminated(Process process) {
	TraceAssertion traceAssertion = processes.getTraceAssertion();

	if (contains(process)) {
	    remove(process);

	    ProcessContext mainContext = traceAssertion.getMainContext();

	    // mainContext will be 'null' if the process terminated while its run-method
	    // is invoked. This is when the process starts and terminates right after.
	    // An example process: MAIN() { STOP }
	    if (mainContext != null) {
		traceAssertion.getTraceStack().newProcessContext(traceAssertion.getMainContext().toString());
	    } 
	} 

	if (isEmpty()) {
	    if (isRoot()) {
		State finalState = process.getActualState();

		if (finalState instanceof StateForNewBasicProcess) {
		    StateForNewBasicProcess basicState = 
			(StateForNewBasicProcess) finalState;

		    traceAssertion.basicProcessReached(basicState);
		} 
	    } 
	} 

	tableCommunicatorToNode.remove(process);
	processes.terminated(process);
    } 

    // Interface Freezable

    /**
     * Method declaration
     *
     *
     * @see
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
     *
     *
     * @see
     */
    public synchronized void thaw() {
	notify();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String toString() {
	String s = "";

	s += "[";

	Communicator[] communicatorArray = communicatorArray();
	int	       communicatorCount = communicatorArray.length;

	for (int iCommunicator = 0; iCommunicator < communicatorCount; 
		++iCommunicator) {
	    Communicator communicator = communicatorArray[iCommunicator];

	    if (communicator instanceof Process) {
		Process process = (Process) communicator;

		s += processes.getIdentification(process).toString();
	    } else if (communicator instanceof ProcessContext) {
		s += ((ProcessContext) communicator).toString();
	    } 

	    if (iCommunicator + 1 < communicatorCount) {
		s += ",";
	    } 
	} 

	s += "]";

	return s;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

