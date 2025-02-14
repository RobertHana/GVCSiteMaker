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
public class TraceStackEntryDestruction extends TraceStackEntryCreation {

    /**
     * Constructor declaration
     *
     *
     * @param process
     *
     * @see
     */
    public TraceStackEntryDestruction(String process) {
	super(process);
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

	string += process + "  DESTROYED";

	return string;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

