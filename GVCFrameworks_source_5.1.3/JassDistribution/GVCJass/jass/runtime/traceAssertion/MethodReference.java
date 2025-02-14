/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class MethodReference {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    public static final String  CLASSNAME = 
	"jass.runtime.traceAssertion.MethodReference";
    public static final boolean BEGIN = true;
    String			packagename;
    String			classname;
    String			methodname;
    boolean			beginOfMethod;

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
     *
     * @see
     */
    public MethodReference(String packagename, String classname, 
			   String methodname, boolean beginOfMethod) {
	this.packagename = packagename;
	this.classname = classname;
	this.methodname = methodname;
	this.beginOfMethod = beginOfMethod;
    }

    /**
     * Method declaration
     *
     *
     * @param packagename
     * @param classname
     * @param methodname
     * @param beginOfMethod
     *
     * @return
     *
     * @see
     */
    public static String constructorString(String packagename, 
					   String classname, 
					   String methodname, 
					   boolean beginOfMethod) {
	String string = "";

	string += "new " + CLASSNAME + "(" + "\"" + packagename + "\"" 
		  + ", \"" + classname + "\"" + ", \"" + methodname + "\"";
	string += ", " + (beginOfMethod ? "true" : "false") + "" + ")";

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
     * @return
     *
     * @see
     */
    public Communication asCommunication() {
	Communication communication = new Communication(packagename, 
		classname, methodname, beginOfMethod, null, null);

	return communication;
    } 

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
	if (!(other instanceof MethodReference)) {
	    return false;
	} 

	MethodReference otherReference = (MethodReference) other;

	return hashString().equals(otherReference.hashString());
    } 

    // public String getName(){return name;}

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
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String hashString() {
	return packagename + "." + classname + "." + methodname + "_" 
	       + (beginOfMethod ? "b" : "e");
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isBeginOfMethod() {
	return beginOfMethod;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isConstructorInvocation() {
	return classname.equals(methodname.substring(0, 
		methodname.indexOf('(')));

	// #TODO# parameter, dann: return classname.equals(methodname);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getPackagename() {
	return packagename;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getClassname() {
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
    public String getMethodname() {
	return methodname;
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
	return constructorString(packagename, classname, methodname, 
				 beginOfMethod);
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

