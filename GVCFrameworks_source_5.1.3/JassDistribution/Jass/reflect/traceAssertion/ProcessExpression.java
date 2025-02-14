/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import jass.parser.JassParserConstants;
import jass.reflect.Class;    // to avoid conflicts with java.lang.Class
import jass.reflect.*;
import jass.runtime.traceAssertion.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public abstract class ProcessExpression implements FileBounded {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */

    // fields with valid values before reflection
    Class	      container;
    Vector	      expressions;
    int		      lineNr;
    ProcessExpression parentExpression;
    String	      stringRepresentation;

    // fields with valid values after reflection
    Process	      parent;

    // Vector javaBlocks;

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
    public ProcessExpression() {
	expressions = new Vector();

	// javaBlocks = new Vector();
    }

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */

    /**
     * Method declaration
     *
     *
     * @param expression
     *
     * @see
     */
    public void add(ProcessExpression expression) {
	expression.setParentExpression(this);
	expressions.add(expression);
    } 

    /*
     * public ProcessJavaBlock[] getJavaBlocks()
     * {
     * ProcessJavaBlock[] blockArray = new ProcessJavaBlock[javaBlocks.size()];
     * javaBlocks.toArray(blockArray);
     * return blockArray;
     * }
     */

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
    public abstract ProcessFactory constructAutomaton(ProcessFactory automaton);

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Class getContainer() {
	return container;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public ProcessExpression getFirstExpression() {
	ProcessExpression expression = null;

	try {
	    expression = (ProcessExpression) expressions.firstElement();
	} catch (NoSuchElementException exception) {
	    System.err.println(exception);
	    System.err.println("Internal error, Jass aborted");
	} 

	return expression;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public SetOfCommunications getCommunications() {
	SetOfCommunications communications = new SetOfCommunications();
	Enumeration	    expressionEnumeration = expressions.elements();

	while (expressionEnumeration.hasMoreElements()) {
	    ProcessExpression expression = 
		(ProcessExpression) expressionEnumeration.nextElement();

	    communications.add(expression.getCommunications());
	} 

	return communications;
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
	SetOfMethods initialCommunications = new SetOfMethods();
	Enumeration  expressionEnumeration = expressions.elements();

	while (expressionEnumeration.hasMoreElements()) {
	    ProcessExpression expression = 
		(ProcessExpression) expressionEnumeration.nextElement();

	    initialCommunications.add(expression.getInitialCommunications());
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
    public ProcessExpression getLastExpression() {
	ProcessExpression expression = null;

	try {
	    expression = (ProcessExpression) expressions.lastElement();
	} catch (NoSuchElementException exception) {
	    System.err.println(exception);
	    System.err.println("Internal error, Jass aborted");
	} 

	return expression;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public int getLine() {
	return lineNr;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Process getParent() {
	return parent;
    } 

    /**
     * Method declaration
     *
     *
     * @param child
     *
     * @return
     *
     * @see
     */
    public ProcessExpression getPrefix(ProcessExpression child) {
	int     childNr = expressions.indexOf(child);
	boolean weAreRoot = (parentExpression == null);

	if (weAreRoot) {
	    return null;
	} 

	boolean isOurChild = (childNr >= 0);

	if (!isOurChild) {
	    return null;
	} 

	return parentExpression.getPrefix(this);
    } 

    /*
     * public SetOfCommunications getRuntimeCommunications()
     * {
     * SetOfCommunications runtimeCommunications = new SetOfCommunications();
     * runtimeCommunications.add(getRuntimeCommunicationsAtBeginOfMethod());
     * runtimeCommunications.add(getRuntimeCommunicationsAtEndOfMethod());
     * return runtimeCommunications;
     * }
     * 
     * public SetOfCommunications getRuntimeCommunicationsAtBeginOfMethod()
     * {
     * SetOfCommunications runtimeCommunications = new SetOfCommunications();
     * Enumeration expressionEnumeration = expressions.elements();
     * while(expressionEnumeration.hasMoreElements())
     * {
     * ProcessExpression expression
     * = (ProcessExpression) expressionEnumeration.nextElement();
     * runtimeCommunications.add(expression.getRuntimeCommunicationsAtBeginOfMethod());
     * }
     * return runtimeCommunications;
     * }
     * public SetOfCommunications getRuntimeCommunicationsAtEndOfMethod()
     * {
     * SetOfCommunications runtimeCommunications = new SetOfCommunications();
     * Enumeration expressionEnumeration = expressions.elements();
     * while(expressionEnumeration.hasMoreElements())
     * {
     * ProcessExpression expression
     * = (ProcessExpression) expressionEnumeration.nextElement();
     * runtimeCommunications.add(expression.getRuntimeCommunicationsAtEndOfMethod());
     * }
     * return runtimeCommunications;
     * }
     */

    /*
     * public boolean isCommunicationGuarded(ProcessExpression caller)
     * {
     * return !getInitialCommunications().isEmpty();
     * }
     */

    /*
     * public boolean isCommunicationGuarded(ProcessExpression caller)
     * {
     * boolean isGuarded = true;
     * 
     * if(expressions.isEmpty())
     * {
     * isGuarded = !getInitialCommunications().isEmpty();
     * }
     * else
     * {
     * Enumeration expressionEnumeration = expressions.elements();
     * while(expressionEnumeration.hasMoreElements())
     * {
     * ProcessExpression expression
     * = (ProcessExpression) expressionEnumeration.nextElement();
     * isGuarded = expression.isCommunicationGuarded(caller);
     * }
     * }
     * return isGuarded;
     * }
     */

    /*
     * public boolean isCommunicationGuarded(ProcessExpression notUsed)
     * {
     * boolean guarded;
     * if(parentExpression == null)
     * {
     * guarded = false;
     * }
     * else
     * {
     * guarded = parentExpression.isCommunicationGuarded(this);
     * }
     * return guarded;
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
    public boolean isCorrectProcessClosure() {
	if (expressions.isEmpty()) {
	    return false;
	} 

	boolean     correctClosure = true;
	Enumeration expressionEnumeration = expressions.elements();

	while (correctClosure && expressionEnumeration.hasMoreElements()) {
	    ProcessExpression expression = 
		(ProcessExpression) expressionEnumeration.nextElement();

	    correctClosure = expression.isCorrectProcessClosure();
	} 

	return correctClosure;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	int numberOfExpressions = expressions.size();

	// no expressions as children, no further reflection
	if (numberOfExpressions == 0) {
	    return;
	} 

	// At least one expression as child!
	ProcessExpression[] expressionArray = 
	    new ProcessExpression[numberOfExpressions];

	expressions.toArray(expressionArray);

	for (int iExpression = 0; iExpression < expressionArray.length; 
		++iExpression) {
	    ProcessExpression expression = expressionArray[iExpression];

	    expression.setParent(this.parent);
	    expression.reflect();

	    if (expression instanceof ProcessJavaBlock) {
		parent.addCodeProducer((ProcessJavaBlock) expression);
	    } 

	    // javaBlocks.addAll(Arrays.asList(expression.getJavaBlocks()));
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param container
     * @param line
     *
     * @see
     */
    public void setFileBounds(Class container, int line) {
	this.container = container;
	this.lineNr = line;
    } 

    /**
     * Method declaration
     *
     *
     * @param parent
     *
     * @see
     */
    public void setParent(Process parent) {
	this.parent = parent;
    } 

    /**
     * Method declaration
     *
     *
     * @param parent
     *
     * @see
     */
    public void setParentExpression(ProcessExpression parent) {
	this.parentExpression = parent;
    } 

    /**
     * Method declaration
     *
     *
     * @param string
     *
     * @see
     */
    public void setStringRepresentation(String string) {
	this.stringRepresentation = string;
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
	return stringRepresentation;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

