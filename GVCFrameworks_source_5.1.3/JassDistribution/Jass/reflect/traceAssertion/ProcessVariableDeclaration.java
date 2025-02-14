/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import jass.parser.JassLocalVariableDeclaration;
import jass.runtime.util.ToJava;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ProcessVariableDeclaration {
    JassLocalVariableDeclaration node;
    String			 javaCode;

    /**
     * Constructor declaration
     *
     *
     * @param node
     *
     * @see
     */
    public ProcessVariableDeclaration(JassLocalVariableDeclaration node) {
	this.node = node;
    }

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	javaCode = ToJava.node(node);
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
	return javaCode;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

