/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public abstract class Condition {

    /**
     * Method declaration
     *
     *
     * @param parentProcess
     *
     * @return
     *
     * @see
     */
    public abstract boolean evaluate(Process parentProcess);

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public abstract MappedMethod getMappedMethod();

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

