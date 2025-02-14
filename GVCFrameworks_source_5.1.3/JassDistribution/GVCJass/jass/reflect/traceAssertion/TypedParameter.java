/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import jass.reflect.Class;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class TypedParameter extends CommunicationParameter {

    /**
     * Constructor declaration
     *
     *
     * @param type
     *
     * @see
     */
    public TypedParameter(Class type) {
	this.unreflectedType = UNREFLECTEDTYPE_TYPED;
	this.type = type;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isNull() {
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
    public boolean isWildcard() {
	return false;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	type.reflect();

	typename = type.getName();
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

