/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import jass.reflect.SimpleNameExpression;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class AssertionLabel {
    public static final String METHODNAME = "jassCheck";
    static int		       nextIdNumber = 0;

    // Context context;
    int			       idNumber;

    // Method method;
    String		       name;

    // SimpleNameExpression[] parameters;

    /**
     * Constructor declaration
     *
     *
     * @param name
     *
     * @see
     */
    public AssertionLabel(String name) {
	this.name = name;

	// parameters = new SimpleNameExpression[]{};
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
	return name;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getName() {
	return name;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

