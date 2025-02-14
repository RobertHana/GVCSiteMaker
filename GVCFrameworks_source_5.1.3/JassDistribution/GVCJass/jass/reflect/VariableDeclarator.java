/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class VariableDeclarator {
    String id;
    String initializerString;

    /**
     * Constructor declaration
     *
     *
     * @param id
     * @param initializerString
     *
     * @see
     */
    public VariableDeclarator(String id, String initializerString) {
	this.id = id;
	this.initializerString = initializerString;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getId() {
	return id;
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

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

