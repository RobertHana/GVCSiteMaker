/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class FiniteIntegerEnumeration implements FiniteEnumeration {
    protected int actualNumber;
    protected int end;

    /**
     * Constructor declaration
     *
     *
     * @param begin
     * @param end
     *
     * @see
     */
    public FiniteIntegerEnumeration(int begin, int end) {
	actualNumber = begin;
	this.end = end;
    }

    /**
     * @return if the enumeration contains more elements
     */
    public boolean hasMoreElements() {
	return actualNumber <= end;

	/**
	 * ensure changeonly{}
	 */
    } 

    /**
     * Call this method to get the next integer object back.
     * @return an integer object (not the primitive data type int (!)). The integer object can be converted with intValue()
     * in a primitive int type.
     * @throws java.util.NoSuchElementException if all elements have been processed.
     */
    public Object nextElement() throws java.util.NoSuchElementException {

	// for compatibility no precondition (hasMoreElements)
	if (hasMoreElements()) {
	    return new Integer(actualNumber++);
	} else {
	    throw new java.util.NoSuchElementException("All elements have been processed ! [" 
						       + actualNumber + ">" 
						       + end + "]");
	}

	/**
	 * ensure changeonly{actualNumber}
	 */
    } 

    /**
     * @return who many objects have not been processed yet
     */
    public int elementsLeft() {
	return end - actualNumber;

	/**
	 * ensure changeonly{}
	 */
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

