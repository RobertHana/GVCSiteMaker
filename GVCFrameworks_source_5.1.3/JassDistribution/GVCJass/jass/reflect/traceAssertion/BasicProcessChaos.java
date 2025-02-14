/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import jass.parser.JassParserConstants;
import jass.reflect.Class;    // to avoid conflicts with java.lang.class
import jass.reflect.*;
import jass.runtime.traceAssertion.*;
import jass.runtime.util.ToString;

/**
 * Basic process 'all communications of trace alphabet are allowed'.
 */
public class BasicProcessChaos extends ProcessExpression 
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
	jass.runtime.traceAssertion.SetOfCommunications runtimeCommunications = 
	    new jass.runtime.traceAssertion.SetOfCommunications();

	// convert array of 'jass.reflect.Communication' to array of 'jass.runtime.Communication'
	// see also reflect() method
	MethodReference[]				methodReferences = 
	    getCommunications().asMethodReferences();

	for (int iReference = 0; iReference < methodReferences.length; 
		++iReference) {
	    MethodReference methodReference = methodReferences[iReference];

	    runtimeCommunications.add(methodReference.asCommunication());
	} 

	ProcessFactory automaton = new PrimitiveChaos(runtimeCommunications);

	return automaton;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {

	// the following instructions model a wildcard expression
	// for 'all communications'. The aim is to model a 'ConditionalCommunication'
	String				allCommunications = 
	    ToString.token(JassParserConstants.STAR);

	// model wildcard expression '*'
	CommunicationWildcardExpression wildcardExpression = 
	    new CommunicationWildcardExpression();

	wildcardExpression.setFileBounds(getContainer(), getLine());
	wildcardExpression.setStringRepresentation(allCommunications);
	wildcardExpression.setToArbitraryClass();
	wildcardExpression.setToArbitraryMethod();
	wildcardExpression.setUnreflectedName(allCommunications);

	// model a set of communications which contains just the wildcard expression
	CommunicationExpressions communications = 
	    new CommunicationExpressions();

	communications.setFileBounds(getContainer(), getLine());
	communications.add(wildcardExpression);

	// finally model a ConditionalCommunication...
	ConditionalCommunication condCommunication = 
	    new ConditionalCommunication(communications);

	condCommunication.setFileBounds(getContainer(), getLine());
	condCommunication.setParent(parent);
	condCommunication.setStringRepresentation(allCommunications);

	// ... and add this expression to the current process expression to reflect it
	add(condCommunication);
	getFirstExpression().reflect();
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

