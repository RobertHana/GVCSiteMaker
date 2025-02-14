/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Interface declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public interface Communicator {

    /**
     * Method declaration
     *
     *
     * @param mr
     *
     * @return
     *
     * @see
     */
    public boolean canCommunicate(MethodReference mr);

    /**
     * Method declaration
     *
     *
     * @param stack
     * @param mr
     * @param params
     *
     * @see
     */
    public void communicate(TraceStack stack, MethodReference mr, 
			    Parameter[] params);
}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

