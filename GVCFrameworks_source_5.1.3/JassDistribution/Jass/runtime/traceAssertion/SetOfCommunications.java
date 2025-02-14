/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import java.util.Hashtable;
import jass.runtime.util.ToJava;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class SetOfCommunications {
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.SetOfCommunications";
    Hashtable		       set;

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
     * ********************************************************************
     * Private Methods                                                    *
     * *******************************************************************
     */
    private void init() {
	set = new Hashtable();
    } 

    /**
     * ********************************************************************
     * Public Methods                                                     *
     * *******************************************************************
     */
    public void add(Communication c) {
	set.put(c.hashString(), c);
    } 

    /**
     * Method declaration
     *
     *
     * @param communications
     *
     * @see
     */
    public void add(Communication[] communications) {
	for (int iCommunication = 0; iCommunication < communications.length; 
		++iCommunication) {
	    Communication communication = communications[iCommunication];

	    add(communication);
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
     * @param methodReference
     *
     * @return
     *
     * @see
     */
    public boolean contains(MethodReference methodReference) {
	return set.containsKey(methodReference.hashString());
    } 

    /**
     * Method declaration
     *
     *
     * @param communication
     *
     * @return
     *
     * @see
     */
    public boolean contains(Communication communication) {
	return set.containsKey(communication.hashString());
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
	return (Communication) set.get(methodReference.hashString());
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
	Communication[] communications = new Communication[set.size()];

	set.values().toArray(communications);

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
    public boolean isEmpty() {
	return set.isEmpty();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public SetOfCommunications prepareForLookahead() {
	SetOfCommunications forLookahead = new SetOfCommunications();
	Communication[]     communications = getCommunications();

	for (int iCommunication = 0; iCommunication < communications.length; 
		++iCommunication) {
	    Communication communication = communications[iCommunication];

	    forLookahead.add(new Communication(communication.getPackagename(), 
					       communication.getClassname(), 
					       communication.getMethodname(), 
					       communication.isBeginOfMethod(), 
					       null, null));
	} 

	return forLookahead;
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
	String code = "";

	code += lineIndent + "new " + CLASSNAME + ToJava.NEWLINE;
	code += lineIndent + "(new " + Communication.CLASSNAME + "[]{" 
		+ ToJava.NEWLINE;

	Communication[] communicationList = getCommunications();

	for (int i = 0; i < communicationList.length; ++i) {
	    code += communicationList[i].toJava(lineIndent + "  ");

	    if (i + 1 < communicationList.length) {
		code += ", ";
	    } 

	    code += ToJava.NEWLINE;
	} 

	code += lineIndent + "})";

	return code;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

