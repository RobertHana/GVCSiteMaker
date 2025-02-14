/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class LookaheadTransition extends Transition {
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.LookaheadTransition";

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    /**
     * Constructor declaration
     *
     *
     * @param communications
     * @param condition
     * @param action
     *
     * @see
     */
    public LookaheadTransition(SetOfCommunications communications, 
			       Condition condition, Action action) {
	super(communications.prepareForLookahead(), condition, action);
    }

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */

    /**
     * ********************************************************************
     * Cloning Information (interface java.lang.Cloneable)                *
     * - the following inner objects are not cloned, just refered:        *
     * condition, action.                                               *
     * - all links to states are lost                                     *
     * *******************************************************************
     */
    public Object clone() {
	LookaheadTransition cloned = new LookaheadTransition(communications, 
		condition, action);

	return cloned;
    } 

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
    public String toJava(String lineIndent) {
	return toJava(lineIndent, CLASSNAME);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String toString() {
	return super.toString() + "*";
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

