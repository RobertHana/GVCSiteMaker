/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import java.util.Hashtable;
import java.util.Enumeration;
import jass.runtime.traceAssertion.MethodReference;
import jass.runtime.util.ToJava;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class SetOfCommunications {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    public static final String CLASSNAME = 
	"jass.reflect.traceAssertion.SetOfCommunications";
    public static final String NEWLINE = System.getProperty("line.separator");
    Hashtable		       beginOfMethod;
    Hashtable		       endOfMethod;

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
    public SetOfCommunications() {
	init();
    }

    /**
     * Constructor declaration
     *
     *
     * @param communication
     *
     * @see
     */
    public SetOfCommunications(Communication communication) {
	init();
	add(communication);
    }

    /**
     * Constructor declaration
     *
     *
     * @param communications
     *
     * @see
     */
    public SetOfCommunications(Communication[] communications) {
	init();
	add(communications);
    }

    /**
     * Constructor declaration
     *
     *
     * @param setOfCommunications
     *
     * @see
     */
    public SetOfCommunications(SetOfCommunications setOfCommunications) {
	init();
	add(setOfCommunications);
    }

    /**
     * Constructor declaration
     *
     *
     * @param setOfCommunications
     *
     * @see
     */
    public SetOfCommunications(SetOfCommunications[] setOfCommunications) {
	init();
	add(setOfCommunications);
    }

    /**
     * ********************************************************************
     * Private Methods                                                    *
     * *******************************************************************
     */
    private void init() {
	beginOfMethod = new Hashtable();
	endOfMethod = new Hashtable();
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
     * @param c
     *
     * @see
     */
    public void add(Communication c) {
	switch (c.getType()) {

	case Communication.TYPE_BEGIN: {
	    beginOfMethod.put(c.hashString(), c);

	    break;
	} 

	case Communication.TYPE_END: {
	    endOfMethod.put(c.hashString(), c);

	    break;
	} 

	case Communication.TYPE_BOTH: {
	    Communication begin = c.clone(true);

	    beginOfMethod.put(begin.hashString(), begin);

	    Communication end = c.clone(false);

	    endOfMethod.put(end.hashString(), end);

	    break;
	} 
	}
    } 

    /**
     * Method declaration
     *
     *
     * @param communication
     *
     * @see
     */
    public void add(Communication[] communication) {
	for (int i = 0; i < communication.length; ++i) {
	    Communication c = communication[i];

	    add(c);
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param communications
     *
     * @see
     */
    public void add(SetOfCommunications communications) {
	add(communications.getCommunications());
    } 

    /**
     * Method declaration
     *
     *
     * @param setOfCommunications
     *
     * @see
     */
    public void add(SetOfCommunications[] setOfCommunications) {
	for (int i = 0; i < setOfCommunications.length; ++i) {
	    add(setOfCommunications[i]);
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
    public Communication anyElement() {
	Communication   arbitraryCommunication;
	Communication[] allCommunications = getCommunications();

	arbitraryCommunication = (allCommunications.length == 0 ? null 
				  : allCommunications[0]);

	return arbitraryCommunication;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public MethodReference[] asMethodReferences() {
	int		  numberOfCommunications = beginOfMethod.size() 
						   + endOfMethod.size();
	MethodReference[] methods = 
	    new MethodReference[numberOfCommunications];
	Enumeration       enumeration;
	int		  iMethod = 0;

	enumeration = beginOfMethod.elements();

	while (enumeration.hasMoreElements()) {
	    Communication communication = 
		(Communication) enumeration.nextElement();

	    methods[iMethod++] = 
		new MethodReference(communication.getReflectedClass().getPackageName(), 
				    communication.getReflectedClass().getIdentifier(), 
				    communication.getMethod().getIdString(), 
				    true);
	} 

	enumeration = endOfMethod.elements();

	while (enumeration.hasMoreElements()) {
	    Communication communication = 
		(Communication) enumeration.nextElement();

	    methods[iMethod++] = 
		new MethodReference(communication.getReflectedClass().getPackageName(), 
				    communication.getReflectedClass().getIdentifier(), 
				    communication.getMethod().getIdString(), 
				    false);
	} 

	return methods;
    } 

    /**
     * Method declaration
     *
     *
     * @param c
     *
     * @return
     *
     * @see
     */
    public boolean contains(Communication c) {
	boolean contains = false;

	switch (c.getType()) {

	case Communication.TYPE_BEGIN: {
	    contains = beginOfMethod.containsKey(c.hashString());

	    break;
	} 

	case Communication.TYPE_END: {
	    contains = endOfMethod.containsKey(c.hashString());

	    break;
	} 

	case Communication.TYPE_BOTH: {
	    contains = beginOfMethod.containsKey(c.hashString()) 
		       && endOfMethod.containsKey(c.hashString());

	    break;
	} 
	}

	return contains;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Communication[] getCommunications() {
	Hashtable communications = new Hashtable();

	communications.putAll(beginOfMethod);
	communications.putAll(endOfMethod);

	Communication[] communicationArray = 
	    new Communication[communications.size()];

	communications.values().toArray(communicationArray);

	return communicationArray;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Communication[] getCommunicationsAtBeginOfMethod() {
	Communication[] communications = 
	    new Communication[beginOfMethod.size()];

	beginOfMethod.values().toArray(communications);

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
    public Communication[] getCommunicationsAtEndOfMethod() {
	Communication[] communications = 
	    new Communication[endOfMethod.size()];

	endOfMethod.values().toArray(communications);

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
    public Communication[] getCommunicationsAtBeginAndEnd() {
	SetOfCommunications communicationsAtBeginAndEnd = 
	    new SetOfCommunications();
	Communication[]     communicationsAtBegin = 
	    getCommunicationsAtBeginOfMethod();

	for (int iCommunication = 0; 
		iCommunication < communicationsAtBegin.length; 
		++iCommunication) {
	    Communication communicationAtBegin = 
		communicationsAtBegin[iCommunication];
	    Communication communicationAtEnd = 
		communicationAtBegin.clone(false);

	    if (endOfMethod.containsKey(communicationAtEnd.hashString())) {
		communicationsAtBeginAndEnd.add(communicationAtBegin);
		communicationsAtBeginAndEnd.add(communicationAtEnd);
	    } 
	} 

	return communicationsAtBeginAndEnd.getCommunications();
    } 

    /*
     * public SetOfCommunications getCommunicationsAtBeginOfMethod()
     * {
     * jass.runtime.traceAssertion.SetOfCommunications runtimeCommunications
     * = new jass.runtime.traceAssertion.SetOfCommunications();
     * 
     * Iterator iterator = beginOfMethod.iterator();
     * while(iterator.hasNext())
     * {
     * Communication communication = (Communication) iterator.next();
     * runtimeCommunications.add(communication.asRuntimeCommunication(true));
     * }
     * return runtimeCommunications;
     * }
     * 
     * public jass.runtime.traceAssertion.SetOfCommunications getCommunicationsAtEndOfMethod()
     * {
     * jass.runtime.traceAssertion.SetOfCommunications runtimeCommunications
     * = new jass.runtime.traceAssertion.SetOfCommunications();
     * 
     * Iterator iterator = endOfMethod.iterator();
     * while(iterator.hasNext())
     * {
     * Communication communication = (Communication) iterator.next();
     * runtimeCommunications.add(communication.asRuntimeCommunication(false));
     * }
     * return runtimeCommunications;
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
    public boolean isEmpty() {
	return beginOfMethod.isEmpty() && endOfMethod.isEmpty();
    } 

    /**
     * Method declaration
     *
     *
     * @param setOfCom2
     *
     * @see
     */
    public void section(SetOfCommunications setOfCom2) {
	Communication[] comm1Array = getCommunications();
	Communication[] comm2Array = setOfCom2.getCommunications();

	for (int i = 0; i < comm1Array.length; ++i) {
	    Communication communication = comm1Array[i];

	    if (!setOfCom2.contains(communication)) {
		remove(communication);
	    } 
	} 

	for (int i = 0; i < comm2Array.length; ++i) {
	    Communication communication = comm2Array[i];

	    if (!contains(communication)) {
		remove(communication);
	    } 
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param communication
     *
     * @see
     */
    public void remove(Communication communication) {
	switch (communication.getType()) {

	case Communication.TYPE_BEGIN: {
	    beginOfMethod.remove(communication.hashString());

	    break;
	} 

	case Communication.TYPE_END: {
	    endOfMethod.remove(communication.hashString());

	    break;
	} 

	case Communication.TYPE_BOTH: {
	    Communication begin = communication.clone(true);

	    beginOfMethod.remove(begin.hashString());

	    Communication end = communication.clone(false);

	    endOfMethod.remove(end.hashString());

	    break;
	} 
	}
    } 

    /**
     * Method declaration
     *
     *
     * @param exceptions
     *
     * @return
     *
     * @see
     */
    public SetOfCommunications without(SetOfCommunications exceptions) {
	Communication[] communications = exceptions.getCommunications();

	for (int iCommunication = 0; iCommunication < communications.length; 
		++iCommunication) {
	    Communication communication = communications[iCommunication];

	    remove(communication);
	} 

	return this;
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

	javaCode += lineIndent + "new " + classname + "( new " 
		    + jass.runtime.traceAssertion.MethodReference.CLASSNAME 
		    + "[] {" + ToJava.NEWLINE;

	MethodReference[] methods = asMethodReferences();

	for (int iMethod = 0; iMethod < methods.length; ++iMethod) {
	    MethodReference method = methods[iMethod];

	    javaCode += lineIndent + "  " + method.toJava();

	    if (iMethod + 1 < methods.length) {
		javaCode += ",";
	    } 

	    javaCode += ToJava.NEWLINE;
	} 

	javaCode += ToJava.INDENT1 + "})";

	return javaCode;
    } 

    /*
     * public String toJava(String lineIndent)
     * {
     * String code = "";
     * code += lineIndent + "new " + CLASSNAME + NEWLINE;
     * code += lineIndent + "(new " + Communication.CLASSNAME + "[]{" + NEWLINE;
     * Communication[] communicationList = getCommunications();
     * for(int i = 0; i < communicationList.length; ++i)
     * {
     * code += communicationList[i].toJava();
     * if(i + 1 < communicationList.length)
     * {
     * code += ", ";
     * }
     * code += NEWLINE;
     * }
     * code += lineIndent + "})";
     * 
     * return code;
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
    public String toString() {
	String s = "";

	s += "{";

	Communication[] communications = getCommunications();

	for (int i = 0; i < communications.length; ++i) {
	    s += communications[i].toString();

	    if (i + 1 < communications.length) {
		s += ",";
	    } 
	} 

	s += "}";

	return s;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

