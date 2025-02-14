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
public class TraceStackEntryContext extends TraceStackEntry {
    String processContext;

    /**
     * Constructor declaration
     *
     *
     * @param processContext
     *
     * @see
     */
    public TraceStackEntryContext(String processContext) {
	super();

	this.processContext = processContext;
    }

    /**
     * Method declaration
     *
     *
     * @param out
     *
     * @see
     */
    public void dump(PrintStream out) {
	out.println();

	String message = message();
	String line = "";

	for (int i = 0; i < message.length(); ++i) {
	    line += "-";
	} 

	out.println(message);
	;
	out.print(line);
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

	string += "New Process Context: " + processContext;

	return string;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

