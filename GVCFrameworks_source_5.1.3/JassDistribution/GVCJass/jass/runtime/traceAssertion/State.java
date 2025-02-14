/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import java.util.Vector;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class State implements Cloneable {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.State";
    final static boolean       IS_FINAL = true;
    Vector		       incomingList;
    Vector		       outgoingList;
    boolean		       finalState;

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
    public State() {
	init();

	finalState = false;
    }

    /**
     * Constructor declaration
     *
     *
     * @param isFinal
     *
     * @see
     */
    public State(boolean isFinal) {
	init();

	this.finalState = isFinal;
    }

    /*
     * public String constructorString(boolean finalState)
     * {
     * return "new " + this.CLASSNAME + "(" + finalState + ")";
     * }
     */

    /**
     * ********************************************************************
     * Private Methods                                                    *
     * *******************************************************************
     */
    private void init() {
	incomingList = new Vector();
	outgoingList = new Vector();
    } 

    /**
     * Method declaration
     *
     *
     * @param v
     *
     * @return
     *
     * @see
     */
    private Transition[] transitionArray(Vector v) {
	Transition[] array = new Transition[v.size()];

	v.toArray(array);

	return array;
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
     * @param t
     *
     * @see
     */
    public void addIncoming(Transition t) {
	incomingList.addElement(t);
	t.setSuccState(this);
    } 

    /**
     * Method declaration
     *
     *
     * @param condition
     * @param action
     *
     * @see
     */
    public void addIncomingLookahead(Condition condition, Action action) {
	LookaheadTransition lookahead = 
	    new LookaheadTransition(getPossibleCommunications(), condition, 
				    action);

	addIncoming(lookahead);
    } 

    /**
     * Method declaration
     *
     *
     * @param t
     *
     * @see
     */
    public void addOutgoing(Transition t) {
	outgoingList.addElement(t);
	t.setPrevState(this);
    } 

    /*
     * ....................................................................
     * . Cloning Information (interface java.lang.Cloneable)              .
     * . - the clone() method just invokes the constructor State()                                                                 .
     * . - all links to transitions are lost                                                                 .
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
	State cloned = new State(finalState);

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
    public Transition[] getIncoming() {
	return transitionArray(incomingList);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Transition[] getOutgoing() {
	return transitionArray(outgoingList);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public SetOfCommunications getPossibleCommunications() {
	SetOfCommunications communications = new SetOfCommunications();
	Transition[]	    transition = getOutgoing();

	for (int i = 0; i < transition.length; ++i) {
	    SetOfCommunications actualCommunications = 
		transition[i].getCommunications();

	    communications.add(actualCommunications);
	} 

	return communications;
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
    public Transition getTransition(MethodReference methodReference) {
	Transition[] transitionList = getOutgoing();
	boolean      belongingTransitionFound = false;
	Transition   actualTransition = null;

	for (int iTransition = 0; 
		!belongingTransitionFound && iTransition < transitionList.length; 
		++iTransition) {
	    actualTransition = transitionList[iTransition];
	    belongingTransitionFound = 
		actualTransition.getCommunications().contains(methodReference);
	} 

	if (belongingTransitionFound) {
	    return actualTransition;
	} else {
	    return null;
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isFinal() {
	return finalState;
    } 

    /**
     * Method declaration
     *
     *
     * @param finalState
     *
     * @see
     */
    public void setFinal(boolean finalState) {
	this.finalState = finalState;
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
	return "new " + this.CLASSNAME + "(" + finalState + ")";
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

	s += "(";

	if (isFinal()) {
	    s += "*";
	} else {
	    s += getOutgoing().length;
	} 

	s += ")";

	return s;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

