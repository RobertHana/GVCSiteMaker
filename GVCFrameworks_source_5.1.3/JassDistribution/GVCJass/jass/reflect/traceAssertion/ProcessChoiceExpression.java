/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

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
public class ProcessChoiceExpression extends ProcessExpression {

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
	checkConditionDeterminism();

	ProcessExpression[] expressionArray = 
	    new ProcessExpression[expressions.size()];

	expressions.toArray(expressionArray);

	ProcessFactory[] automatonArray = 
	    new ProcessFactory[expressionArray.length];

	for (int iExpression = 0; iExpression < expressionArray.length; 
		++iExpression) {
	    ProcessExpression expression = expressionArray[iExpression];

	    automaton = expression.constructAutomaton(automaton);
	    automatonArray[iExpression] = automaton;
	} 

	return ProcessFactory.newChoice(automatonArray);
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void checkConditionDeterminism() {
	ProcessExpression[] expressionArray = 
	    new ProcessExpression[expressions.size()];

	expressions.toArray(expressionArray);

	boolean      isDeterministic = true;
	SetOfMethods fullSet = expressionArray[0].getInitialCommunications();

	for (int iExpression = 0; iExpression < expressionArray.length; 
		++iExpression) {
	    for (int jExpression = 0; jExpression < expressionArray.length; 
		    ++jExpression) {
		if (iExpression != jExpression) {
		    ProcessExpression expression_i = 
			expressionArray[iExpression];
		    ProcessExpression expression_j = 
			expressionArray[jExpression];
		    SetOfMethods      set = new SetOfMethods();

		    set.add(expression_i.getInitialCommunications());
		    set.section(expression_j.getInitialCommunications());

		    if (!set.isEmpty()) {
			throw new SemanticError("Nondeterminism: Expressions " 
						+ new Integer(1 + iExpression) 
						+ " & " 
						+ new Integer(1 + jExpression) 
						+ " must not refer to equal communications." 
						+ " One is '" 
						+ set.anyElement() + "'", 
						this);
		    } 
		} 
	    } 
	} 
    } 

    /*
     * public String toString()
     * {
     * String string = "";
     * Iterator iterator  = expressions.iterator();
     * 
     * while(iterator.hasNext())
     * {
     * ProcessExpression expression = (ProcessExpression) iterator.next();
     * 
     * string += expression;
     * if(iterator.hasNext())
     * {
     * string += " <|> ";
     * }
     * }
     * return string;
     * }
     */
}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

