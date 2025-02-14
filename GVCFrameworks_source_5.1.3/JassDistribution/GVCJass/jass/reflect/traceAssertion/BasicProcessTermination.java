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
public class BasicProcessTermination extends ProcessExpression 
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

	// add this.finalize() as a prefix to the automaton
	// set reflect() method
	automaton = getFirstExpression().constructAutomaton(automaton);

	return automaton;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {

	// the following instructions model a ConditionalExpression 'this.finalize()'
	// model expression for 'this.finalize()'
	CommunicationExpression communication = new CommunicationExpression();

	communication.setCommunicationType(Communication.TYPE_BOTH);
	communication.setUnreflectedName("this.finalize");
	communication.setParameters(new CommunicationParameter[0]);
	communication.setContainer(getContainer());
	communication.setLine(getLine());
	communication.setStringRepresentation(toString());

	// model a set of communications which contains just the single expression
	CommunicationExpressions communications = 
	    new CommunicationExpressions();

	communications.setFileBounds(getContainer(), getLine());
	communications.add(communication);

	// finally model a ConditionalCommunication...
	ConditionalCommunication condCommunication = 
	    new ConditionalCommunication(communications);

	condCommunication.setFileBounds(getContainer(), getLine());
	condCommunication.setParent(parent);
	condCommunication.setStringRepresentation("");

	// ... and add this expression to the current process expression to reflect it
	add(condCommunication);
	getFirstExpression().reflect();
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

