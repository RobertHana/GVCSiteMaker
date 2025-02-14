/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class VoidClass extends Class {

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public VoidClass() {
	super("void");

	unreflected = false;
	modifiers = Modifier.PUBLIC;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public final boolean isNumeric() {
	return false;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public final boolean isIntegral() {
	return false;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public final boolean isBoolean() {
	return false;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public final boolean isPrimitive() {
	return isNumeric() || isIntegral() || isBoolean();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public final boolean isReference() {
	return false;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public final boolean isVoid() {
	return true;
    } 

    /**
     * Method declaration
     *
     *
     * @param c
     *
     * @return
     *
     * @see
     */
    public boolean isAssignableFrom(Class c) {
	return false;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

