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
public class WildcardParameter extends CommunicationParameter {
    int wildcardType;

    /**
     * Constructor declaration
     *
     *
     * @param wildcardType
     *
     * @see
     */
    public WildcardParameter(int wildcardType) {
	this.wildcardType = wildcardType;
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
	return true;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	switch (wildcardType) {

	case UNREFLECTEDTYPE_ARBITRARY: {
	    typename = ARBITRARY;

	    break;
	} 

	case UNREFLECTEDTYPE_ONE_ARBITRARY: {
	    typename = ONE_ARBITRARY;

	    break;
	} 

	case UNREFLECTEDTYPE_NULL: {
	    typename = NULL;
	} 
	}
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

