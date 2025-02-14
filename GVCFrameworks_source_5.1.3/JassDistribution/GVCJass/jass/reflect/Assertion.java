/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.util.Vector;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public abstract class Assertion {

    /**
     * The container, the assertion is contained in. Can be class (for invariant only) or method.
     */
    protected Context container;

    /**
     * Method declaration
     *
     *
     * @see
     */
    public abstract void reflectExpressions();

    /**
     * Method declaration
     *
     *
     * @param exprs
     *
     * @see
     */
    public abstract void setAssertionExpressions(Vector exprs);

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Context getContainer() {
	return container;
    } 

    /**
     * Method declaration
     *
     *
     * @param c
     *
     * @see
     */
    public void setContainer(Context c) {
	container = c;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

