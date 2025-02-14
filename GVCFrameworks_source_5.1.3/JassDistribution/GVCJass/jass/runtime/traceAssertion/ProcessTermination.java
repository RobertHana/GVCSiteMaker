/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ProcessTermination extends Action {

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public ProcessTermination() {}

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
	System.out.println("Prozess ist terminiert!");

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
	return lineIndent + "new ProcessTermination()";
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
	return "Termination";
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

