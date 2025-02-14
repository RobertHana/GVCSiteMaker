/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import jass.runtime.traceAssertion.Process;    // to avoid conflicts with java.lang.Process

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ProcessFactory extends Process {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */

    // none

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    // none

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */

    /**
     * Method declaration
     *
     *
     * @param prefix
     *
     * @see
     */
    public void addPrefix(State prefix) {
	if (initialState == null) {
	    prefix.setFinal(true);

	    initialState = prefix;

	    return;
	} 

	Transition[] postfixIncoming = initialState.getIncoming();

	for (int i = 0; i < postfixIncoming.length; ++i) {
	    Transition t = postfixIncoming[i];

	    // dont't link to loops
	    if (t.getPrevState() == null) {
		prefix.addOutgoing(t);
		initialState.addIncoming(t);
	    } 
	} 

	initialState = prefix;
    } 

    /**
     * Method declaration
     *
     *
     * @param action
     *
     * @see
     */
    public void addPrefixJava(CodeExecution action) {
	State prefix = new State();

	addPrefix(prefix);
	initialState.addIncomingLookahead(null, action);
    } 

    /**
     * Method declaration
     *
     *
     * @param communication
     * @param condition
     * @param action
     *
     * @see
     */
    public void addPrefixMethod(Communication communication, 
				Condition condition, Action action) {
	addPrefixMethod(new SetOfCommunications(communication), condition, 
			action);
    } 

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
    public void addPrefixMethod(SetOfCommunications communications, 
				Condition condition, Action action) {
	State prefix = new State();

	prefix.addIncoming(new Transition(communications, condition, action));
	addPrefix(prefix);
    } 

    /**
     * Method declaration
     *
     *
     * @param name
     * @param initialCommunications
     *
     * @see
     */
    public void finishProcessCreation(String name, 
				      SetOfMethods initialCommunications) {
	this.name = name;
	this.initialCommunications = initialCommunications;

	// addLookaheadPrefix(null, new ProcessCreation(name));
	State prefix = new State();

	addPrefix(prefix);
    } 

    /**
     * Method declaration
     *
     *
     * @param alternatives
     *
     * @return
     *
     * @see
     */
    public static ProcessFactory newChoice(ProcessFactory[] alternatives) {
	return newChoice(alternatives, new State());
    } 

    /**
     * Method declaration
     *
     *
     * @param alternatives
     * @param choice
     *
     * @return
     *
     * @see
     */
    public static ProcessFactory newChoice(ProcessFactory[] alternatives, 
					   State choice) {

	// () -> ()
	// -> ()
	ProcessFactory process = new ProcessFactory();

	for (int iAlternative = 0; iAlternative < alternatives.length; 
		++iAlternative) {
	    ProcessFactory alternative = alternatives[iAlternative];

	    alternative.addPrefix(choice);
	} 

	choice.addIncomingLookahead(null, null);
	process.setInitialState(choice);

	return process;
    } 

    /**
     * Method declaration
     *
     *
     * @param condition
     * @param ifBranch
     * @param elseBranch
     *
     * @return
     *
     * @see
     */
    public static ProcessFactory newIfElse(ConditionReference condition, 
					   ProcessFactory ifBranch, 
					   ProcessFactory elseBranch) {
	State choice = new ConditionalState(condition);

	return newChoice(new ProcessFactory[] {
	    ifBranch, elseBranch
	}, choice);
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

