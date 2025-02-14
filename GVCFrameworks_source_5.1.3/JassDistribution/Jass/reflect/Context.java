/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

/**
 * Interface declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public interface Context {

    /**
     * Get a field of this context by it's name
     *
     * @param name the name of the field
     *
     * @return a reference to the Field or null if not found.
     */
    public Field getField(String name);

    /**
     * Get a field of this context by an equal field
     *
     * @param name a field, that is equal to the one that we are
     *             looking for
     *
     * @return a reference to the Field or null if not found.  
     */
    public Field getField(Field field);

    /**
     * Get a method of this context by it's name and the types of the
     * formal parameters.
     *
     * @param name the method's name
     * @param params the types of the formal parameters
     *
     * @return a reference to the Method or null if not found.  */
    public Method getMethod(String name, Class[] params);

    /**
     * Method declaration
     *
     *
     * @param name
     *
     * @return
     *
     * @see
     */
    public FormalParameter getFormalParameter(String name);

    /**
     * Return the name of this compilation unit.
     */
    public String getUnitName();

    /**
     * Get this context's method name 
     *
     * @return the name, if this context is a Method, or null
     *         otherwise.
     */
    public String getMethodName();

    /**
     * Returns an array of Field objects reflecting all the fields
     * declared by this context.
     */
    public Field[] getDeclaredFields();
}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

