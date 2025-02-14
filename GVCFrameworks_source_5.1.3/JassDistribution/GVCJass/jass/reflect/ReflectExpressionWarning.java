/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ReflectExpressionWarning {
    String message;

    /**
     * Constructor declaration
     *
     *
     * @param message
     * @param expression
     *
     * @see
     */
    public ReflectExpressionWarning(String message, FileBounded expression) {
	this.message = "WARNING: " 
		       + ReflectExpressionError.errorString(expression) 
		       + message;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String toString() {
	return message;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

