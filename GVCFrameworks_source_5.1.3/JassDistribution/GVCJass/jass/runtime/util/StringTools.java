/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.util;

import java.io.InputStream;
import java.util.Vector;

/**
 * This class provides a collection of static methods for string operations.
 * <br>
 * Note that this class doesn't subclass <code>java.lang.String</code>.<br>
 * <br>
 * 
 * @author 	Michael Plath
 * @version 	0.9, 09/29/99
 */
public class StringTools extends Object {

    /**
     * Returns the index within this string of the first occurrence of one of the specified substrings.
     * @param str string
     * @param lookup substrings
     * @result if one of the lookup arguments occurs as a substring within this object, then the index of the first character of the first such substring is returned; if it does not occur as a substring, -1 is returned
     * @see java.lang.String#indexOf(java.lang.String)
     */
    public static int indexOf(String str, String[] lookup) {
	int position = -1;

	for (int i = 0; i < lookup.length && position == -1; ++i) {
	    position = str.indexOf(lookup[i]);
	} 

	return position;
    } 

    /**
     * Remove all tags.
     * All text between two text markers and the marker themselfes will be removed.
     * @param text text
     * @param markerStart this marks the beginning of a tag
     * @param markerEnd this marks the end of a tag
     * @return new text with removed tags
     */
    public static String removeTags(String text, String markerStart, 
				    String markerEnd) {
	int i_start;
	int i_end;

	// As long as a marker for a beginning tag is found the text is cut to the position
	// where the marker for the end tag is found. If no end tag is found than the entire
	// end of the text is truncated.
	while ((i_start = text.indexOf(markerStart)) != -1) {
	    i_end = text.indexOf(markerEnd, i_start + markerStart.length());

	    if (i_end == -1) {
		i_end = text.length();
	    } else {
		i_end += markerEnd.length();
	    } 

	    // cut
	    text = text.substring(0, i_start) 
		   + text.substring(i_end, text.length());
	} 

	return text;
    } 

    /**
     * Replace a substring within a string.
     * If there are more than one matches to the substring, all substrings will be replaced.
     * If no substring is found, the unchanged string is passed back.
     * @param s do the replacement here
     * @param oldString this string is going to be replaced
     * @param newString replace oldString with this new string
     * @return String after replacement. If substring was not found, return unchanged string s
     */
    public static String replaceSubstring(String s, String oldString, 
					  String newString) {
	int startIndex;
	int fromIndex = 0;

	while ((startIndex = s.indexOf(oldString, fromIndex)) != -1) {
	    s = (s.substring(0, startIndex) + newString 
		 + s.substring(startIndex + oldString.length(), s.length()));
	    fromIndex = startIndex + newString.length();
	} 

	return (s);
    } 

    /**
     * Tests if this string starts with one of the specified prefixes.
     * @param str string
     * @param prefix prefixes
     * @result true if one of the the character sequencen represented by the prefix-argument is a prefix of the character sequence represented by this string; false otherwise. Note also that true will be returned if one of the prefix- arguments is an empty string or is equal to this String object as determined by the equals(Object) method.
     */
    public static boolean startsWith(String str, String[] prefix) {
	boolean startsWith = false;

	for (int i = 0; i < prefix.length &&!startsWith; ++i) {
	    startsWith = str.startsWith(prefix[i]);
	} 

	return startsWith;
    } 

    /**
     * Convert a string to a vector.
     * @see #stringToVector(java.util.Vector, java.lang.String, java.lang.String)
     */
    public static Vector stringToVector(String s, String delemiter) {
	return stringToVector(new Vector(), s, delemiter);
    } 

    /**
     * Convert a string to a vector and add it to an existing vector object.
     * The string is cut into pieces of vector elements. By giving a delemiter string
     * you choose the places where the string should be cut. After conversion all
     * delemiters are removed.
     * @param vec vector converted string is added to
     * @param s string
     * @param delemiter delemiter
     * @result vector representation of string
     */
    public static Vector stringToVector(Vector vec, String s, 
					String delemiter) {
	int index = 0;
	int index_delemiter;

	while ((index_delemiter = s.indexOf(delemiter, index)) != -1) {
	    vec.addElement(new String(s.substring(index, index_delemiter)));

	    index = index_delemiter 
		    + delemiter.length();    // overread delemiter's chars
	} 

	if (index <= s.length()) {
	    vec.addElement(s.substring(index, s.length()));
	} 

	return (vec);
    } 

    /**
     * Method declaration
     *
     *
     * @param array
     *
     * @return
     *
     * @see
     */
    public static String toString(Object[] array) {
	if (array == null) {
	    return "null";
	} 

	String s = "";

	s += "[";

	for (int i = 0; i < array.length; ++i) {
	    Object arrayEntry = array[i];

	    if (arrayEntry == null) {
		s += "null";
	    } else if (arrayEntry.getClass().isArray()) {
		s += toString((Object[]) arrayEntry);
	    } else {
		s += arrayEntry.toString();
	    } 

	    if (i + 1 < array.length) {
		s += ", ";
	    } 
	} 

	s += "]";

	return s;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

