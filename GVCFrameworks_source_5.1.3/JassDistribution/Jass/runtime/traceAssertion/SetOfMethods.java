/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class SetOfMethods {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.SetOfMethods";
    public static final String NEWLINE = System.getProperty("line.separator");
    Hashtable		       set;

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
    public SetOfMethods() {
	init();
    }

    /**
     * Constructor declaration
     *
     *
     * @param method
     *
     * @see
     */
    public SetOfMethods(MethodReference method) {
	init();
	add(method);
    }

    /**
     * Constructor declaration
     *
     *
     * @param communications
     *
     * @see
     */
    public SetOfMethods(MethodReference[] communications) {
	init();
	add(communications);
    }

    /**
     * Constructor declaration
     *
     *
     * @param setOfMethodReferences
     *
     * @see
     */
    public SetOfMethods(SetOfMethods setOfMethodReferences) {
	init();
	add(setOfMethodReferences);
    }

    /**
     * Constructor declaration
     *
     *
     * @param setOfMethodReferences
     *
     * @see
     */
    public SetOfMethods(SetOfMethods[] setOfMethodReferences) {
	init();
	add(setOfMethodReferences);
    }

    /**
     * ********************************************************************
     * Private Methods                                                    *
     * *******************************************************************
     */
    private void init() {
	set = new Hashtable();
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
    public void add(MethodReference c) {
	set.put(c.hashString(), c);
    } 

    /**
     * Method declaration
     *
     *
     * @param method
     *
     * @see
     */
    public void add(MethodReference[] method) {
	for (int i = 0; i < method.length; ++i) {
	    MethodReference c = method[i];

	    set.put(c.hashString(), c);
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
    public void add(SetOfMethods communications) {
	add(communications.getMethodReferences());
    } 

    /**
     * Method declaration
     *
     *
     * @param setOfMethodReferences
     *
     * @see
     */
    public void add(SetOfMethods[] setOfMethodReferences) {
	for (int i = 0; i < setOfMethodReferences.length; ++i) {
	    add(setOfMethodReferences[i]);
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
    public MethodReference anyElement() {
	MethodReference   arbitraryMethodReference;
	MethodReference[] allMethodReferences = getMethodReferences();

	arbitraryMethodReference = (allMethodReferences.length == 0 ? null 
				    : allMethodReferences[0]);

	return arbitraryMethodReference;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public SetOfCommunications asSetOfCommunications() {
	SetOfCommunications set = new SetOfCommunications();
	MethodReference[]   methods = getMethodReferences();

	for (int iMethod = 0; iMethod < methods.length; ++iMethod) {
	    MethodReference method = methods[iMethod];

	    set.add(method.asCommunication());
	} 

	return set;
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
    public boolean contains(MethodReference c) {
	boolean contains = set.containsKey(c.hashString());

	return contains;
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
    public MethodReference get(Communication communication) {
	MethodReference method = null;

	method = (MethodReference) set.get(communication.hashString());

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
    public MethodReference[] getMethodReferences() {
	MethodReference[] methodArray = new MethodReference[set.size()];

	set.values().toArray(methodArray);

	return methodArray;
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
     * @param set2
     *
     * @see
     */
    public void section(SetOfMethods set2) {
	MethodReference[] set1Array = getMethodReferences();
	MethodReference[] set2Array = set2.getMethodReferences();

	for (int i = 0; i < set1Array.length; ++i) {
	    MethodReference method = set1Array[i];

	    if (!set2.contains(method)) {
		set.remove(method.hashString());
	    } 
	} 

	for (int i = 0; i < set2Array.length; ++i) {
	    MethodReference method = set2Array[i];

	    if (!contains(method)) {
		set.remove(method.hashString());
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
    public SetOfMethods without(SetOfMethods exceptions) {
	MethodReference[] method = exceptions.getMethodReferences();

	for (int i = 0; i < method.length; ++i) {

	    // It doesn't matter whether 'c' exists or not
	    set.remove(method[i]);
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
	String code = "";

	code += lineIndent + "new " + CLASSNAME + NEWLINE;
	code += lineIndent + "(new " + MethodReference.CLASSNAME + "[]{" 
		+ NEWLINE;

	MethodReference[] methodList = getMethodReferences();

	for (int i = 0; i < methodList.length; ++i) {
	    code += methodList[i].toJava();

	    if (i + 1 < methodList.length) {
		code += ", ";
	    } 

	    code += NEWLINE;
	} 

	code += lineIndent + "})";

	return code;
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
	String		  s = "";
	MethodReference[] method = getMethodReferences();

	for (int i = 0; i < method.length; ++i) {
	    s += method[i].toString();

	    if (i + 1 < method.length) {
		s += ",";
	    } 
	} 

	return s;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

