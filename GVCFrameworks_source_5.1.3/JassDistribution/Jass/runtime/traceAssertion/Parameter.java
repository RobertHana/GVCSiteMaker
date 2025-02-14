/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class Parameter {

    /**
     * Class declaration
     *
     *
     * @author
     * @version %I%, %G%
     */
    public class NullObject {

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
	    return other instanceof NullObject ? true : false;
	} 

    }

    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.Parameter";
    boolean		       hasPrimitiveType = true;
    Object		       value;
    Class		       valueClass;

    /**
     * Constructor declaration
     *
     *
     * @param value
     *
     * @see
     */
    public Parameter(boolean value) {
	this.value = new Boolean(value);
	valueClass = Boolean.TYPE;
    }

    /**
     * Constructor declaration
     *
     *
     * @param value
     *
     * @see
     */
    public Parameter(char value) {
	this.value = new Character(value);
	valueClass = Character.TYPE;
    }

    /**
     * Constructor declaration
     *
     *
     * @param value
     *
     * @see
     */
    public Parameter(byte value) {
	this.value = new Byte(value);
	valueClass = Byte.TYPE;
    }

    /**
     * Constructor declaration
     *
     *
     * @param value
     *
     * @see
     */
    public Parameter(short value) {
	this.value = new Short(value);
	valueClass = Short.TYPE;
    }

    /**
     * Constructor declaration
     *
     *
     * @param value
     *
     * @see
     */
    public Parameter(int value) {
	this.value = new Integer(value);
	valueClass = Integer.TYPE;
    }

    /**
     * Constructor declaration
     *
     *
     * @param value
     *
     * @see
     */
    public Parameter(long value) {
	this.value = new Long(value);
	valueClass = Long.TYPE;
    }

    /**
     * Constructor declaration
     *
     *
     * @param value
     *
     * @see
     */
    public Parameter(float value) {
	this.value = new Float(value);
	valueClass = Float.TYPE;
    }

    /**
     * Constructor declaration
     *
     *
     * @param value
     *
     * @see
     */
    public Parameter(double value) {
	this.value = new Double(value);
	valueClass = Double.TYPE;
    }

    /**
     * Constructor declaration
     *
     *
     * @param value
     *
     * @see
     */
    public Parameter(Object value) {
	if (value == null) {
	    this.value = new NullObject();
	} else {
	    this.value = value;
	} 

	valueClass = this.value.getClass();
	hasPrimitiveType = false;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Object getValue() {
	return value;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Class getValueClass() {
	return valueClass;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean hasPrimitiveType() {
	return hasPrimitiveType;
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
	return value.toString();
    } 

    /**
     * Method declaration
     *
     *
     * @param parameters
     *
     * @return
     *
     * @see
     */
    public static String toString(Parameter[] parameters) {
	String string = "";

	for (int iParameter = 0; iParameter < parameters.length; 
		++iParameter) {
	    Parameter parameter = parameters[iParameter];

	    string += parameter.toString();

	    if (iParameter + 1 < parameters.length) {
		string += ", ";
	    } 
	} 

	return string;
    } 

    /*
     * public String toJava()
     * {
     * String javaCode = "";
     * javaCode += "new " + CLASSNAME	+ "(";
     * javaCode += "new " + value.getClass().getName() + "
     * return javaCode;
     * }
     */

    /*
     * public static String toJava(Parameter[] parameters)
     * {
     * String javaCode = "";
     * 
     * return javaCode;
     * }
     */
}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

