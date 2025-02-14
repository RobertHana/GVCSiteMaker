/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import jass.runtime.traceAssertion.TraceAssertion;
import jass.runtime.traceAssertion.traceMonitor.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ProcessObserver {
    public final static String CLASSNAME = 
	"jass.runtime.traceAssertion.ProcessObserver";

    /**
     * Class declaration
     *
     *
     * @author
     * @version %I%, %G%
     */
    public class Identification {
	String name;
	int    number;

	/**
	 * Constructor declaration
	 *
	 *
	 * @param name
	 * @param number
	 *
	 * @see
	 */
	public Identification(String name, int number) {
	    this.name = name;
	    this.number = number;
	}

	/**
	 * Method declaration
	 *
	 *
	 * @return
	 *
	 * @see
	 */
	public String hashString() {
	    return name + "#" + number;
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
	    return hashString();
	} 

    }

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    TraceAssertion traceAssertion;
    Hashtable      tableIdentificationToProcess;
    Hashtable      tableProcessToIdentification;
    Hashtable      tableProcessNameToCurrentIdNumber;
    FrameMonitor   traceMonitor;

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    /**
     * Constructor declaration
     *
     *
     * @param traceAssertion
     *
     * @see
     */
    public ProcessObserver(TraceAssertion traceAssertion) {
	this.traceAssertion = traceAssertion;
	tableIdentificationToProcess = new Hashtable();
	tableProcessToIdentification = new Hashtable();
	tableProcessNameToCurrentIdNumber = new Hashtable();
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public static String constructorString() {
	return "new " + CLASSNAME + "()";
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
     * @param process
     * @param idNumber
     *
     * @see
     */
    protected void define(Process process, int idNumber) {
	Identification id = new Identification(process.getName(), idNumber);

	tableProcessToIdentification.put(process, id);
	tableIdentificationToProcess.put(id.hashString(), process);
	getTraceAssertion().getTraceStack().processCreation(id.toString());
    } 

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
    public Class getMappedClass(String processname) {
	String mappedClassname = traceAssertion.getClassname(processname);
	Class  mappedClass = null;

	try {
	    mappedClass = Class.forName(mappedClassname);
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	} 

	return mappedClass;
    } 

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Process[] processes() {
	Process[]   listProcess = 
	    new Process[tableIdentificationToProcess.size()];
	Enumeration enumeration = tableIdentificationToProcess.elements();
	int	    iProcess = 0;

	while (enumeration.hasMoreElements()) {
	    listProcess[iProcess++] = ((Process) enumeration.nextElement());
	} 

	return listProcess;
    } 

    /**
     * Method declaration
     *
     *
     * @param f
     *
     * @see
     */
    public void announceStep(Freezable f) {
	if (traceMonitor != null) {
	    traceMonitor.announceStep(f);
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param f
     *
     * @see
     */
    public void announceTrace(Freezable f) {
	if (traceMonitor != null) {
	    traceMonitor.announceTrace(f);
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param process
     *
     * @see
     */
    public void define(Process process) {
	IdNumber idNumber = new IdNumber(IdNumber.START_VALUE);

	tableProcessNameToCurrentIdNumber.put(process.getName(), idNumber);
	define(process, idNumber.create());
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
    public SetOfMethods getAlphabet(Process process) {
	return getAlphabet(process.getName());
    } 

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
    public SetOfMethods getAlphabet(String processname) {
	SetOfMethods alphabet = null;

	try {
	    Field fieldAlphabet = 
		getMappedClass(processname).getDeclaredField(jass.reflect.traceAssertion.Process.FIELDNAME_ALPHABET);

	    alphabet = (SetOfMethods) fieldAlphabet.get(null);
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	} 

	return alphabet;
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
    public Identification getIdentification(Process process) {
	return (Identification) tableProcessToIdentification.get(process);
    } 

    /**
     * Method declaration
     *
     *
     * @param id
     *
     * @return
     *
     * @see
     */
    public Process getProcess(Identification id) {
	return (Process) tableIdentificationToProcess.get(id.hashString());
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
    public String[] getReferedProcesses(Process process) {
	return getReferedProcesses(process.getName());
    } 

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
    public String[] getReferedProcesses(String processname) {
	String[] referedProcesses = null;

	try {
	    Field fieldReferedProcesses = 
		getMappedClass(processname).getDeclaredField(jass.reflect.traceAssertion.Process.FIELDNAME_REFEREDPROCESSES);

	    referedProcesses = (String[]) fieldReferedProcesses.get(null);
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	} 

	return referedProcesses;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public TraceAssertion getTraceAssertion() {
	return traceAssertion;
    } 

    /**
     * Method declaration
     *
     *
     * @param referer
     * @param methodname
     *
     * @return
     *
     * @see
     */
    public Process[] newInstances(Process referer, String methodname) {
	MappedClass[] mappedInstances = null;

	try {
	    Class  mappedClass = getMappedClass(referer.getName());

	    // System.out.println("##New Process, mappedClass:" + mappedClass);
	    Method method = mappedClass.getDeclaredMethod(methodname, 
		    new Class[0]);

	    // System.out.println("##New Process, method:" + method);
	    Object object = method.invoke(referer.getMappedClass(), 
					  new Object[0]);

	    // System.out.println("##New Process, object:" + object);
	    mappedInstances = (MappedClass[]) object;
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	} 

	Process[] processes = new Process[mappedInstances.length];

	for (int iInstance = 0; iInstance < mappedInstances.length; 
		++iInstance) {
	    MappedClass mappedInstance = mappedInstances[iInstance];
	    Process     process = mappedInstance.constructProcess();

	    processes[iInstance] = process;

	    IdNumber currentProcessIdNumber = 
		(IdNumber) tableProcessNameToCurrentIdNumber.get(process.getName());

	    if (currentProcessIdNumber != null) {
		define(process, currentProcessIdNumber.create());
	    } else {
		define(process);
	    } 
	} 

	return processes;
    } 

    /**
     * Method declaration
     *
     *
     * @param monitor
     *
     * @see
     */
    public void setTraceMonitor(FrameMonitor monitor) {
	traceMonitor = monitor;
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
	traceAssertion.getTraceStack().processTermination(getIdentification(process).toString());

	Identification id = getIdentification(process);

	tableIdentificationToProcess.remove(id.hashString());
	tableProcessToIdentification.remove(process);
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

