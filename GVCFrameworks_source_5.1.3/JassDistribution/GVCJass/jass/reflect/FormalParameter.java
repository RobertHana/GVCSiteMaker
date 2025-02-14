/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class FormalParameter extends Entity {

    /**
     * Constructor declaration
     *
     *
     * @param name
     *
     * @see
     */
    public FormalParameter(String name) {
	super(name);
    }

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public FormalParameter() {}

    /**
     * Method declaration
     *
     *
     * @param c
     *
     * @see
     */
    public void init(java.lang.Class c) {
	type = ClassPool.getClass(c.getName());
	name = null;
	modifiers = Modifier.convert(c.getModifiers());
    } 

    /**
     * Method declaration
     *
     *
     * @param b
     *
     * @see
     */
    public void setFinal(boolean b) {
	if (b) {
	    modifiers = jass.reflect.Modifier.FINAL;
	} else {
	    modifiers = jass.reflect.Modifier.NOTHING;
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
    public String toJava() {
	return toJava(name);
    } 

    /**
     * Method declaration
     *
     *
     * @param fieldname
     *
     * @return
     *
     * @see
     */
    public String toJava(String fieldname) {
	String javaCode = "";

	javaCode += getType().toJava();
	javaCode += " ";
	javaCode += fieldname;

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
	return "[FormalParameter] " + type.getName() + " " + name;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

