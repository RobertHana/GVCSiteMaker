/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import java.util.Arrays;
import java.util.Vector;
import jass.reflect.Class;    // to avoid conflicts with 'java.lang.class'
import jass.reflect.*;
import jass.runtime.traceAssertion.*;
import jass.runtime.util.ToJava;

/**
 * A communication describes a method call.
 * It can signal the beginning or the end of a method call or both.
 */
public class Communication implements Cloneable {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    /* constants */

    /**
     * the communication points just to the beginning of a method call
     */
    public static final int    TYPE_BEGIN = 1;

    /**
     * the communication points just to the end of a method call
     */
    public static final int    TYPE_END = 2;

    /**
     * the communication contains a complete method call (begin and end)
     */
    public static final int    TYPE_BOTH = 3;

    /**
     * name prefix for mapped methods which are invoked before communication
     */
    public static final String METHOD_IDENTIFIER_BEFORE = "before_";

    // id counter for mapped methods
    static int		       idMethodnameBefore = 0;

    /**
     * method which is invoked before the communication appears at runtime
     */
    MappedMethod	       mappedMethodBefore = null;

    /**
     * method which is invoked after the communication appears at runtime
     */
    MappedMethod	       mappedMethodAfter = null;
    String		       javaCodeBefore = "";
    CommunicationExpression    expression;

    // Remark: Bug-Workaround!
    // Parameters were represented by an array of 'CommunicationParameter'.
    // This caused an 'ArrayStoreException' when calling
    // 'setParameter(CommunicationParameter, int)'. Hence a vector is used instead of an array.
    Vector		       parameters;

    // A communications is characterized by ...
    Method		       method;    // the method (-call)
    Class		       reflectedClass;    // the class of the method is declared in
    int			       type;      // the type of communication

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    /**
     * Constructor declaration
     *
     *
     * @param expression
     * @param parameters
     * @param clazz
     * @param method
     * @param type
     *
     * @see
     */
    public Communication(CommunicationExpression expression, 
			 CommunicationParameter[] parameters, Class clazz, 
			 Method method, int type) {
	this.expression = expression;
	this.parameters = new Vector(Arrays.asList(parameters));
	this.reflectedClass = clazz;
	this.method = method;
	this.type = type;
    }

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */

    /**
     * Convert a reflected communication to a runtime method reference.
     * A reference is either a begin or an end of a method call. Hence
     * this information is passed as a parameter.
     */
    public MethodReference asMethodReference(boolean beginOfMethod) {
	MethodReference methodReference;

	// note: the information of any mapped methods (after, before) is lost.
	methodReference = new MethodReference(reflectedClass.getPackageName(), 
					      reflectedClass.getIdentifier(), 
					      method.getIdString(), 
					      beginOfMethod);

	return methodReference;
    } 

