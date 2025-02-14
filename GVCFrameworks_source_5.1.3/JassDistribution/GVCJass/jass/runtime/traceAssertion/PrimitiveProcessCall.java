/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class PrimitiveProcessCall extends ProcessFactory {

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    /**
     * Constructor declaration
     *
     *
     * @param methodSet
     * @param mappedMethodname
     *
     * @see
     */
    public PrimitiveProcessCall(SetOfMethods methodSet, 
				String mappedMethodname) {
	State		    state = new State(State.IS_FINAL);
	LookaheadTransition lookaheadTransition = 
	    new LookaheadTransition(methodSet.asSetOfCommunications(), null, 
				    new ProcessCall(mappedMethodname));

	state.addIncoming(lookaheadTransition);

	initialState = state;
    }

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */
}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

