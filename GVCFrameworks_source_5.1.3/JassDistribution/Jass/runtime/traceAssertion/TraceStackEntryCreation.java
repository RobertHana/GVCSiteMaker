/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import jass.runtime.util.ToString;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class TraceStackEntryCreation extends TraceStackEntry {
    String process;

    /**
     * Constructor declaration
     *
     *
     * @param process
     *
     * @see
     */
    public TraceStackEntryCreation(String process) {
	super();

	this.process = process;
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

	string += process + "  CREATED";

	return string;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

