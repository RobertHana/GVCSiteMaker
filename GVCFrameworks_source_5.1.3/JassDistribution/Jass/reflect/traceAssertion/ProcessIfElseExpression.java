/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import jass.reflect.Class;    // to avoid conflicts with java.lang.class
import jass.reflect.*;
import jass.runtime.traceAssertion.*;
import jass.runtime.util.ToJava;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ProcessIfElseExpression extends ProcessExpression 
    implements CodeProducer {
    static int	      nextIdNumberCondition = 0;
    Expression	      conditionExpression;
    int		      idNumberCondition;
    ProcessExpression ifExpression;
    ProcessExpression elseExpression;

    /**
     * Constructor declaration
     *
     *
     * @param ifExpression
     * @param elseExpression
     *
     * @see
     */
    public ProcessIfElseExpression(ProcessExpression ifExpression, 
				   ProcessExpression elseExpression) {
	this.ifExpression = ifExpression;
	this.elseExpression = elseExpression;

	expressions.add(ifExpression);
	expressions.add(elseExpression);

	idNumberCondition = nextIdNumberCondition++;
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
	MappedMethod       method = 
	    new MappedMethod(new Source(getContainer().getName(), getLine()), 
			     getMappedMethodnameCondition(), new String[]{}, 
			     conditionExpression.toString());
	ConditionReference condition = new ConditionReference(method);
	ProcessFactory     ifBranch = ifExpression.constructAutomaton(null);
	ProcessFactory     elseBranch = 
	    elseExpression.constructAutomaton(null);
	ProcessFactory     automaton = ProcessFactory.newIfElse(condition, 
		ifBranch, elseBranch);

	return automaton;
    } 

    /*
     * public ProcessExpression getIfExpression()
     * {
     * ProcessExpression expression = (ProcessExpression) expressions.elementAt(0);
     * return expression;
     * }
     * public ProcessExpression getElseExpression()
     * {
     * ProcessExpression expression = (ProcessExpression) expressions.elementAt(1);
     * return expression;
     * }
     */

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getMappedMethodnameCondition() {
	return "ifElseCondition_" + idNumberCondition;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	super.reflect();
	conditionExpression.reflectExpression(parent.getMappedClass(), 
					      new DependencyCollector());
	parent.addCodeProducer(this);
    } 

    /**
     * Method declaration
     *
     *
     * @param expression
     *
     * @see
     */
    public void setCondition(Expression expression) {
	this.conditionExpression = expression;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String toJava() {
	String javaCode = "";
	String codeCondition = conditionExpression.toString();

	javaCode += ToJava.INDENT1 + "public boolean";
	javaCode += " " + getMappedMethodnameCondition() + "()" 
		    + ToJava.NEWLINE;
	javaCode += ToJava.INDENT1 + "{return " + codeCondition + ";}" 
		    + ToJava.NEWLINE;

	return javaCode;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

