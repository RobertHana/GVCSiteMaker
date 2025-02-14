/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import jass.parser.JassParserConstants;
import jass.reflect.Class;    // to avoid conflicts with java.lang.class
import jass.reflect.*;
import jass.runtime.traceAssertion.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class BasicProcessDeadlock extends ProcessExpression 
    implements JassParserConstants {

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */

    // A process declaration may end with a basic process

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isCorrectProcessClosure() {
	return true;
    } 

    /**
     * Method declaration
     *
     *
     * @param notUsed
     *
     * @return
     *
     * @see
     */
    public ProcessFactory constructAutomaton(ProcessFactory notUsed) {
	ProcessFactory automaton = null;

	automaton = new PrimitiveDeadlock();

	return automaton;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

