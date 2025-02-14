/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import java.io.PrintStream;
import jass.runtime.util.ToString;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class TraceStackEntryCommunication extends TraceStackEntry {
    String communication;
    String process;

    /**
     * Constructor declaration
     *
     *
     * @param process
     * @param method
     *
     * @see
     */
    public TraceStackEntryCommunication(Process process, 
					MethodReference method) {
	super();

	this.communication = method.toString();
	this.process = 
	    process.getProcessObserver().getIdentification(process).toString();
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String message() {
	String string = "";

	string += communication + " -> " + process;

	return string;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

