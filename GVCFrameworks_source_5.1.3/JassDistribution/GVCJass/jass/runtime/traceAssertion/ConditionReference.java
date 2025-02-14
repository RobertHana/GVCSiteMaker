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
public class ConditionReference extends Condition {
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.ConditionReference";
    MappedMethod	       mappedMethod;

    /**
     * Constructor declaration
     *
     *
     * @param method
     *
     * @see
     */
    public ConditionReference(MappedMethod method) {
	this.mappedMethod = method;
    }

    /**
     * Method declaration
     *
     *
     * @param parentProcess
     *
     * @return
     *
     * @see
     */
    public boolean evaluate(Process parentProcess) {
	MappedClass mappedClass = parentProcess.getMappedClass();
	boolean     evaluation = false;

	try {

	    // System.out.println(mappedClass);
	    Method method = 
		mappedClass.getClass().getDeclaredMethod(mappedMethod.getMethodname(), 
		    new Class[0]);

	    // System.out.println(method);
	    Object object = method.invoke(mappedClass, new Object[0]);

	    // System.out.println(object);
	    evaluation = ((Boolean) object).booleanValue();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	} 

	return evaluation;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public MappedMethod getMappedMethod() {
	return mappedMethod;
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
	String javaCode = "";

	javaCode += lineIndent + "new " + CLASSNAME + "(" 
		    + mappedMethod.toJava() + ")";

	return javaCode;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

