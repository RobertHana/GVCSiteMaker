/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ProcessCall extends Action {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.ProcessCall";
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
    public ProcessCall(String mappedMethodname) {
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
	ProcessContext  processContext = parentProcess.getContext();
	ProcessObserver processes = parentProcess.getProcessObserver();
	Process[]       newProcessArray = 
	    processes.newInstances(parentProcess, mappedMethodname);
	Communicator    communicator = processContext.change(parentProcess, 
		newProcessArray);

	communicator.communicate(parentProcess.getActualTraceStack(), 
				 parentProcess.getActualReference(), 
				 parentProcess.getActualParameters());

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
	String s = "";

	s += lineIndent + "new " + CLASSNAME + "(\"" + mappedMethodname 
	     + "\")";

	return s;
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
	String s = "";

	s += "Call " + mappedMethodname;

	return s;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

