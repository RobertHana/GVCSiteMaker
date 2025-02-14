/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import java.util.HashSet;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class SetOfTraceAssertions {
    HashSet traceAssertions;

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public SetOfTraceAssertions() {
	traceAssertions = new HashSet();
    }

    /**
     * Method declaration
     *
     *
     * @param traceAssertion
     *
     * @see
     */
    public void add(TraceAssertionManager traceAssertion) {
	traceAssertions.add(traceAssertion);
    } 

    /**
     * Method declaration
     *
     *
     * @param traceAssertion
     *
     * @see
     */
    public void remove(TraceAssertionManager traceAssertion) {
	traceAssertions.remove(traceAssertion);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public TraceAssertionManager[] elements() {
	TraceAssertionManager[] assertionArray = 
	    new TraceAssertionManager[traceAssertions.size()];

	traceAssertions.toArray(assertionArray);

	return assertionArray;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isEmpty() {
	return traceAssertions.isEmpty();
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

