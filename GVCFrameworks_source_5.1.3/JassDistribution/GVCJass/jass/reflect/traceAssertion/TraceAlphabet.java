/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import java.util.*;
import jass.reflect.Class;    // to avoid conflicts with java.lang.class
import jass.reflect.*;
import jass.runtime.util.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class TraceAlphabet {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    public static final String FIELDNAME_ALPHABET = "alphabet";
    TraceAssertionExpression   parent;
    Hashtable		       classIdentifierToClassname;
    Hashtable		       classnameToCommunications;
    SetOfCommunications	       communications;
    CommunicationExpressions   expressions;
    HashSet		       classes;

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
    public TraceAlphabet() {
	classIdentifierToClassname = new Hashtable();
	classnameToCommunications = new Hashtable();
	classes = new HashSet();
	communications = new SetOfCommunications();
    }

    /*
     * ....................................................................
     * . Protected Methods                                                .
     * ....................................................................
     */

    /*
     * protected static String toJava(Communication communication)
     * {
     * String javaCode = "";
     * switch(communication.getType())
     * {
     * case Communication.TYPE_BEGIN :
     * {
     * javaCode += ToJava.INDENT2 +
     * communication.asRuntimeCommunication
     * (
     * jass.runtime.traceAssertion.Communication.BEGIN
     * ).toJava();
     * 
     * break;
     * }
     * case Communication.TYPE_END :
     * {
     * javaCode += ToJava.INDENT2 +
     * communication.asRuntimeCommunication
     * (
     * !jass.runtime.traceAssertion.Communication.BEGIN
     * ).toJava();
     * break;
     * }
     * default:
     * {
     * javaCode += ToJava.INDENT2 +
     * communication.asRuntimeCommunication
     * (
     * jass.runtime.traceAssertion.Communication.BEGIN
     * ).toJava();
     * javaCode += ", " + ToJava.NEWLINE;
     * javaCode +=
     * communication.asRuntimeCommunication
     * (
     * !jass.runtime.traceAssertion.Communication.BEGIN
     * ).toJava();
     * break;
     * }
     * }
     * return javaCode;
     * }
     */

    /*
     * protected static String toJava(String communicationName, boolean beginOfMethod)
     * {
     * String javaCode = "";
     * 
     * javaCode +=
     * ToJava.INDENT2 + "new " + jass.runtime.traceAssertion.Communication.CLASSNAME + "(\""
     * + communicationName + "\""
     * + ", " + (beginOfMethod ? "true" : "false")
     * + ")";
     * return javaCode;
     * }
     */

    /**
     * Method declaration
     *
     *
     * @param expandedClassname
     * @param methodname
     * @param methodParameter
     * @param potentialCommunications
     *
     * @return
     *
     * @see
     */
    public Communication[] lookupCommunications(String expandedClassname, 
						String methodname, 
						Class[] methodParameter, 
						Communication[] potentialCommunications) {
	Vector foundCommunications = new Vector();

	for (int iCommunication = 0; 
		iCommunication < potentialCommunications.length; 
		++iCommunication) {
	    Communication communication;

	    communication = potentialCommunications[iCommunication];

	    if (communication.matchesWith(expandedClassname, methodname, 
					  methodParameter)) {
		foundCommunications.add(communication);
	    } 
	} 

	Communication[] communicationArray = null;
	int		numberOfCommunicationsFound = 
	    foundCommunications.size();

	if (numberOfCommunicationsFound > 0) {
	    communicationArray = 
		new Communication[numberOfCommunicationsFound];

	    foundCommunications.toArray(communicationArray);
	} 

	return communicationArray;
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
     * @param communications
     *
     * @see
     */
    public void add(SetOfCommunications communications) {

	// System.out.println("Add: " + deUniOldenburgInformatik.musik.javaTools.StringTools.toString(communications));
	this.communications.add(communications);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Class[] getClasses() {
	Class[] classArray = new Class[classes.size()];

	classes.toArray(classArray);

	return classArray;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String[] getClassnames() {
	String[] classnames = new String[classnameToCommunications.size()];

	classnameToCommunications.keySet().toArray(classnames);

	return classnames;
    } 

    /**
     * Method declaration
     *
     *
     * @param classidentifier
     * @param methodname
     * @param methodParameter
     * @param type
     *
     * @return
     *
     * @see
     */
    public Communication[] getCommunications(String classidentifier, 
					     String methodname, 
					     Class[] methodParameter, 
					     int type) {
	SetOfCommunications referedCommunications;
	String		    classname;
	boolean		    arbitraryClasses = (classidentifier.length() 
						== 0);

	if (!arbitraryClasses) {
	    classname = getClassname(classidentifier);

	    if (classname == null) {
		return null;
	    } 

	    referedCommunications = 
		(SetOfCommunications) classnameToCommunications.get(classname);
	} else {
	    referedCommunications = new SetOfCommunications();
	    classname = classidentifier;

	    Enumeration communicationsEnumeration = 
		classnameToCommunications.elements();

	    while (communicationsEnumeration.hasMoreElements()) {
		SetOfCommunications communications = 
		    (SetOfCommunications) communicationsEnumeration.nextElement();

		referedCommunications.add(communications);
	    } 
	} 

	if (referedCommunications == null 
		|| referedCommunications.isEmpty()) {
	    return null;
	} 

	Communication[] foundCommunications = null;

	switch (type) {

	case Communication.TYPE_BEGIN: {
	    foundCommunications = 
		lookupCommunications(classname, methodname, methodParameter, 
				     referedCommunications.getCommunicationsAtBeginOfMethod());

	    break;
	} 

	case Communication.TYPE_END: {
	    foundCommunications = 
		lookupCommunications(classname, methodname, methodParameter, 
				     referedCommunications.getCommunicationsAtEndOfMethod());

	    break;
	} 

	case Communication.TYPE_BOTH: {
	    foundCommunications = 
		lookupCommunications(classname, methodname, methodParameter, 
				     referedCommunications.getCommunications());

	    if (foundCommunications != null 
		    && foundCommunications.length < 2) {
		foundCommunications = null;
	    } 

	    break;
	} 
	}

	return foundCommunications;
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
     * @param classidentifier
     *
     * @return
     *
     * @see
     */
    public Communication[] getCommunications(String classidentifier) {
	SetOfCommunications referedCommunications;
	String		    classname;
	boolean		    arbitraryClasses = (classidentifier.length() 
						== 0);

	if (!arbitraryClasses) {
	    classname = getClassname(classidentifier);

	    if (classname == null) {
		return null;
	    } 

	    referedCommunications = 
		(SetOfCommunications) classnameToCommunications.get(classname);
	} else {
	    referedCommunications = getCommunications();
	} 

	return referedCommunications.getCommunications();
    } 

    /**
     * Method declaration
     *
     *
     * @param classIdentifier
     *
     * @return
     *
     * @see
     */
    public String getClassname(String classIdentifier) {
	String  classname;
	boolean isAllreadyClassname;

	if (classIdentifier.length() == 0) {
	    return null;
	} 

	isAllreadyClassname = (classIdentifier.indexOf('.') != -1);

	if (isAllreadyClassname) {
	    classname = classIdentifier;
	} else {
	    classname = 
		(String) classIdentifierToClassname.get(classIdentifier);

	    // System.out.println("Expanded " + name + " to " + expandedClassname);
	} 

	return classname;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public TraceAssertionExpression getParent() {
	return parent;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	if (expressions != null) {
	    expressions.reflect();
	    communications.add(expressions.getCommunications());
	} 

	Communication[] communicationArray = 
	    communications.getCommunications();

	for (int iCommunication = 0; 
		iCommunication < communicationArray.length; 
		++iCommunication) {
	    Communication communication = communicationArray[iCommunication];
	    String	  classname;
	    String	  classIdentifier;
	    Class	  clazz = communication.getReflectedClass();

	    classes.add(clazz);

	    classname = clazz.getName();
	    classIdentifier = Class.getIdentifier(classname);

	    classIdentifierToClassname.put(classIdentifier, classname);

	    SetOfCommunications communicationsReferedWithClassname;

	    communicationsReferedWithClassname = 
		(SetOfCommunications) classnameToCommunications.get(classname);

	    if (communicationsReferedWithClassname == null) {
		communicationsReferedWithClassname = 
		    new SetOfCommunications();

		classnameToCommunications.put(classname, 
					      communicationsReferedWithClassname);
	    } 

	    communicationsReferedWithClassname.add(communication);
	} 

	// System.out.println("Alphabet:");
	// System.out.println(communications.toString());
    } 

    /*
     * public boolean remove(Communication communication)
     * {
     * return communications.remove(communication);
     * }
     */

    /**
     * Method declaration
     *
     *
     * @param expressions
     *
     * @see
     */
    public void setCommunications(CommunicationExpressions expressions) {
	this.expressions = expressions;
    } 

    /**
     * Method declaration
     *
     *
     * @param parent
     *
     * @see
     */
    public void setParent(TraceAssertionExpression parent) {
	this.parent = parent;
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
	String javaCode = "";
	String classname = jass.runtime.traceAssertion.SetOfMethods.CLASSNAME;

	javaCode += lineIndent + "public static final " + classname + " " 
		    + FIELDNAME_ALPHABET + " = " + ToJava.NEWLINE 
		    + communications.toJava(lineIndent + "  ");
	javaCode += ";" + ToJava.NEWLINE;
	javaCode += lineIndent + "public " + classname + " getAlphabet()" 
		    + "{return " + FIELDNAME_ALPHABET + ";}" + ToJava.NEWLINE;

	return javaCode;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

