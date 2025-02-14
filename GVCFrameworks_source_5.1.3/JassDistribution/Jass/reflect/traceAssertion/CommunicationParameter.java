/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import jass.reflect.Class;    // to avoid conflicts with java.lang.class
import jass.reflect.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public abstract class CommunicationParameter {
    public final static String ARBITRARY = "*";
    public final static String ONE_ARBITRARY = "?";
    public final static String NULL = "null";
    public final static int    UNREFLECTEDTYPE_FLOATING = 0;
    public final static int    UNREFLECTEDTYPE_INTEGER = 1;
    public final static int    UNREFLECTEDTYPE_NULL = 2;
    public final static int    UNREFLECTEDTYPE_CHAR = 3;
    public final static int    UNREFLECTEDTYPE_STRING = 4;
    public final static int    UNREFLECTEDTYPE_TYPED = 5;
    public final static int    UNREFLECTEDTYPE_INPUT = 6;
    public final static int    UNREFLECTEDTYPE_OUTPUT = 7;
    public final static int    UNREFLECTEDTYPE_ARBITRARY = 8;
    public final static int    UNREFLECTEDTYPE_ONE_ARBITRARY = 9;
    int			       unreflectedType;
    String		       typename;
    Class		       type;

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public abstract boolean isNull();

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public abstract boolean isWildcard();

    /**
     * Method declaration
     *
     *
     * @see
     */
    public abstract void reflect();

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getTypename() {
	return typename;
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

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String toString() {
	String string = "";

	string += typename;

	return string;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

