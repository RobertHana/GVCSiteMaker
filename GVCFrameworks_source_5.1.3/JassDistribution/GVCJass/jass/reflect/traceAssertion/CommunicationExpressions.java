/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import java.util.*;
import jass.parser.JassParserConstants;
import jass.reflect.*;
import jass.reflect.Class;
import jass.runtime.traceAssertion.SetOfMethods;
import jass.runtime.util.ToString;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class CommunicationExpressions implements FileBounded, 
	AlphabetListener {
    TraceAlphabet	     alphabet;
    Class		     container;
    int			     lineNr;
    Vector		     expressions;
    CommunicationExpressions exceptions;
    boolean		     hasArbitraryClass = false;
    SetOfCommunications      communications;

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public CommunicationExpressions() {
	expressions = new Vector();
	communications = new SetOfCommunications();
    }

    // interface AlphabetListener

    /**
     * Method declaration
     *
     *
     * @param alphabet
     *
     * @see
     */
    public void announceAlphabet(TraceAlphabet alphabet) {

	// System.out.println("Alphabet announced: " + StringTools.toString(alphabet.getCommunications()));
	this.alphabet = alphabet;

	reflect();
    } 

    /**
     * Method declaration
     *
     *
     * @param expression
     *
     * @see
     */
    public void add(CommunicationExpression expression) {
	expressions.add(expression);

	hasArbitraryClass = hasArbitraryClass 
			    || expression.isArbitraryClass();
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
    public boolean hasArbitraryClass() {
	return hasArbitraryClass;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	Enumeration expressionEnumeration = expressions.elements();

	while (expressionEnumeration.hasMoreElements()) {
	    CommunicationExpression communicationExpression = 
		(CommunicationExpression) expressionEnumeration.nextElement();

	    communicationExpression.setAlphabet(alphabet);
	    communicationExpression.reflect();
	    communications.add(communicationExpression.getCommunications());
	} 

	if (exceptions != null) {
	    exceptions.setAlphabet(alphabet);
	    exceptions.reflect();
	    communications.without(exceptions.getCommunications());
	} 

	boolean atLeastOneCommunication = !communications.isEmpty();

	if (!atLeastOneCommunication) {
	    if (alphabet != null) {
		System.out.println(new ReflectExpressionWarning("Expression can not be reflected to any communications in alphabet", 
			this));
	    } else {
		System.out.println(new ReflectExpressionWarning("Expression can not be reflected to any communications", 
			this));
	    } 
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param alphabet
     *
     * @see
     */
    public void setAlphabet(TraceAlphabet alphabet) {
	this.alphabet = alphabet;
    } 

    /**
     * Method declaration
     *
     *
     * @param exceptions
     *
     * @see
     */
    public void setExceptions(CommunicationExpressions exceptions) {
	this.exceptions = exceptions;
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
     * @return
     *
     * @see
     */
    public String toString() {
	String			  string = "";
	CommunicationExpression[] expressionArray = 
	    new CommunicationExpression[expressions.size()];

	expressions.toArray(expressionArray);

	for (int iExpression = 0; iExpression < expressionArray.length - 1; 
		++iExpression) {
	    CommunicationExpression expression = expressionArray[iExpression];

	    string += expression.toString() + ", ";
	} 

	string += expressionArray[expressionArray.length - 1].toString();

	if (expressionArray.length > 1) {
	    string = "{" + string + "}";
	} 

	if (exceptions != null) {
	    String tokenImage = 
		ToString.token(JassParserConstants.TRACE_COMPLEMENT_OPERATOR);

	    string += " " + tokenImage + " ";
	    string += exceptions.toString();
	} 

	return string;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

