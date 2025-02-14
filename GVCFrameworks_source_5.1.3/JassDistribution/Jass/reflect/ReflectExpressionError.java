/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import jass.reflect.Class;
import jass.runtime.traceAssertion.TraceAssertionException;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ReflectExpressionError extends ReflectionError {

    /**
     * Constructor declaration
     *
     *
     * @param msg
     * @param expression
     *
     * @see
     */
    public ReflectExpressionError(String msg, FileBounded expression) {
	super(errorString(expression) + msg);
    }

    /**
     * Method declaration
     *
     *
     * @param expression
     *
     * @return
     *
     * @see
     */
    public static String errorString(FileBounded expression) {
	String string = "";

	try {
	    string = expression.toString();
	} catch (Exception e) {

	    // do nothing
	} 

	string += 
	    TraceAssertionException.enclosedErrorString(expression.getContainer().getName(), 
		expression.getLine());

	return string;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

