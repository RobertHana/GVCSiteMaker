/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.*;
import jass.runtime.util.ToJava;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class Field extends Entity {
    String initializerString;

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public Field() {}

    /**
     * Constructor declaration
     *
     *
     * @param variableDeclarator
     *
     * @see
     */
    public Field(VariableDeclarator variableDeclarator) {
	super(variableDeclarator.getId());

	this.initializerString = variableDeclarator.getInitializerString();
    }

    /**
     * Method declaration
     *
     *
     * @param f
     *
     * @see
     */
    public void init(java.lang.reflect.Field f) {
	name = f.getName();
	modifiers = Modifier.convert(f.getModifiers());
	type = ClassPool.getClass(f.getType().getName());
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getInitializerString() {
	return initializerString;
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

	javaCode += Modifier.toJava(getModifier());

	boolean hasModifier = javaCode.length() > 0;

	if (hasModifier) {
	    javaCode += ToJava.SEPARATOR;
	} 

	javaCode += type.toJava();
	javaCode += ToJava.SEPARATOR + name;

	if (initializerString != null) {
	    javaCode += " =" + initializerString;
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
	return "[Field] " + type.getName() + " " + name;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

