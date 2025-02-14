/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class Tool {

    /**
     * arrayEquals will return true, if the two arrays have same length 
     * and do contain the same data. This version is for Object arrays
     * and compares the data using the <code>referenceEquals</code> method.
     * There are other version for scalar values.
     *
     * @param a  the first array
     * @param b  the second array, that the first has to be compared to.
     *
     * @return true if both arrays have equal length and equal content.
     *
     * @see referenceEquals(Object, Object)
     */
    public static final synchronized boolean arrayEquals(Object[] a, 
	    Object[] b) {
	if (a.length == b.length) {
	    for (int i = 0; i < a.length; i++) {
		if (!(referenceEquals(a[i],b[i]))) {
		    return false;
		} 
	    } 
	    return true;
	} else {
	    return false;
	}
    } 

    /**
     * arrayEquals will return true, if the two arrays have same length 
     * and do contain the same data. This version is for int arrays.
     * There are other version for other scalar values and one for objects.
     *
     * @param a  the first array
     * @param b  the second array, that the first has to be compared to.
     *
     * @return true if both arrays have equal length and equal content.
     */
    public static final synchronized boolean arrayEquals(int[] a, int[] b) {
	if (a.length == b.length) {
	    for (int i = 0; i < a.length; i++) {
		if (a[i] != b[i]) {
		    return false;
		} 
	    } 

	    return true;
	} else {
	    return false;
	}
    } 

    /**
     * arrayEquals will return true, if the two arrays have same length 
     * and do contain the same data. This version is for boolean arrays.
     * There are other version for other scalar values and one for objects.
     *
     * @param a  the first array
     * @param b  the second array, that the first has to be compared to.
     *
     * @return true if both arrays have equal length and equal content.
     */
    public static final synchronized boolean arrayEquals(boolean[] a, 
	    boolean[] b) {
	if (a.length == b.length) {
	    for (int i = 0; i < a.length; i++) {
		if (a[i] != b[i]) {
		    return false;
		} 
	    } 

	    return true;
	} else {
	    return false;
	}
    } 

    /**
     * check if two references refer to equal objects. To refer to
     * equal objects, both references must be <code>null</code> or must
     * refer to objects, that are equal with respect to the 
     * <code>equals</code> method of the first parameter.
     *
     * @param a  the first object reference. The equals method of this 
     *          object is used (if not <code>null</code>
     * @param b  the second object reference.
     *
     * @return true, if both references are equals.
     *
     */
    public static final synchronized boolean referenceEquals(Object a, 
	    Object b) {
	if (a == null || b == null) {
	    return a == b;
	} else {
	    return a.equals(b);
	}
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

