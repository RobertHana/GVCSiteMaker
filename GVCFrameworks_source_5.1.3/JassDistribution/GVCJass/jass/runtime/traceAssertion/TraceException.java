/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import jass.runtime.AssertionException;
import jass.runtime.util.ToString;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class TraceException extends TraceAssertionException {

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
     * @param process
     * @param methodReference
     * @param traceStack
     *
     * @see
     */
    public TraceException(ProcessObserver observer, Source source, 
			  Process process, MethodReference methodReference, 
			  TraceStack traceStack) {
	super(observer, source, 
	      "Invalid communication: " + methodReference.toString() 
	      + (process == null ? "" 
		 : ToString.NEWLINE + "  at process " 
		   + observer.getIdentification(process).toString()), traceStack);
    }

    /*
     * public TraceException(String className, String methodName, int lineNumber, String message)
     * {
     * super(className, methodName, lineNumber, message);
     * }
     */
}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

