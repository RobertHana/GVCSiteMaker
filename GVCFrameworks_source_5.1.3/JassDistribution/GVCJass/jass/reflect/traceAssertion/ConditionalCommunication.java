/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import java.io.*;
import jass.parser.Node;
import jass.reflect.Class;    // to avoid conflicts with 'java.lang.class'
import jass.reflect.*;
import jass.runtime.traceAssertion.*;
import jass.runtime.util.ToJava;
import jass.visitor.OutputVisitor;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ConditionalCommunication extends ProcessExpression 
    implements AlphabetListener, CodeProducer {
    public static final String METHOD_IDENTIFIER_AFTER = "after_";
    static int		       nextIdNumberCondition = 0;
    static int		       nextIdMethodnameAfter = 0;
    String		       javaCodeAfter = "";
    ExchangeParameter	       returnType;
    CommunicationExpressions   communicationExpressions;
    Expression                 condition = null;
    String		       codeCondition = "";
    String		       codeBefore = "";
    String		       codeAfter = "";
    int			       idNumberCondition;

    /**
     * Constructor declaration
     *
     *
     * @param expressions
     *
     * @see
     */
    public ConditionalCommunication(CommunicationExpressions expressions) {
	this.communicationExpressions = expressions;
    }

    /**
     * Method declaration
     *
     *
     * @param alphabet
     *
     * @see
     */
    public void announceAlphabet(TraceAlphabet alphabet) {
	reflect();
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
	Communication[]					communicationsAtBegin = 
	    getCommunications().getCommunicationsAtBeginOfMethod();
	jass.runtime.traceAssertion.SetOfCommunications runtimeCommunicationsAtBeginOfMethod = 
	    new jass.runtime.traceAssertion.SetOfCommunications();

	for (int iCommunication = 0; 
		iCommunication < communicationsAtBegin.length; 
		++iCommunication) {
	    Communication communication = 
		communicationsAtBegin[iCommunication];

	    runtimeCommunicationsAtBeginOfMethod.add(communication.asRuntimeCommunication(true));
	} 

	Communication[]					communicationsAtEnd = 
	    getCommunications().getCommunicationsAtEndOfMethod();
	jass.runtime.traceAssertion.SetOfCommunications runtimeCommunicationsAtEndOfMethod = 
	    new jass.runtime.traceAssertion.SetOfCommunications();

	for (int iCommunication = 0; 
		iCommunication < communicationsAtEnd.length; 
		++iCommunication) {
	    Communication communication = communicationsAtEnd[iCommunication];

	    runtimeCommunicationsAtEndOfMethod.add(communication.asRuntimeCommunication(false));
	} 

	ConditionReference conditionReference = null;

	if (condition != null) {
	    conditionReference = 
		new ConditionReference(new MappedMethod(new Source(getContainer().getName(), getLine()), 
							getMappedMethodnameCondition(), 
							new String[]{}, 
							toString()));
	} 

	if (!runtimeCommunicationsAtEndOfMethod.isEmpty()) {
	    automaton.addPrefixMethod(runtimeCommunicationsAtEndOfMethod, 
				      conditionReference, null);
	} 

	if (!runtimeCommunicationsAtBeginOfMethod.isEmpty()) {
	    automaton.addPrefixMethod(runtimeCommunicationsAtBeginOfMethod, 
				      conditionReference, null);
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
    public SetOfCommunications getCommunications() {
	return communicationExpressions.getCommunications();
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
	SetOfMethods    initialCommunications = new SetOfMethods();
	Communication[] communicationsAtBegin = 
	    getCommunications().getCommunicationsAtBeginOfMethod();

	if (communicationsAtBegin != null) {
	    for (int iCommunication = 0; 
		    iCommunication < communicationsAtBegin.length; 
		    ++iCommunication) {
		Communication communication = 
		    communicationsAtBegin[iCommunication];

		initialCommunications.add(communication.asMethodReference(true));
	    } 
	} else {
	    Communication[] communicationsAtEnd = 
		getCommunications().getCommunicationsAtEndOfMethod();

	    for (int iCommunication = 0; 
		    iCommunication < communicationsAtEnd.length; 
		    ++iCommunication) {
		Communication communication = 
		    communicationsAtEnd[iCommunication];

		initialCommunications.add(communication.asMethodReference(false));
	    } 
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
    public String getMappedMethodnameCondition() {
	return "condition_" + idNumberCondition;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	TraceAssertionExpression traceAssertion = parent.getParent();
	TraceAlphabet		 alphabet = traceAssertion.getAlphabet();
	SetOfCommunications      reflectedCommunications;

	if (!traceAssertion.hasExplicitAlphabet() 
		&& communicationExpressions.hasArbitraryClass() 
		&& alphabet == null) {
	    traceAssertion.addAlphabetListener(this);

	    return;
	} 

	communicationExpressions.setAlphabet(alphabet);
	communicationExpressions.reflect();

	reflectedCommunications = 
	    communicationExpressions.getCommunications();

	parent.addToAlphabet(reflectedCommunications);

	if (condition != null) {
	    Class parentClass = parent.getMappedClass();

	    condition.reflectExpression(parentClass, 
					new DependencyCollector());

	    if (condition instanceof ForAllExpression) {
		codeCondition = condition.toString();
	    } 
	} 

	MappedMethod mappedMethodAfter = null;

	if (returnType != null) {
	    returnType.reflect();

	    String   mappedMethodnameAfter = METHOD_IDENTIFIER_AFTER 
					     + nextIdMethodnameAfter++;
	    JavaCode codeBlock = new JavaCode();
	    String   returnVariable = "returnValue";

	    Communication.toJava(codeBlock, returnType, returnVariable);

	    codeAfter += 
		Communication.toJava(codeBlock, returnType.getTypename() + " " + returnVariable, mappedMethodnameAfter) 
		+ ToJava.NEWLINE;
	    mappedMethodAfter = 
		new MappedMethod(new Source(getContainer().getName(), getLine()), 
				 mappedMethodnameAfter, new String[] {
		returnType.getTypename()
	    }, toString());
	} 

	boolean		allMethodsReturnCorrectType = true;
	Communication[] communications = 
	    getCommunications().getCommunications();
	int		iCommunication = 0;

	while (iCommunication < communications.length 
	       && allMethodsReturnCorrectType) {
	    Communication communication = communications[iCommunication];
	    String	  newCodeBefore = communication.getJavaCodeBefore();

	    if (newCodeBefore.length() > 0) {
		codeBefore += newCodeBefore + ToJava.NEWLINE;
	    } 

	    if (mappedMethodAfter != null) {
		communication.setMappedMethodAfter(mappedMethodAfter);

		if (!communication.getMethod().getReturnType().isSubTypeOf(returnType.getType())) {
		    allMethodsReturnCorrectType = false;
		} 
	    } 

	    ++iCommunication;
	} 

	if (!allMethodsReturnCorrectType) {
	    Method invalidMethod = 
		communications[iCommunication - 1].getMethod();

	    throw new SemanticError("Result mismatch. Belonging expression refers to '" 
				    + invalidMethod 
				    + "' which has invalid return type '" 
				    + invalidMethod.getReturnType().getName() 
				    + "'", returnType);
	} 

	/*
	 * if(mappedMethodAfter != null)
	 * {
	 * JavaCode codeBlock = new JavaCode();
	 * String returnVariable = "returnValue";
	 * Communication.toJava(codeBlock, returnType, returnVariable);
	 * 
	 * codeAfter
	 * += Communication.toJava
	 * (
	 * codeBlock,
	 * returnType.getTypename() + " " + returnVariable,
	 * mappedMethodAfter.getMethodname()
	 * ) + ToJava.NEWLINE;
	 * }
	 */
	if (codeCondition.length() > 0 || codeBefore.length() > 0 
		|| codeAfter.length() > 0) {
	    parent.addCodeProducer(this);
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param condition
     * @param nodeCondition
     *
     * @see
     */
    public void setCondition(Expression condition, 
			     Node nodeCondition) {
	idNumberCondition = nextIdNumberCondition++;
	this.condition = condition;
	codeCondition = ToJava.node(nodeCondition);
    } 

    /**
     * Method declaration
     *
     *
     * @param returnType
     *
     * @see
     */
    public void setReturnType(ExchangeParameter returnType) {
	this.returnType = returnType;
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

	if (codeCondition.length() > 0) {
	    javaCode += ToJava.INDENT1 + "public boolean";
	    javaCode += " " + getMappedMethodnameCondition() + "()" 
			+ ToJava.NEWLINE;
	    javaCode += ToJava.INDENT1 + "{return " + codeCondition + ";}" 
			+ ToJava.NEWLINE;
	} 

	javaCode += ToJava.NEWLINE;

	if (codeBefore.length() > 0) {
	    javaCode += codeBefore;
	} 

	if (codeAfter.length() > 0) {
	    javaCode += codeAfter;
	} 

	if (condition != null) {
	    if (condition instanceof ForAllExpression) {
		Class	     parentClass = parent.getMappedClass();
		StringWriter stringWriter = new StringWriter();

		OutputVisitor.generateJassCheckForAll_Exists(parentClass, 
			new PrintWriter(stringWriter), "");

		javaCode += ToJava.NEWLINE;
		javaCode += stringWriter.toString();
	    } 
	} 

	return javaCode;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

