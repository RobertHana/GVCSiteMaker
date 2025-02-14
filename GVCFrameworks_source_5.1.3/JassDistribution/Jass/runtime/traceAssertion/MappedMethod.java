/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import jass.runtime.util.ToJava;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class MappedMethod {
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.MappedMethod";
    String		       methodname;
    String[]		       parameter;
    String		       code;
    Source		       source;

    /**
     * Constructor declaration
     *
     *
     * @param source
     * @param methodname
     * @param parameter
     * @param code
     *
     * @see
     */
    public MappedMethod(Source source, String methodname, String[] parameter, 
			String code) {
	this.source = source;
	this.methodname = methodname;
	this.parameter = parameter;
	this.code = code;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getCode() {
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
    public String[] getParameter() {
	return parameter;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Source getSource() {
	return source;
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

	javaCode += "new " + CLASSNAME + "(" + source.toJava() + ", \"" 
		    + methodname + "\"" + ", " 
		    + ToJava.stringArray(parameter) + ", \"" 
		    + ToJava.string(code) + "\")";

	return javaCode;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

