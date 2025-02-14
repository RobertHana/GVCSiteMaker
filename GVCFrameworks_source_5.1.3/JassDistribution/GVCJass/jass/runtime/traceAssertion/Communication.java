/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import java.lang.reflect.Method;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class Communication extends MethodReference {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.Communication";
    MappedMethod	       mappedMethodBefore;
    MappedMethod	       mappedMethodAfter;

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    /**
     * Constructor declaration
     *
     *
     * @param packagename
     * @param classname
     * @param methodname
     * @param beginOfMethod
     * @param mappedMethodBefore
     * @param mappedMethodAfter
     *
     * @see
     */
    public Communication(String packagename, String classname, 
			 String methodname, boolean beginOfMethod, 
			 MappedMethod mappedMethodBefore, 
			 MappedMethod mappedMethodAfter) {
	super(packagename, classname, methodname, beginOfMethod);

	this.mappedMethodBefore = mappedMethodBefore;
	this.mappedMethodAfter = mappedMethodAfter;
    }

    /**
     * Method declaration
     *
     *
     * @param packagename
     * @param classname
     * @param methodname
     * @param beginOfMethod
     * @param mappedMethodBefore
     * @param mappedMethodAfter
     *
     * @return
     *
     * @see
     */
    public static String constructorString(String packagename, 
					   String classname, 
					   String methodname, 
					   boolean beginOfMethod, 
					   MappedMethod mappedMethodBefore, 
					   MappedMethod mappedMethodAfter) {
	String string = "";

	string += "new " + CLASSNAME + "(" + "\"" + packagename + "\"" 
		  + ", \"" + classname + "\"" + ", \"" + methodname + "\"";
	string += 
	    ", " + (beginOfMethod ? "true" : "false") + "" + ", " 
	    + (mappedMethodBefore != null ? mappedMethodBefore.toJava() : "null") 
	    + ", " 
	    + (mappedMethodAfter != null ? mappedMethodAfter.toJava() : "null") 
	    + ")";

	return string;
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
     * @param other
     *
     * @return
     *
     * @see
     */
    public boolean equals(Object other) {
	if (!(other instanceof Communication)) {
	    return false;
	} 

	Communication otherCommunication = (Communication) other;

	return hashString().equals(otherCommunication.hashString());
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public MappedMethod getMappedMethodBefore() {
	return mappedMethodBefore;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public MappedMethod getMappedMethodAfter() {
	return mappedMethodAfter;
    } 

    /**
     * Method declaration
     *
     *
     * @param method
     *
     * @see
     */
    public void setMappedMethodBefore(MappedMethod method) {
	this.mappedMethodBefore = method;
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
     * @param lineIndent
     *
     * @return
     *
     * @see
     */
    public String toJava(String lineIndent) {
	return lineIndent 
	       + constructorString(packagename, classname, methodname, 
				   beginOfMethod, mappedMethodBefore, 
				   mappedMethodAfter);
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
	return hashString();
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

