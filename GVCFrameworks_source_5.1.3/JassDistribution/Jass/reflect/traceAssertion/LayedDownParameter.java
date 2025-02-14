/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import jass.reflect.Class;
import jass.reflect.ClassPool;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class LayedDownParameter extends ExchangeParameter {
    String value;

    /**
     * Constructor declaration
     *
     *
     * @param unreflectedType
     *
     * @see
     */
    public LayedDownParameter(int unreflectedType) {
	super(unreflectedType, null);
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isInputParameter() {
	return false;
    } 

    // public boolean isNull(){return false;}
    // public boolean isWildcard(){return false;}

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getName() {
	return value;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	switch (unreflectedType) {

	case UNREFLECTEDTYPE_CHAR: {
	    typename = "char";

	    break;
	} 

	case UNREFLECTEDTYPE_STRING: {
	    typename = "java.lang.String";

	    break;
	} 

	case UNREFLECTEDTYPE_INTEGER: {
	    char lastChar = value.charAt(value.length() - 1);

	    if (lastChar == 'l' || lastChar == 'L') {
		typename = "long";
	    } else {
		typename = "int";
	    } 

	    break;
	} 

	case UNREFLECTEDTYPE_FLOATING: {
	    char lastChar = value.charAt(value.length() - 1);

	    if (lastChar == 'd' || lastChar == 'D') {
		typename = "double";
	    } else {
		typename = "float";
	    } 

	    break;
	} 
	}

	this.type = ClassPool.getClass(typename);
    } 

    /**
     * Method declaration
     *
     *
     * @param value
     *
     * @see
     */
    public void setValue(String value) {
	this.value = value;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

