/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import jass.runtime.util.ToString;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class CodeExecution extends Action {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.CodeExecution";
    String		       mappedMethodname;

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    /**
     * Constructor declaration
     *
     *
     * @param mappedMethodname
     *
     * @see
     */
    public CodeExecution(String mappedMethodname) {
	this.mappedMethodname = mappedMethodname;
    }

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */

    // interface Action

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
    public Object perform(Process parentProcess) {
	MappedClass mappedClass = parentProcess.getMappedClass();

	try {
	    Method method = 
		mappedClass.getClass().getDeclaredMethod(mappedMethodname, 
		    new Class[0]);

	    // System.out.println(method);
	    Object object = method.invoke(mappedClass, new Object[0]);

	    // System.out.println(object);
	} catch (InvocationTargetException e) {
	    ProcessObserver processObserver = 
		parentProcess.getProcessObserver();

	    throw new TraceAssertionException(processObserver, 
					      mappedClass.getSource(), 
					      e.getTargetException().toString() 
					      + ToString.NEWLINE + "  at " 
					      + mappedMethodname, processObserver.getTraceAssertion().getTraceStack());
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	} 

	return null;
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
	return lineIndent + "new " + CLASSNAME + "(\"" + mappedMethodname 
	       + "\")";
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
	return "{" + mappedMethodname + "}";
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

