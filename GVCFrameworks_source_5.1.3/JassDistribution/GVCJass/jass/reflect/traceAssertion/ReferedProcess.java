/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import jass.reflect.Class;
import jass.reflect.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ReferedProcess {
    String	       name;
    ArgumentExpression expression;
    FormalParameter[]  parameters;

    /**
     * Constructor declaration
     *
     *
     * @param name
     * @param expression
     *
     * @see
     */
    public ReferedProcess(String name, ArgumentExpression expression) {
	this.name = name;
	this.expression = expression;
    }

    /**
     * Constructor declaration
     *
     *
     * @param name
     * @param parameters
     *
     * @see
     */
    public ReferedProcess(String name, FormalParameter[] parameters) {
	this.name = name;
	this.parameters = parameters;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getArgumentsAsJavaCode() {
	String javaCode = null;

	if (expression != null) {
	    javaCode = expression.toJava();
	} else if (parameters != null) {
	    javaCode = "";
	    javaCode += "(";

	    for (int iParameter = 0; iParameter < parameters.length; 
		    ++iParameter) {
		FormalParameter parameter = parameters[iParameter];

		javaCode += parameter.toJava();

		if (iParameter + 1 < parameters.length) {
		    javaCode += ",";
		} 
	    } 

	    javaCode += ")";
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
    public String[] getArgumentsAsStringArray() {
	String[] array = null;

	if (expression != null) {
	    array = expression.asStringArray();
	} else if (parameters != null) {
	    array = new String[parameters.length];

	    for (int iParameter = 0; iParameter < array.length; 
		    ++iParameter) {
		FormalParameter parameter = parameters[iParameter];

		array[iParameter] = parameter.toString();
	    } 
	} 

	return array;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getName() {
	return name;
    } 

    /**
     * Method declaration
     *
     *
     * @param context
     *
     * @see
     */
    public void reflect(Class context) {
	if (expression != null) {
	    expression.reflectExpression(context, new DependencyCollector());
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
    public String toString() {
	String   string = "";
	String[] array = getArgumentsAsStringArray();

	for (int iParameter = 0; iParameter < array.length; ++iParameter) {
	    String parameter = array[iParameter];

	    string += parameter;

	    if (iParameter + 1 < array.length) {
		string += ",";
	    } 
	} 

	return string;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

