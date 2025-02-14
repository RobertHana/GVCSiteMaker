/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class IdNumber {
    public static final int START_VALUE = 0;
    int			    number;

    /**
     * Constructor declaration
     *
     *
     * @param START_VALUE
     *
     * @see
     */
    public IdNumber(final int START_VALUE) {
	number = START_VALUE;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public int create() {
	return number++;
    } 

    ;
}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

