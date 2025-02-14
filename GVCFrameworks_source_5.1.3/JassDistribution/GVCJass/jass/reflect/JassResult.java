/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class JassResult extends Entity {

    /**
     * Constructor declaration
     *
     *
     * @param type
     *
     * @see
     */
    public JassResult(Class type) {
	this.type = type;
	name = "jassResult";
	modifiers = Modifier.PRIVATE;
    }

    /**
     * Method declaration
     *
     *
     * @param other
     *
     * @return
     *
     * @see
     */
    public boolean equals(Object other) {
	boolean isEqual = false;

	if (other instanceof JassResult) {
	    JassResult otherResult = (JassResult) other;
	    String     thisName = this.type.getName();
	    String     otherName = otherResult.getType().getName();

	    if (thisName.equals(otherName)) {
		isEqual = true;
	    } 
	} 

	return isEqual;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

