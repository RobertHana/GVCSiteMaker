/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class CheckException extends AssertionException {

    /**
     * Constructor declaration
     *
     *
     * @param c
     * @param m
     * @param line
     * @param label
     *
     * @see
     */
    public CheckException(String c, String m, int line, String label) {
	super(c, m, line, label);
    }

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

