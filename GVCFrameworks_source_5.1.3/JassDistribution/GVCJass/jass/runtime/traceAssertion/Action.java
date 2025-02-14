/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public abstract class Action {

    /**
     * Method declaration
     *
     *
     * @param parent
     *
     * @return
     *
     * @see
     */
    public abstract Object perform(Process parent);

    /**
     * Method declaration
     *
     *
     * @param lineIndent
     *
     * @return
     *
     * @see
     */
    public abstract String toJava(String lineIndent);
}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

