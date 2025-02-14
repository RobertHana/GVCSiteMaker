/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class PrimitiveDeadlock extends ProcessFactory {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */

    // none

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
    public PrimitiveDeadlock() {
	StateDeadlock       state = new StateDeadlock();
	LookaheadTransition lookaheadTransition = 
	    new LookaheadTransition(new SetOfCommunications(), null, null);

	state.addIncoming(lookaheadTransition);

	initialState = state;
    }

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */

    // none
}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

