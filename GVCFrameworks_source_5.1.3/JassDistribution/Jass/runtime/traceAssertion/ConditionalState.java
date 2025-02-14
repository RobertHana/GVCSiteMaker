/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ConditionalState extends State {
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.ConditionalState";
    ConditionReference	       condition;
    boolean		       evaluation;

    /**
     * Constructor declaration
     *
     *
     * @param condition
     *
     * @see
     */
    public ConditionalState(ConditionReference condition) {
	this.condition = condition;
    }

    /**
     * Constructor declaration
     *
     *
     * @param isFinal
     * @param condition
     *
     * @see
     */
    public ConditionalState(boolean isFinal, ConditionReference condition) {
	super(isFinal);

	this.condition = condition;
    }

    /**
     * Method declaration
     *
     *
     * @param finalState
     * @param condition
     *
     * @return
     *
     * @see
     */
    public static String constructorString(boolean finalState, 
					   ConditionReference condition) {
	String javaCode = "";

	javaCode += "new " + CLASSNAME;
	javaCode += "(";
	javaCode += finalState;
	javaCode += ", " + condition.toJava("");
	javaCode += ")";

	return javaCode;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Transition elseBranch() {
	return getOutgoing()[1];
    } 

    /**
     * Method declaration
     *
     *
     * @param process
     *
     * @see
     */
    public void evaluate(Process process) {
	evaluation = condition.evaluate(process);
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

	communications.add(ifBranch().getCommunications());
	communications.add(elseBranch().getCommunications());

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
	Transition	    evaluatedTransition;
	SetOfCommunications communicationsIf = ifBranch().getCommunications();
	SetOfCommunications communicationsElse = 
	    elseBranch().getCommunications();
	boolean		    possibleIf = 
	    communicationsIf.isEmpty() 
	    || communicationsIf.contains(methodReference);
	boolean		    possibleElse = 
	    communicationsElse.isEmpty() 
	    || communicationsElse.contains(methodReference);

	evaluatedTransition = evaluation ? ifBranch() : elseBranch();

	if (!(possibleIf && possibleElse)) {
	    if ((possibleIf && evaluation == false) 
		    || (possibleElse && evaluation == true)) {
		evaluatedTransition = null;
	    } 
	} 

	return evaluatedTransition;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Transition ifBranch() {
	return getOutgoing()[0];
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
	return constructorString(isFinal(), condition);
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

