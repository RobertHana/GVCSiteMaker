/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import jass.reflect.Class;
import jass.reflect.*;
import jass.runtime.util.ToJava;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ProcessDeclarator implements FileBounded {
    Class       container;
    int		lineNr;
    Constructor mappedConstructor;

    /**
     * Constructor declaration
     *
     *
     * @param name
     *
     * @see
     */
    public ProcessDeclarator(String name) {
	mappedConstructor = new Constructor();

	mappedConstructor.setName(name);
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Class getContainer() {
	return container;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public int getLine() {
	return lineNr;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Constructor getMappedConstructor() {
	return mappedConstructor;
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
	return mappedConstructor.getName();
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {

	// System.out.println("REFLECT!" + mappedConstructor.getName());
	mappedConstructor.reflect();
    } 

    /**
     * Method declaration
     *
     *
     * @param container
     * @param line
     *
     * @see
     */
    public void setFileBounds(Class container, int line) {
	this.container = container;
	this.lineNr = line;
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
	String initializer = "";

	javaCode += 
	    ToJava.INDENT1 + "public " 
	    + ((Class) mappedConstructor.getContainer()).getIdentifier();
	javaCode += "(";

	FormalParameter[] parameters = 
	    mappedConstructor.getFormalParameters();
	int		  numberOfParameters = parameters.length;

	if (numberOfParameters > 0) {
	    for (int iParameter = 0; iParameter < numberOfParameters; 
		    ++iParameter) {
		FormalParameter parameter = parameters[iParameter];

		initializer += ToJava.INDENT2 + "this." + parameter.getName() 
			       + " = " + parameter.getName() + ";" 
			       + ToJava.NEWLINE;
		javaCode += parameter.toJava();

		if (iParameter + 1 < numberOfParameters) {
		    javaCode += ", ";
		} 
	    } 
	} 

	javaCode += ")" + ToJava.NEWLINE;
	javaCode += ToJava.INDENT1 + "{" + ToJava.NEWLINE;
	javaCode += initializer;
	javaCode += ToJava.INDENT1 + "}" + ToJava.NEWLINE;

	return javaCode;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

