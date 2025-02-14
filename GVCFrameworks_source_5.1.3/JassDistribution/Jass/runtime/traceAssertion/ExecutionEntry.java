/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ExecutionEntry {
    public final static String CLASSNAME = 
	"jass.runtime.traceAssertion.ExecutionEntry";
    String		       callerId;
    MethodReference	       methodReference;

    /**
     * Constructor declaration
     *
     *
     * @param caller
     * @param methodReference
     *
     * @see
     */
    public ExecutionEntry(Object caller, MethodReference methodReference) {
	this.callerId = caller.getClass().getName() + "@" + caller.hashCode();
	this.methodReference = methodReference;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getCallerId() {
	return callerId;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public MethodReference getReference() {
	return methodReference;
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
	String string = "";

	string += callerId + "(" + methodReference + ")";

	return string;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

