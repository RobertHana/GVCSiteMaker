/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Iterator;
import jass.reflect.Class;    // to avoid conflicts with java.lang.class
import jass.reflect.*;
import jass.runtime.traceAssertion.ProcessFactory;
import jass.runtime.traceAssertion.SetOfMethods;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ProcessPrefixExpression extends ProcessExpression {
    final int NO_GUARD = -1;
    int       communicationGuard = NO_GUARD;

    /**
     * Method declaration
     *
     *
     * @param expression
     *
     * @see
     */
    public void add(ProcessExpression expression) {
	super.add(expression);

	if (communicationGuard == NO_GUARD) {
	    if (expression instanceof ConditionalCommunication 
		    || (expression instanceof ProcessPrefixExpression 
			&& ((ProcessPrefixExpression) expression).hasCommunicationGuard())) {
		communicationGuard = expressions.size() - 1;
	    } 
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param automaton
     *
     * @return
     *
     * @see
     */
    public ProcessFactory constructAutomaton(ProcessFactory automaton) {
	int		    numberOfExpressions = expressions.size();
	ProcessExpression[] expressionArray = 
	    new ProcessExpression[numberOfExpressions];

	expressions.toArray(expressionArray);

	int		  lastIndex = expressionArray.length - 1;
	ProcessExpression lastExpression = expressionArray[lastIndex];

	automaton = lastExpression.constructAutomaton(automaton);

	for (int iExpression = lastIndex - 1; iExpression >= 0; 
		--iExpression) {
	    ProcessExpression expression = expressionArray[iExpression];

	    automaton = expression.constructAutomaton(automaton);
	} 

	return automaton;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public SetOfMethods getInitialCommunications() {
	SetOfMethods initialCommunications;
	Enumeration  expressionEnumeration = expressions.elements();
	Object       expression = null;

	while (expressionEnumeration.hasMoreElements() 
	       && ((expression = expressionEnumeration.nextElement()) 
		   instanceof ProcessJavaBlock)) {
	    ;    // next element is read by boolean expression of while loop
	} 

	boolean notAllExpressionsAreJavaBlocks = 
	    (expressionEnumeration.hasMoreElements());

	if (notAllExpressionsAreJavaBlocks) {
	    initialCommunications = 
		((ProcessExpression) expression).getInitialCommunications();
	} else {
	    initialCommunications = new SetOfMethods();
	} 

	return initialCommunications;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean hasCommunicationGuard() {
	return communicationGuard != NO_GUARD;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isCorrectProcessClosure() {
	boolean		  correctClosure;
	ProcessExpression lastExpression = getLastExpression();

	correctClosure = lastExpression.isCorrectProcessClosure();

	return correctClosure;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