    /**
     * Convert a reflected communication to a runtime communication.
     * A runtime communication is either a begin or an end of a method call. Hence
     * this information is passed as a parameter.
     */
    public jass.runtime.traceAssertion.Communication asRuntimeCommunication(boolean beginOfMethod) {
	jass.runtime.traceAssertion.Communication runtimeCommunication;

	// note: only the informations of the according mapped methods are preserved
	runtimeCommunication = 
	    new jass.runtime.traceAssertion.Communication(reflectedClass.getPackageName(), 
		reflectedClass.getIdentifier(), method.getIdString(), 
		beginOfMethod, beginOfMethod ? mappedMethodBefore : null, 
		beginOfMethod ? null : mappedMethodAfter);

	return runtimeCommunication;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Object clone() {
	Communication clone = null;

	try {
	    clone = (Communication) super.clone();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	} 

	clone.javaCodeBefore = this.javaCodeBefore;
	clone.mappedMethodAfter = this.mappedMethodAfter;
	clone.mappedMethodBefore = this.mappedMethodBefore;
	clone.method = this.method;
	clone.parameters = this.parameters;
	clone.type = this.type;

	return clone;
    } 

    /**
     * Method declaration
     *
     *
     * @param type
     *
     * @return
     *
     * @see
     */
    public Communication clone(boolean type) {
	Communication clone = (Communication) clone();

	if (type == true) {
	    clone.type = TYPE_BEGIN;
	} else {
	    clone.javaCodeBefore = "";
	    clone.type = TYPE_END;
	} 

	return clone;
    } 

    /**
     * Two communications are equal if got the same id string.
     * @see getIdString()
     */
    public boolean equals(Object o) {

	// System.out.println(this + " <-> " + o);
	if (!(o instanceof Communication)) {
	    return false;
	} 

	Communication other = (Communication) o;

	return getIdString().equals(other.getIdString());
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public CommunicationExpression getExpression() {
	return expression;
    } 

    /**
     * A communication is identified by the name of the class, the method name
     * and kind and order of method parameters
     */
    public String getIdString() {
	return reflectedClass.getName() + "." + method.getIdString();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getJavaCodeBefore() {
	return javaCodeBefore;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Method getMethod() {
	return method;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public CommunicationParameter[] getParameters() {
	CommunicationParameter[] parameterArray = 
	    new CommunicationParameter[parameters.size()];

	parameters.toArray(parameterArray);

	return parameterArray;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Class getReflectedClass() {
	return reflectedClass;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public int getType() {
	return type;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public int hashCode() {
	return hashString().hashCode();
    } 

    /**
     * Like id string, but considers communication type as well
     * @see getIdString()
     */
    public String hashString() {
	return getIdString() + type;
    } 

    /**
     * This is a special test of equality of communications.
     * The classname may be ommited. In this case no test for equality of class names
     * is performed.
     * @see equals(java.lang.Object)
     */
    public boolean matchesWith(String otherClassname, String otherMethodname, 
			       Class[] otherParameters) {
	boolean classnameGiven = (otherClassname.length() != 0);

	if (classnameGiven) {
	    String myClassname = reflectedClass.getName();

	    // System.out.println("Classname: " + myClassname + " <-> " + otherClassname);
	    if (!myClassname.equals(otherClassname)) {
		return false;
	    } 
	} 

	String myMethodname = method.getName();

	// System.out.println("Methodname: " + myMethodname + " <-> " + otherMethodname);
	if (!myMethodname.equals(otherMethodname)) {
	    return false;
	} 

	FormalParameter[] myParameters = method.getFormalParameters();
	boolean		  sameNumberOfParameter = myParameters.length 
						  == otherParameters.length;

	if (!sameNumberOfParameter) {
	    return false;
	} 

	boolean parametersMatch = true;

	for (int iParameter = 0; 
		parametersMatch && iParameter < otherParameters.length; 
		++iParameter) {
	    String myTypename = myParameters[iParameter].getType().getName();
	    String otherTypename = otherParameters[iParameter].getUnitName();

	    parametersMatch = myTypename.equals(otherTypename);
	} 

	if (!parametersMatch) {
	    return false;
	} 

	/*
	 * boolean typesMatch = (type == otherType);
	 * System.out.println(type + " <-> " + otherType);
	 */
	return parametersMatch;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {

	// For data exchange between the process of a trace assertion and the running program
	// mappedMethodBefore is used. To realize an exchange we need to
	// map the parameters of this communication to the arguments of the mapped method.
	JavaCode	  codeBefore = new JavaCode();

	// parameter declaration of the mapped method as java code
	String		  parameterString = "";
	FormalParameter[] methodParameters = method.getFormalParameters();

	for (int iParameter = 0; iParameter < methodParameters.length; 
		++iParameter) {
	    FormalParameter methodParameter = methodParameters[iParameter];
	    String	    variableParameter = "par" + iParameter;

	    parameterString += methodParameter.getType().toJava() + " " 
			       + variableParameter;

	    if (iParameter + 1 < methodParameters.length) {
		parameterString += ", ";
	    } 
	} 

	// Each parameter for data exchange produces certain java code.
	CommunicationParameter[] parameterArray = getParameters();

	for (int iParameter = 0; iParameter < parameterArray.length; 
		++iParameter) {
	    CommunicationParameter communicationParameter = 
		parameterArray[iParameter];
	    String		   variableParameter = "par" + iParameter;

	    if (communicationParameter instanceof ExchangeParameter) {
		ExchangeParameter exchangeParameter = 
		    (ExchangeParameter) communicationParameter;

		toJava(codeBefore, exchangeParameter, variableParameter);
	    } 
	} 

	// after all parameters are reflected we can define the data structure
	// for the mapped method
	if (!codeBefore.isEmpty()) {
	    String mappedMethodnameBefore = METHOD_IDENTIFIER_BEFORE 
					    + idMethodnameBefore++;

	    javaCodeBefore = toJava(codeBefore, parameterString, 
				    mappedMethodnameBefore);

	    String[] parameterTypenames = new String[methodParameters.length];

	    for (int iParameter = 0; iParameter < parameterTypenames.length; 
		    ++iParameter) {
		FormalParameter parameter = methodParameters[iParameter];
		String		parameterTypename = 
		    parameter.getType().getName();

		parameterTypenames[iParameter] = parameterTypename;
	    } 

	    mappedMethodBefore = 
		new MappedMethod(new Source(getExpression().getContainer().getName(), getExpression().getLine()), 
				 mappedMethodnameBefore, parameterTypenames, 
				 getExpression().toString());
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param method
     *
     * @see
     */
    public void setMappedMethodAfter(MappedMethod method) {
	this.mappedMethodAfter = method;
    } 

    /**
     * Method declaration
     *
     *
     * @param parameter
     * @param parameterNumber
     *
     * @see
     */
    public void setParameter(CommunicationParameter parameter, 
			     int parameterNumber) {

	// System.out.println("##" + this + "##" + parameter.getClass() + "##" + parameters[parameterNumber].getClass() + "##" + parameters.length);
	parameters.setElementAt(parameter, parameterNumber);
    } 

    /**
     * Method declaration
     *
     *
     * @param expression
     *
     * @see
     */
    public void setExpression(CommunicationExpression expression) {
	this.expression = expression;
    } 

    /**
     * Method declaration
     *
     *
     * @param code
     * @param exchangeParameter
     * @param variableParameter
     *
     * @see
     */
    public static void toJava(JavaCode code, 
			      ExchangeParameter exchangeParameter, 
			      String variableParameter) {
	if (exchangeParameter.isInputParameter()) {
	    code.javaCodeInput += exchangeParameter.getName() + " = " 
				  + variableParameter + ";" + ToJava.NEWLINE;
	} else if (exchangeParameter.isOutputParameter()) {
	    code.javaCodeOutput += exchangeParameter.getName();

	    if (exchangeParameter.getType().isPrimitive()) {
		code.javaCodeOutput += " == " + variableParameter;
	    } else {
		code.javaCodeOutput += ".equals(" + variableParameter + ")";
	    } 

	    code.javaCodeOutput += " && " + ToJava.NEWLINE;
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param code
     * @param parameterString
     * @param mappedMethodname
     *
     * @return
     *
     * @see
     */
    public static String toJava(JavaCode code, String parameterString, 
				String mappedMethodname) {
	String javaCode = "";

	parameterString = "(" + parameterString + ")";

	if (code.javaCodeInput.length() > 0 
		| code.javaCodeOutput.length() > 0) {
	    javaCode += ToJava.INDENT1 + "public boolean " + mappedMethodname 
			+ parameterString + "{" + ToJava.NEWLINE;

	    if (code.javaCodeInput.length() > 0) {
		javaCode += ToJava.INDENT2 + code.javaCodeInput 
			    + ToJava.NEWLINE;
	    } 

	    // javaCode += "System.out.println(\"##at: " + mappedMethodname + "\");" + ToJava.NEWLINE;
	    code.javaCodeOutput = "return " + code.javaCodeOutput + "true;";
	    javaCode += ToJava.INDENT2 + code.javaCodeOutput + ToJava.NEWLINE;
	    javaCode += ToJava.INDENT1 + "}" + ToJava.NEWLINE;
	} 

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
    public String toString() {
	String string = "";

	string += getReflectedClass().getName() + ":";

	if (method.isProtected()) {
	    string += "protected ";
	} else if (method.isPublic()) {
	    string += "public ";
	} 

	string += method.toString();
	string += "_";

	switch (type) {

	case TYPE_BEGIN:
	    string += "BEGIN";

	    break;

	case TYPE_END:
	    string += "END";

	    break;

	case TYPE_BOTH:
	    string += "BOTH";

	    break;
	}

	return string;
    } 

    /**
     * Method declaration
     *
     *
     * @param parameters
     *
     * @return
     *
     * @see
     */
    public static String toString(CommunicationParameter[] parameters) {
	String string = "";

	string += "(";

	for (int iParameter = 0; iParameter < parameters.length; 
		++iParameter) {
	    CommunicationParameter parameter = parameters[iParameter];

	    string += parameter.toString();

	    if (iParameter + 1 < parameters.length) {
		string += ", ";
	    } 
	} 

	string += ")";

	return string;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

