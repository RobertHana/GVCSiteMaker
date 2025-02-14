/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import java.io.PrintStream;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public abstract class TraceStackEntry {

    /**
     * Method declaration
     *
     *
     * @param out
     *
     * @see
     */
    public void dump(PrintStream out) {
	out.print(message());
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public abstract String message();
}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

