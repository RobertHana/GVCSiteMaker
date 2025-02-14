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
public class CommunicationWildcardExpression extends CommunicationExpression {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    String unreflectedWildcardname;

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
    public CommunicationWildcardExpression() {}

    /*
     * ....................................................................
     * . Protected Methods                                                .
     * ....................................................................
     */

    /**
     * Method declaration
     *
     *
     * @see
     */
    protected void reflectArbitraryClass() {

	// System.out.println("Reflect arbitrary class");
	if (alphabet != null) {
	    String[] classnames = alphabet.getClassnames();
	    String   methodName = Method.getName(unreflectedWildcardname);

	    for (int iClass = 0; iClass < classnames.length; ++iClass) {
		String classname = classnames[iClass];

		this.unreflectedName = classname + "." + methodName;

		super.reflectExpression();
	    } 
	} else {
	    throw new ReflectExpressionError("Arbitrary class declaration ist not allowed in alphabet", 
					     this);
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    protected void reflectArbitraryMethod() {
	if (hasThisReference) {
	    referedClass = container;
	} else {
	    String unreflectedClassname = 
		Method.getUnitName(unreflectedWildcardname);

	    referedClass = getClass(unreflectedClassname);
	} 

	Communication[] communicationsToMatch;

	if (alphabet != null) {
	    communicationsToMatch = 
		alphabet.getCommunications(referedClass.getName());
	} else {
	    Method[] methods = referedClass.getMethodsAndConstructors();

	    communicationsToMatch = new Communication[methods.length];

	    for (int iMethod = 0; iMethod < methods.length; ++iMethod) {
		Method		  method = methods[iMethod];
		FormalParameter[] methodParameters = 
		    method.getFormalParameters();

		parameterCommunication = 
		    new TypedParameter[methodParameters.length];

		for (int iParameter = 0; 
			iParameter < methodParameters.length; ++iParameter) {
		    FormalParameter methodParameter = 
			methodParameters[iParameter];
		    TypedParameter  communicationParameter = 
			new TypedParameter(methodParameter.getType());

		    parameterCommunication[iParameter] = 
			communicationParameter;
		} 

		Communication communicationToMatch = new Communication(this, 
			parameterCommunication, referedClass, method, 
			communicationType);

		communicationsToMatch[iMethod] = communicationToMatch;
	    } 
	} 

	for (int iCommunication = 0; 
		iCommunication < communicationsToMatch.length; 
		++iCommunication) {
	    this.communicationToMatch = communicationsToMatch[iCommunication];

	    Method methodToMatch = communicationToMatch.getMethod();

	    if ((arbitraryMethodLimitation == JassParserConstants.STAR) 
		    || (arbitraryMethodLimitation == JassParserConstants.PUBLIC && methodToMatch.isPublic()) 
		    || (arbitraryMethodLimitation == JassParserConstants.PROTECTED && methodToMatch.isProtected()) 
		    || (arbitraryMethodLimitation == JassParserConstants.PACKAGE && methodToMatch.isPackage()) 
		    || (arbitraryMethodLimitation 
			== JassParserConstants.PRIVATE && methodToMatch.isPrivate())) {

		// System.out.println(communicationToMatch);
		communicationReflected();
	    } 
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    protected void reflectExpression() {

	// System.out.println("Reflect arbitrary communication expression with alphabet:" + alphabet);
	if (isArbitrary()) {
	    matchedCommunications = alphabet.getCommunications();
	} else if (hasArbitraryMethod) {
	    reflectArbitraryMethod();
	} else if (hasArbitraryClass) {
	    reflectArbitraryClass();
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
    public int getMethodLimitation() {
	return arbitraryMethodLimitation;
    } 

    /**
     * Method declaration
     *
     *
     * @param kind
     *
     * @see
     */
    public void setMethodLimitation(int kind) {
	arbitraryMethodLimitation = kind;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void setToArbitraryClass() {
	hasArbitraryClass = true;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void setToArbitraryMethod() {
	hasArbitraryMethod = true;
	parameterCommunication = new CommunicationParameter[0];
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
	this.unreflectedWildcardname = name;
	this.unreflectedName = name;
    } 

    /*
     * public String toString()
     * {
     * String string = "";
     * switch(arbitraryMethodLimitation)
     * {
     * case JassParserConstants.PUBLIC:
     * case JassParserConstants.PROTECTED:
     * case JassParserConstants.PRIVATE:
     * {
     * String tokenImage = ToString.token(arbitraryMethodLimitation);
     * string += tokenImage.substring(1, tokenImage.length() -1) + " ";
     * }
     * }
     * string += super.toString(unreflectedWildcardname);
     * 
     * return string;
     * }
     */
}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

