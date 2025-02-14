/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Stack;
import jass.runtime.util.ToString;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class TraceStack {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    final static String TA = "TA@";
    ProcessObserver     processObserver;
    Process		process;
    Stack		stack;

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    /**
     * Constructor declaration
     *
     *
     * @param observer
     *
     * @see
     */
    public TraceStack(ProcessObserver observer) {
	this.processObserver = observer;
	stack = new Stack();
    }

    /**
     * Method declaration
     *
     *
     * @param entry
     *
     * @see
     */
    void newEntry(TraceStackEntry entry) {
	stack.push(entry);

	/*
	 * System.out.println
	 * (
	 * TA + Integer.toHexString
	 * (
	 * processObserver.getTraceAssertion().hashCode()
	 * ) + ":" + entry.message()
	 * );
	 */
    } 

    /**
     * Method declaration
     *
     *
     * @param string
     *
     * @return
     *
     * @see
     */
    String headline(String string) {
	final char style = '*';
	final int  maxChars = 78;
	String     headline = " " + string + " ";

	headline = line(style, (maxChars - headline.length()) / 2) + headline;
	headline += line(style, maxChars - headline.length());

	return headline;
    } 

    /**
     * Method declaration
     *
     *
     * @param style
     * @param num
     *
     * @return
     *
     * @see
     */
    String line(char style, int num) {
	String line = "";

	for (int iChar = 0; iChar < num; ++iChar) {
	    line += style;
	} 

	return line;
    } 

    /**
     * Method declaration
     *
     *
     * @param string
     *
     * @return
     *
     * @see
     */
    String underlined(String string) {
	final char style = '-';
	String     underlined = string;

	underlined += ToString.NEWLINE + line(style, string.length());

	return underlined;
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
    public Process getProcess() {
	return process;
    } 

    /**
     * Method declaration
     *
     *
     * @param process
     * @param method
     *
     * @see
     */
    public void communication(Process process, MethodReference method) {
	TraceStackEntryCommunication entry = 
	    new TraceStackEntryCommunication(process, method);

	newEntry(entry);
    } 

    /**
     * Method declaration
     *
     *
     * @param context
     *
     * @see
     */
    public void newProcessContext(String context) {
	TraceStackEntryContext entry = new TraceStackEntryContext(context);

	newEntry(entry);
    } 

    /**
     * Method declaration
     *
     *
     * @param process
     *
     * @see
     */
    public void processCreation(String process) {
	TraceStackEntryCreation entry = new TraceStackEntryCreation(process);

	newEntry(entry);
    } 

    /**
     * Method declaration
     *
     *
     * @param process
     *
     * @see
     */
    public void processTermination(String process) {
	TraceStackEntryDestruction entry = 
	    new TraceStackEntryDestruction(process);

	newEntry(entry);
    } 

    /**
     * Method declaration
     *
     *
     * @param exception
     * @param out
     *
     * @see
     */
    public void dump(Exception exception, PrintStream out) {
	TraceAssertion traceAssertion = processObserver.getTraceAssertion();

	out.println(headline("General Information"));
	out.println();
	out.println("Exception: " + exception.toString());
	out.println("Object: " + CommunicationManager.getActualCaller());
	out.println("Trace Assertion Object: " + traceAssertion.toString());
	out.println();
	out.println(headline("Active process(es)"));
	out.println();
	out.println("Context: " + traceAssertion.getMainContext().toString());
	out.println();

	Process[] processes = processObserver.processes();

	for (int iProcess = 0; iProcess < processes.length; ++iProcess) {
	    Process process = processes[iProcess];

	    out.println(underlined("Process: " 
				   + processObserver.getIdentification(process)));
	    out.println(process.getMappedClass().toString());
	} 

	out.println();
	out.println(headline("Trace Stack"));
	out.println();

	Iterator iterator = stack.iterator();

	while (iterator.hasNext()) {
	    TraceStackEntry entry = (TraceStackEntry) iterator.next();

	    entry.dump(out);
	    out.println(" ");
	} 

	out.println();
	out.println(headline("end"));
    } 

    /**
     * Method declaration
     *
     *
     * @param process
     *
     * @see
     */
    public void setProcess(Process process) {
	this.process = process;
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
    public static String toString(Process process) {
	String string = "";

	if (process != null) {
	    string += "Actual process: " + ToString.NEWLINE 
		      + process.getMappedClass().toString() 
		      + ToString.NEWLINE + ToString.NEWLINE;
	} 

	return string;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

