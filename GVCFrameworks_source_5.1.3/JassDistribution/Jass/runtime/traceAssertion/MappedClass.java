/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public abstract class MappedClass {
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.MappedClass";

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public abstract jass.runtime.traceAssertion.Process constructProcess();

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public abstract jass.runtime.traceAssertion.SetOfMethods getAlphabet();

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public abstract String getParentClassname();

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public abstract String[] getReferedProcesses();

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public abstract Source getSource();
}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

