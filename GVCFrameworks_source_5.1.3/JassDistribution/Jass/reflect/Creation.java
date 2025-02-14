/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class Creation {
    protected Class       type;
    protected Constructor c;

    /**
     * Constructor declaration
     *
     *
     * @param type
     * @param c
     *
     * @see
     */
    public Creation(Class type, Constructor c) {
	this.type = type;
	this.c = c;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Constructor getConstructor() {
	return c;
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
	return type.getName() + "<" + c.getIdString() + ">";
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Class getType() {
	return type;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

