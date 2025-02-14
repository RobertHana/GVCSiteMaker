/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import jass.parser.Node;
import jass.parser.Token;
import jass.reflect.Class;     // to avoid conflicts with java.lang.Class
import jass.reflect.Method;    // to avoid conflicts with java.lang.Method
import jass.reflect.*;
import jass.runtime.util.ToJava;
import jass.runtime.traceAssertion.CodeExecution;
import jass.runtime.traceAssertion.ProcessFactory;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ProcessJavaBlock extends ProcessExpression 
    implements CodeProducer {
    public static final String METHOD_IDENTIFIER = "javaBlock";
    Node		       node;

    // IMPORTANT
    // column number may be wrong if tab spaces were used
    int			       columnNumber;
    String		       javaCode;
    Method		       mappedMethod;

    /**
     * Constructor declaration
     *
     *
     * @param node
     * @param lineNumber
     * @param columnNumber
     *
     * @see
     */
    public ProcessJavaBlock(Node node, int lineNumber, int columnNumber) {
	this.node = node;
	this.columnNumber = columnNumber;
	mappedMethod = new Method();

	mappedMethod.setName(METHOD_IDENTIFIER + columnNumber + "_" 
			     + lineNumber);
    }

    /**
     * Method declaration
     *
     *
     * @param automaton
     *
     * @return
     *
     * @see
     */
    public ProcessFactory constructAutomaton(ProcessFactory automaton) {
	CodeExecution action = new CodeExecution(getMappedMethod().getName());

	automaton.addPrefixJava(action);

	return automaton;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Method getMappedMethod() {
	return mappedMethod;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	javaCode = "";
	javaCode += ToJava.INDENT1 + "public void " + mappedMethod.getName() 
		    + "()" + ToJava.NEWLINE;
	javaCode += ToJava.INDENT1 + "{";
	javaCode += ToJava.node(node);
	javaCode += ToJava.INDENT1 + "}";
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

