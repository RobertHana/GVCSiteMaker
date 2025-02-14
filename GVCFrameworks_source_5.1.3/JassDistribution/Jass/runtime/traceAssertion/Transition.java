/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import jass.runtime.util.ToJava;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class Transition implements Cloneable {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.Transition";
    State		       prevState;
    State		       succState;
    SetOfCommunications	       communications;
    Condition		       condition;
    Action		       action;

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
     *
     * @see
     */
    public Transition(SetOfCommunications communications) {
	init(communications, null, null);
    }

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
    public Transition(SetOfCommunications communications, 
		      Condition condition, Action action) {
	init(communications, condition, action);
    }

    /*
     * ....................................................................
     * . Private Methods                                                  .
     * ....................................................................
     */

    /**
     * Method declaration
     *
     *
     * @param communications
     * @param condition
     * @param action
     *
     * @see
     */
    private void init(SetOfCommunications communications, 
		      Condition condition, Action action) {
	this.communications = communications;
	this.condition = condition;
	this.action = action;
    } 

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */

    /*
     * ....................................................................
     * . Cloning Information (interface java.lang.Cloneable)              .
     * . - the following inner objects are not cloned, just refered:      .
     * .   communications, condition, action.                             .
     * . - all links to states are lost                                   .
     * ....................................................................
     */

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Object clone() {
	Transition cloned = new Transition(communications, condition, action);

	return cloned;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Action getAction() {
	return action;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Condition getCondition() {
	return condition;
    } 

    /**
     * Method declaration
     *
     *
     * @param methodReference
     *
     * @return
     *
     * @see
     */
    public Communication getCommunication(MethodReference methodReference) {
	return getCommunications().getCommunication(methodReference);
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

    /*
     * public SetOfCommunications getCommunications()
     * {
     * if(action instanceof ProcessReference)
     * {
     * ProcessReference processReference = (ProcessReference) action;
     * Automaton referedAutomaton = automata.getAutomaton(processReference.getReference());
     * return referedAutomaton.getInitialCommunications(automata);
     * }
     * else
     * {
     * return getCommunications();
     * }
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
    public State getPrevState() {
	return prevState;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public State getSuccState() {
	return succState;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isLoop() {
	return prevState == succState;
    } 

    /**
     * Method declaration
     *
     *
     * @param communications
     *
     * @see
     */
    public void setCommunications(SetOfCommunications communications) {
	this.communications = communications;
    } 

    /**
     * Method declaration
     *
     *
     * @param s
     *
     * @see
     */
    public void setPrevState(State s) {
	prevState = s;
    } 

    /**
     * Method declaration
     *
     *
     * @param s
     *
     * @see
     */
    public void setSuccState(State s) {
	succState = s;
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
     * @param lineIndent
     * @param className
     *
     * @return
     *
     * @see
     */
    public String toJava(String lineIndent, String className) {
	String code = "";
	String succLineIndent = lineIndent + "  ";

	code += 
	    lineIndent + "new " + className + ToJava.NEWLINE + lineIndent 
	    + "(" + ToJava.NEWLINE 
	    + (communications == null ? succLineIndent + "null" : communications.toJava(succLineIndent)) 
	    + "," + ToJava.NEWLINE 
	    + (condition == null ? succLineIndent + "null" : condition.toJava(succLineIndent)) 
	    + "," + ToJava.NEWLINE 
	    + (action == null ? succLineIndent + "null" : action.toJava(succLineIndent)) 
	    + ToJava.NEWLINE + lineIndent + ")";

	return code;
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
	String s = "";

	if (isLoop()) {
	    s += "<";
	} else {
	    s += "-";
	} 

	if (action != null) {
	    s += "[" + action.toString() + "]";
	} 

	s += communications != null ? communications.toString() 
	     : "<unresolved>";
	s += ">";

	return s;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

