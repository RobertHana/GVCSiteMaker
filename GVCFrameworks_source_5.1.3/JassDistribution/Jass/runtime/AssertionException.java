/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class AssertionException extends RuntimeException {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    public int    lineNumber = -1;
    public String className = null;
    public String methodName = null;
    public String message = null;

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public AssertionException() {}

    /**
     * Constructor declaration
     *
     *
     * @param message
     *
     * @see
     */
    public AssertionException(String message) {
	super(message);
    }

    /**
     * Constructor declaration
     *
     *
     * @param className
     * @param methodName
     * @param lineNumber
     * @param message
     *
     * @see
     */
    public AssertionException(String className, String methodName, 
			      int lineNumber, String message) {
	super(className + (methodName != null ? "." + methodName : "") 
	      + (lineNumber > 0 ? ":" + lineNumber : "") 
	      + (message != null ? " <" + message + ">" : ""));

	this.lineNumber = lineNumber;
	this.className = className;
	this.methodName = methodName;
	this.message = message;
    }

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

