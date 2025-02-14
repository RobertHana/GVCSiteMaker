/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import jass.runtime.AssertionException;
import jass.runtime.util.ToJava;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ConditionException extends TraceAssertionException {

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
     * @param message
     * @param mappedMethod
     * @param communication
     * @param traceStack
     *
     * @see
     */
    public ConditionException(ProcessObserver observer, String message, 
			      MappedMethod mappedMethod, 
			      Communication communication, 
			      TraceStack traceStack) {
	super(observer, mappedMethod.getSource(), 
	      message + ToJava.NEWLINE + "  while " 
	      + communication.toString() + ToJava.NEWLINE + "  at " 
	      + mappedMethod.getCode(), traceStack);
    }

    /*
     * public ConditionException(String className, String methodName, int lineNumber, String message)
     * {
     * super(className, methodName, lineNumber, message);
     * }
     */
}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

