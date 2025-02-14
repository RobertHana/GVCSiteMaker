/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class PrimitiveChaos extends ProcessFactory {

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
     * @param alphabet
     *
     * @see
     */
    public PrimitiveChaos(SetOfCommunications alphabet) {
	StateChaos	    state = new StateChaos();
	LookaheadTransition lookaheadTransition = 
	    new LookaheadTransition(alphabet, null, null);

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

