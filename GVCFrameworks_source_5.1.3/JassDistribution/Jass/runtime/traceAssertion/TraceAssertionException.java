/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import java.io.*;
import jass.runtime.AssertionException;
import jass.runtime.util.ToString;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class TraceAssertionException extends AssertionException {
    TraceStack traceStack;

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
     * @param source
     * @param message
     * @param traceStack
     *
     * @see
     */
    public TraceAssertionException(ProcessObserver observer, Source source, 
				   String message, TraceStack traceStack) {
	super(message + ToString.NEWLINE + "  at trace assertion " 
	      + observer.getTraceAssertion().getName() 
	      + (source == null ? "" 
		 : enclosedErrorString(source.getFilename() + ".jass", 
				       source.getLineNr())));

	this.traceStack = traceStack;

	// source auch null
    }

    /**
     * Method declaration
     *
     *
     * @param filename
     * @param lineNr
     *
     * @return
     *
     * @see
     */
    public static String enclosedErrorString(String filename, int lineNr) {
	String string = "";

	string += "(" + errorString(filename, lineNr) + ")" + " ";

	return string;
    } 

    /**
     * Method declaration
     *
     *
     * @param filename
     * @param lineNr
     *
     * @return
     *
     * @see
     */
    public static String errorString(String filename, int lineNr) {
	String string = "";

	string += filename + ":" + lineNr;

	return string;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void printStackTrace() {
	printStackTrace(System.err);
    } 

    /**
     * Method declaration
     *
     *
     * @param out
     *
     * @see
     */
    public void printStackTrace(PrintStream out) {
	super.printStackTrace(out);

	File dumpFile = new File(TraceAssertion.CLASS_IDENTIFIERPREFIX 
				 + "traceStack");

	try {
	    System.out.print("Dump Trace Stack to file " + dumpFile + "... ");

	    PrintStream dumpStream = 
		new PrintStream(new FileOutputStream(dumpFile));

	    traceStack.dump(this, dumpStream);
	    dumpStream.close();
	    System.out.println("finished!");
	} catch (Exception e) {
	    String streamName;

	    if (out.equals(System.err)) {
		streamName = "standard error stream";
	    } else if (out.equals(System.out)) {
		streamName = "standard output stream";
	    } else {
		streamName = out.toString();
	    } 

	    System.out.println("failed!");
	    System.out.println("Dump Trace Stack to " + streamName);
	    traceStack.dump(this, System.err);
	} 
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

