/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class JassOld extends Entity {

    /**
     * Constructor declaration
     *
     *
     * @param type
     *
     * @see
     */
    public JassOld(Class type) {
	this.type = type;
	name = "jassOld";
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

	if (other instanceof JassOld) {
	    JassOld otherOld = (JassOld) other;
	    String  thisName = this.type.getName();
	    String  otherName = otherOld.getType().getName();

	    if (thisName.equals(otherName)) {
		isEqual = true;
	    } 
	} 

	return isEqual;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

