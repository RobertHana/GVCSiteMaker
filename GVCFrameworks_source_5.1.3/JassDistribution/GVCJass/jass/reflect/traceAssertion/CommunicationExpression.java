/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import java.util.*;
import jass.parser.JassParserConstants;
import jass.reflect.Class;     // to avoid conflicts with java.lang.Class
import jass.reflect.Method;    // to avoid conflicts with java.lang.Method
import jass.reflect.*;
import jass.runtime.util.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class CommunicationExpression extends Expression {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    TraceAlphabet	     alphabet;
    Class		     referedClass = null;
    boolean		     hasArbitraryClass = false;
    boolean		     hasArbitraryMethod = false;
    int			     arbitraryMethodLimitation = 
	JassParserConstants.STAR;
    boolean		     hasParameterWildcards = false;
    boolean		     hasThisReference = false;
    int			     communicationType;
    String		     unreflectedName;
    String		     classname;
    String		     name;
    Communication	     communicationToMatch;
    Stack		     stack;
    CommunicationParameter[] parameterCommunication;
    FormalParameter[]	     parameterMethod;
    int			     iParameterCommunication;
    int			     iParameterMethod;
    Hashtable		     methodIdToCommunicationId;
    String		     stringRepresentation;
    SetOfCommunications      matchedCommunications;

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
    public CommunicationExpression() {

	// listeners = new HashSet();
	matchedCommunications = new SetOfCommunications();
	methodIdToCommunicationId = new Hashtable();
    }

    /*
     * ....................................................................
     * . Protected Methods                                                .
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
    protected boolean allCommunicationParametersParsed() {
	return iParameterCommunication >= this.parameterCommunication.length;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    protected boolean allMethodParametersParsed() {
	return iParameterMethod >= parameterMethod.length;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    protected boolean communicationParametersHaveWildcard() {
	if (parameterCommunication.length == 0) {
	    return false;
	} 

	boolean haveWildcard = false;

	for (int iParameter = 0; 
		!haveWildcard && iParameter < parameterCommunication.length; 
		++iParameter) {
	    CommunicationParameter parameter = 
		parameterCommunication[iParameter];

	    // String parametername = parameter.getName();
	    if (parameter.isWildcard()) {
		haveWildcard = true;
	    } 
	} 

	return haveWildcard;
    } 

    /**
     * Method declaration
     *
     *
     * @param string
     *
     * @return
     *
     * @see
     */
    protected boolean communicationParameterMatches(String string) {
	String parameter = 
	    parameterCommunication[iParameterCommunication].getTypename();

	return parameter.compareTo(string) == 0;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    protected void communicationReflected() {
	communicationToMatch.setExpression(this);
	communicationToMatch.reflect();
	matchedCommunications.add(communicationToMatch);

	// System.out.println("##Actual communication set : " + matchedCommunications);
	if (!hasArbitraryMethod && classname.length() == 0) {
	    String communicationId = communicationToMatch.getIdString();
	    String methodId = communicationToMatch.getMethod().getIdString();
	    String referedWithMethodId = 
		(String) methodIdToCommunicationId.get(methodId);

	    if (referedWithMethodId == null) {
		methodIdToCommunicationId.put(methodId, communicationId);
	    } else {
		if (!referedWithMethodId.equals(communicationId)) {
		    throw new ReflectExpressionError("Ambiguous name '" 
						     + methodId 
						     + "', maybe '" 
						     + referedWithMethodId 
						     + "' or '" 
						     + communicationId + "'", 
						     this);
		} 
	    } 
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param result
     *
     * @return
     *
     * @see
     */
    protected boolean matchingResult(boolean result) {

	// Restore old indeces
	restoreValues();

	return result;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    protected boolean methodParameterTypeIsPrimitive() {
	FormalParameter parameter = parameterMethod[iParameterMethod];

	return parameter.getType().isPrimitive();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    protected boolean namesMatch() {
	boolean classnamesMatch;

	if (classname.length() == 0) {
	    classnamesMatch = true;
	} else {
	    classnamesMatch = 
		communicationToMatch.getReflectedClass().getName().equals(classname);
	} 

	boolean methodnamesMatch = false;

	if (classnamesMatch) {
	    methodnamesMatch = 
		communicationToMatch.getMethod().getName().equals(name);
	} 

	return classnamesMatch && methodnamesMatch;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    protected void restoreValues() {
	iParameterMethod = ((Integer) stack.pop()).intValue();
	iParameterCommunication = ((Integer) stack.pop()).intValue();
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    protected void saveValues() {
	stack.push(new Integer(iParameterCommunication));
	stack.push(new Integer(iParameterMethod));
    } 

    /**
     * Method declaration
     *
     *
     * @param _iParameterCommunication
     * @param _iParameterMethod
     *
     * @return
     *
     * @see
     */
    protected boolean doesMatch(int _iParameterCommunication, 
				int _iParameterMethod) {

	// we changes values of parameter indeces. Save values
	// to restore them later.
	saveValues();

	// Set indexes of class fields to make them visible to all members of the class
	iParameterCommunication = _iParameterCommunication;
	iParameterMethod = _iParameterMethod;

	// System.out.println("iC:" + iParameterCommunication + " iM:" + iParameterMethod);
	// Test cases which indicate that matching failed or was successfull
	// (termination of recursion)
	if (allMethodParametersParsed()) {
	    if (allCommunicationParametersParsed()) {
		return matchingResult(true);
	    } else {

		// All method parameters were parsed. If there are unparsed communication
		// parameters all parameters must be of type ARBITRARY, since ARBITRARY also
		// means "no parameter".
		boolean onlyArbitrary = true;

		while (onlyArbitrary &&!allCommunicationParametersParsed()) {
		    onlyArbitrary = 
			communicationParameterMatches(CommunicationParameter.ARBITRARY);
		    ++iParameterCommunication;
		} 

		return matchingResult(onlyArbitrary);
	    } 
	} else if (allCommunicationParametersParsed()) {
	    return matchingResult(false);
	} 

	// Try to match actual parameters.
	// Handle special wildcards ARBITRARY, ONE_ARBITRARY and NULL
	communicationToMatch.setParameter(parameterCommunication[iParameterCommunication], 
					  iParameterCommunication);

	if (communicationParameterMatches(CommunicationParameter.ARBITRARY)) {
	    boolean match = false;

	    ++iParameterCommunication;

	    // ARBITRARY means no, one or more method parameters.
	    match = doesMatch(iParameterCommunication, iParameterMethod);

	    while (!match &&!allMethodParametersParsed()) {
		++iParameterMethod;
		match = doesMatch(iParameterCommunication, iParameterMethod);
	    } 

	    return matchingResult(match);
	} else if (communicationParameterMatches(CommunicationParameter.ONE_ARBITRARY)) {
	    return doesMatch(iParameterCommunication + 1, 
			     iParameterMethod + 1);
	} else if (communicationParameterMatches(CommunicationParameter.NULL)) {
	    if (methodParameterTypeIsPrimitive()) {
		return false;
	    } else {
		return doesMatch(iParameterCommunication + 1, 
				 iParameterMethod + 1);
	    } 
	} else {
	    if (parametersMatch()) {
		return doesMatch(iParameterCommunication + 1, 
				 iParameterMethod + 1);
	    } else {
		return matchingResult(false);
	    } 
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
    protected boolean parametersMatch() {
	FormalParameter parameter = parameterMethod[iParameterMethod];
	String		methodParameterName = parameter.getType().getName();
	String		communicationParameterName = 
	    parameterCommunication[iParameterCommunication].getTypename();

	return communicationParameterName.compareTo(methodParameterName) == 0;
    } 

    /**
     * Method declaration
     *
     *
     * @param classidentifier
     *
     * @return
     *
     * @see
     */
    protected Class getClass(String classidentifier) {
	Class  clazz = null;
	String classname;

	// System.out.println("unreflectedClassname: " + unreflectedClassname);
	if (alphabet != null) {
	    classname = alphabet.getClassname(classidentifier);

	    if (classname == null) {
		throw new ReflectExpressionError("Alphabet does not contain methods of class '" 
						 + classidentifier + "'", 
						 this);
	    } 
	} else {
	    classname = NameAnalysis.expandTypeName(classidentifier, 
						    container);

	    if (classname == null) {
		throw new ReflectExpressionError("Can not reflect name '" 
						 + classidentifier 
						 + "' at class '" 
						 + container.getName() + "'", 
						 this);
	    } 
	} 

	clazz = ClassPool.getClass(classname);

	return clazz;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    protected void reflectExpression() {
	String unreflectedClassname = Method.getUnitName(unreflectedName);

	name = Method.getName(unreflectedName);

	boolean explicitClassname = (unreflectedClassname.length() != 0 
				     || hasThisReference);

	if (hasThisReference) {
	    referedClass = container;
	    classname = referedClass.getName();
	} else {
	    if (explicitClassname) {
		referedClass = getClass(unreflectedClassname);
		classname = referedClass.getName();
	    } else {
		classname = "";
	    } 
	} 

	if (!communicationParametersHaveWildcard() && explicitClassname) {
	    Method method;

	    if (alphabet != null) {
		Communication[] communications;

		communications = 
		    alphabet.getCommunications(classname, name, 
					       getParameterTypes(), 
					       communicationType);

		if (communications == null) {
		    throw new ReflectExpressionError("Communication(s) not found in alphabet", 
						     this);
		} 

		for (int iCommunication = 0; 
			iCommunication < communications.length; 
			++iCommunication) {
		    communicationToMatch = communications[iCommunication];

		    communicationReflected();
		} 
	    } else {
		method = referedClass.getMethodOrConstructor(name, 
			getParameterTypes());

		if (method == null) {
		    throw new ReflectExpressionError("Can not find method " 
						     + name + " in class " 
						     + referedClass.toString(), this);
		} else {
		    communicationToMatch = 
			new Communication(this, parameterCommunication, 
					  referedClass, method, 
					  communicationType);

		    communicationReflected();
		} 
	    } 
	} else {
	    Communication[] communicationArray = null;

	    if (explicitClassname) {
		Method[] methodArray = 
		    referedClass.getMethodsAndConstructors();

		communicationArray = new Communication[methodArray.length];

		for (int iMethod = 0; iMethod < methodArray.length; 
			++iMethod) {
		    communicationArray[iMethod] = new Communication(this, 
			    parameterCommunication, referedClass, 
			    methodArray[iMethod], communicationType);
		} 
	    } else if (alphabet != null) {

		/*
		 * communicationArray = alphabet.getCommunications(classname);
		 * if(communicationArray == null)
		 * {
		 * throw new ReflectExpressionError
		 * (
		 * "Communication(s) not found in alphabet",
		 * this
		 * );
		 * }
		 */
		SetOfCommunications communicationsOfAlphabet = 
		    alphabet.getCommunications();

		switch (communicationType) {

		case Communication.TYPE_BEGIN: {
		    communicationArray = 
			communicationsOfAlphabet.getCommunicationsAtBeginOfMethod();

		    break;
		} 

		case Communication.TYPE_END: {
		    communicationArray = 
			communicationsOfAlphabet.getCommunicationsAtEndOfMethod();

		    break;
		} 

		case Communication.TYPE_BOTH: {
		    communicationArray = 
			communicationsOfAlphabet.getCommunicationsAtBeginAndEnd();

		    break;
		} 
		}
	    } else {
		throw new RuntimeException();
	    } 

	    int numberOfCommunications = communicationArray.length;

	    stack = new Stack();

	    for (int iCommunication = 0; 
		    iCommunication < numberOfCommunications; 
		    ++iCommunication) {
		communicationToMatch = communicationArray[iCommunication];

		// System.out.println("#Try to match: " + communicationToMatch);
		if (namesMatch()) {
		    parameterMethod = 
			communicationToMatch.getMethod().getFormalParameters();

		    if (doesMatch(0, 0)) {
			communicationReflected();

			// System.out.print(" ... MATCHED");
		    } 
		} 

		// System.out.println();
	    } 
	} 
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
     * @return
     *
     * @see
     */
    public SetOfCommunications getCommunications() {
	return matchedCommunications;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Class[] getParameterTypes() {
	Class[] typeList = 
	    new jass.reflect.Class[parameterCommunication.length];

	for (int iType = 0; iType < typeList.length; ++iType) {
	    typeList[iType] = parameterCommunication[iType].getType();
	} 

	return typeList;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getUnreflectedName() {
	return unreflectedName;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean hasWildcards() {
	return hasParameterWildcards;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isArbitrary() {
	return hasArbitraryClass && hasArbitraryMethod;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isArbitraryClass() {
	return hasArbitraryClass;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isArbitraryMethod() {
	return hasArbitraryMethod;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	for (int iParameter = 0; iParameter < parameterCommunication.length; 
		++iParameter) {
	    CommunicationParameter parameter = 
		parameterCommunication[iParameter];

	    parameter.reflect();

	    if (parameter.isWildcard()) {
		this.hasParameterWildcards = true;
	    } 
	} 

	String thisPattern = ToString.token(JassParserConstants.THIS);

	thisPattern += ".";
	hasThisReference = unreflectedName.startsWith(thisPattern);

	// implicit this-reference
	boolean noClassname = (Method.getUnitName(unreflectedName).length() 
			       == 0);

	if (noClassname && alphabet == null) {
	    hasThisReference = true;
	    referedClass = container;
	} 

	reflectExpression();

	/*
	 * System.out.println
	 * (
	 * "Reflected " + toString()
	 * + " (" + container + ")"
	 * + (alphabet != null ? " in alphabet context " : "")
	 * + "to:"
	 * );
	 * System.out.println(matchedCommunications + ".");
	 */
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
     * @param clazz
     *
     * @see
     */
    public void setClass(Class clazz) {
	this.referedClass = clazz;
    } 

    /**
     * Method declaration
     *
     *
     * @param type
     *
     * @see
     */
    public void setCommunicationType(int type) {
	this.communicationType = type;
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
	setContainer(container);
	setLine(line);
    } 

    /**
     * Method declaration
     *
     *
     * @param parameters
     *
     * @see
     */
    public void setParameters(CommunicationParameter[] parameters) {
	parameterCommunication = parameters;
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
     * @param name
     *
     * @see
     */
    public void setUnreflectedName(String name) {
	this.unreflectedName = name;
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

