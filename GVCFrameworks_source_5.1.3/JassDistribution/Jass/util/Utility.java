/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.util;

import java.util.*;
import jass.reflect.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class Utility {
    public final static String DEFAULT_INDENT = "  ";

    /*
     * public static Vector objectArray2Vector(Object[] os) {
     * Vector v = new Vector(os.length);
     * for (int i = 0; i < os.length; i++) v.setElementAt(os[i],i);
     * return v;
     * }
     * 
     * public static Object[] vector2ObjectArray (Vector v) {
     * Object[] os = new Object[v.size()];
     * v.copyInto(os);
     * return os;
     * }
     * 
     * public static AssertionExpression[] vector2AssertionExpressionArray (Vector v) {
     * AssertionExpression[] os = new AssertionExpression[v.size()];
     * v.copyInto(os);
     * return os;
     * }
     */

    /**
     * Method declaration
     *
     *
     * @param s
     * @param strs
     *
     * @return
     *
     * @see
     */
    public static boolean contains(String s, String[] strs) {
	for (int i = 0; i < strs.length; i++) {
	    if (s.equals(strs[i])) {
		return true;
	    } 
	} 

	return false;
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     * @param v
     *
     * @see
     */
    public static void removeField(Field e, Vector v) {
	for (int i = 0; i < v.size(); i++) {
	    if (((Field) v.elementAt(i)).getIdString().equals(e.getIdString())) {
		v.removeElementAt(i);
	    } 
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     * @param v
     *
     * @see
     */
    public static void removeMethod(Method e, Vector v) {
	for (int i = 0; i < v.size(); i++) {
	    if (((Method) v.elementAt(i)).getIdString().equals(e.getIdString())) {
		v.removeElementAt(i);
	    } 
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param o
     * @param v
     *
     * @return
     *
     * @see
     */
    public static boolean containsObject(Object o, Vector v) {
	for (int i = 0; i < v.size(); i++) {
	    if (((Object) v.elementAt(i)).equals(o)) {
		return true;
	    } 
	} 

	return false;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

