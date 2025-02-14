/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class BooleanClass extends Class {

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public BooleanClass() {
	super("boolean");

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
	return true;
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
	return !isPrimitive();
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
	return false;
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
	if (c == ClassPool.Boolean) {
	    return true;
	} 

	return false;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

