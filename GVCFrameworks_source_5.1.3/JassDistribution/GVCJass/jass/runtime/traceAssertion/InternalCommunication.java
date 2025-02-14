/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class InternalCommunication extends Communication {
    Transition transition;

    /**
     * Constructor declaration
     *
     *
     * @param transition
     *
     * @see
     */
    public InternalCommunication(Transition transition) {
	super("internal", "internal", "internal", true, null, null);

	this.transition = transition;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Transition getTransition() {
	return transition;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

