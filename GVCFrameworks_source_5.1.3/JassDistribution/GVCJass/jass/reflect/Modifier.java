/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.util.*;

/**
 * Collector for modifiers. The Modifier class provides static methods
 * and constants to decode class and member access modifiers.
 *
 * @author Detlef Bartetzko
 * @version $Revision: 1$ 
 */
public class Modifier {

    // constants a la JLS ?
    public static final int NOTHING = 0;
    public static final int ABSTRACT = 1;
    public static final int FINAL = 2;

    // public static final int INTERFACE 	= 4;
    public static final int STATIC = 128;
    public static final int PUBLIC = 64;
    public static final int PROTECTED = 32;
    public static final int PACKAGE = 4;
    public static final int PRIVATE = 16;
    public static final int SYNCHRONIZED = 256;
    public static final int TRANSIENT = 512;
    public static final int NATIVE = 8;
    public static final int VOLATILE = 1024;
    public static final int STRICT = 2048;
    public static final int ALL = 4095;
    protected static int    modi = NOTHING;

    /**
     * Constructor declaration
     *
     *
     * @param m
     *
     * @see
     */
    public Modifier(int m) {
	modi = m;
    }

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public Modifier() {}

    /**
     * Method declaration
     *
     *
     * @see
     */
    public static void clear() {
	modi = NOTHING;
    } 

    /**
     * Converts a java.lang.reflect.Modifier value into a
     * jass.reflect.Modifiere value. The result value will represent
     * the same access modifiers as the input.
     * e.g.: java.lang.reflect.Modifier.isAbstract(m) ==
     *       jass.reflect.Modifier.isAbstract(jass.reflect.Modifier.convert(m))
     *  is always true.
     *
     * @see java.lang.reflect.Modifier */
    public static int convert(int m) {
	clear();
	if (java.lang.reflect.Modifier.isAbstract(m)) {
	    setAbstract();
	} 
	if (java.lang.reflect.Modifier.isFinal(m)) {
	    setFinal();
	} 
	if (java.lang.reflect.Modifier.isNative(m)) {
	    setNative();
	} 
	if (java.lang.reflect.Modifier.isPrivate(m)) {
	    setPrivate();
	} 
	if (java.lang.reflect.Modifier.isProtected(m)) {
	    setProtected();
	} 
	if (java.lang.reflect.Modifier.isPublic(m)) {
	    setPublic();
	} 
	if (java.lang.reflect.Modifier.isStatic(m)) {
	    setStatic();
	} 
	if (java.lang.reflect.Modifier.isSynchronized(m)) {
	    setSynchronized();
	} 
	if (java.lang.reflect.Modifier.isStrict(m)) {
	    setStrict();
	} 
	if (java.lang.reflect.Modifier.isTransient(m)) {
	    setTransient();
	} 
	if (java.lang.reflect.Modifier.isVolatile(m)) {
	    setVolatile();
	} 

	return modi;
    } 

    /**
     * Return true if the specifier integer includes the
     * <code>abstract</code> modifier.  
     */
    public static boolean isAbstract(int mod) {
	return (mod & ABSTRACT) != 0;
    } 

    /**
     * Return true if the specifier integer includes the
     * <code>final</code> modifier.  
     */
    public static boolean isFinal(int mod) {
	return (mod & FINAL) != 0;
    } 

    /**
     * Return true if the specifier integer includes the
     * <code>static</code> modifier.  
     */
    public static boolean isStatic(int mod) {
	return (mod & STATIC) != 0;
    } 

    /**
     * Return true if the specifier integer includes the
     * <code>public</code> modifier.  
     */
    public static boolean isPublic(int mod) {
	return (mod & PUBLIC) != 0;
    } 

    /**
     * Return true if the specifier integer includes the
     * <code>protected</code> modifier.  
     */
    public static boolean isProtected(int mod) {
	return (mod & PROTECTED) != 0;
    } 

    /**
     * Return true if the specifier integer includes the
     * <code>package</code> modifier.  
     */
    public static boolean isPackage(int mod) {
	return (mod & PACKAGE) != 0;
    } 

    /**
     * Return true if the specifier integer includes the
     * <code>private</code> modifier.  
     */
    public static boolean isPrivate(int mod) {
	return (mod & PRIVATE) != 0;
    } 

    /**
     * Return true if the specifier integer includes the
     * <code>synchronized</code> modifier.  
     */
    public static boolean isSynchronized(int mod) {
	return (mod & SYNCHRONIZED) != 0;
    } 

    /**
     * Return true if the specifier integer includes the
     * <code>strict</code> modifier.  
     */
    public static boolean isStrict(int mod) {
	return (mod & STRICT) != 0;
    } 

    /**
     * Return true if the specifier integer includes the
     * <code>native</code> modifier.  
     */
    public static boolean isNative(int mod) {
	return (mod & NATIVE) != 0;
    } 

    /**
     * Return true if the specifier integer includes the
     * <code>transient</code> modifier.  
     */
    public static boolean isTransient(int mod) {
	return (mod & TRANSIENT) != 0;
    } 

    /**
     * Return true if the specifier integer includes the
     * <code>volatile</code> modifier.  
     */
    public static boolean isVolatile(int mod) {
	return (mod & VOLATILE) != 0;
    } 

    /**
     * Add the <code>final</code> modifier to the current specifier
     * integer. CAUTION: the 'current specifier integer' is a class
     * member of Modifier!
     */
    public static void setFinal() {
	modi = modi | FINAL;
    } 

    /**
     * Add the <code>static</code> modifier to the current specifier
     * integer. CAUTION: the 'current specifier integer' is a class
     * member of Modifier!
     */
    public static void setStatic() {
	modi = modi | STATIC;
    } 

    /**
     * Add the <code>abstract</code> modifier to the current specifier
     * integer. CAUTION: the 'current specifier integer' is a class
     * member of Modifier!
     */
    public static void setAbstract() {
	modi = modi | ABSTRACT;
    } 

    /**
     * Add the <code>public</code> modifier to the current specifier
     * integer. This will remove any <code>protected</code>,
     * <code>private</code> or <code>package</code> modifier.
     * CAUTION: the 'current specifier integer' is a class member of
     * Modifier!  
     */
    public static void setPublic() {
	modi = modi | PUBLIC;
	modi = modi & (ALL - PROTECTED - PRIVATE - PACKAGE);
    } 

    /**
     * Add the <code>protected</code> modifier to the current specifier
     * integer. This will remove any <code>public</code>,
     * <code>private</code> or <code>package</code> modifier.
     * CAUTION: the 'current specifier integer' is a class member of
     * Modifier!  
     */
    public static void setProtected() {
	modi = modi | PROTECTED;
	modi = modi & (ALL - PUBLIC - PRIVATE - PACKAGE);
    } 

    /**
     * Add the <code>package</code> modifier to the current specifier
     * integer. This will remove any <code>public</code>,
     * <code>private</code> or <code>protected</code> modifier.
     * CAUTION: the 'current specifier integer' is a class member of
     * Modifier!  
     */
    public static void setPackage() {
	modi = modi | PACKAGE;
	modi = modi & (ALL - PUBLIC - PROTECTED - PRIVATE);
    } 

    /**
     * Add the <code>private</code> modifier to the current specifier
     * integer. This will remove any <code>public</code>,
     * <code>package</code> or <code>protected</code> modifier.
     * CAUTION: the 'current specifier integer' is a class member of
     * Modifier!  
     */
    public static void setPrivate() {
	modi = modi | PRIVATE;
	modi = modi & (ALL - PROTECTED - PUBLIC - PACKAGE);
    } 

    /**
     * Add the <code>native</code> modifier to the current specifier
     * integer. CAUTION: the 'current specifier integer' is a class
     * member of Modifier!
     */
    public static void setNative() {
	modi = modi | NATIVE;
    } 

    /**
     * Add the <code>transient</code> modifier to the current specifier
     * integer. CAUTION: the 'current specifier integer' is a class
     * member of Modifier!
     */
    public static void setTransient() {
	modi = modi | TRANSIENT;
    } 

    /**
     * Add the <code>synchronized</code> modifier to the current specifier
     * integer. CAUTION: the 'current specifier integer' is a class
     * member of Modifier!
     */
    public static void setSynchronized() {
	modi = modi | SYNCHRONIZED;
    } 

    /**
     * Add the <code>strict</code> modifier to the current specifier
     * integer. CAUTION: the 'current specifier integer' is a class
     * member of Modifier!
     */
    public static void setStrict() {
	modi = modi | STRICT;
    } 

    /**
     * Add the <code>volatile</code> modifier to the current specifier
     * integer. CAUTION: the 'current specifier integer' is a class
     * member of Modifier!
     */
    public static void setVolatile() {
	modi = modi | VOLATILE;
    } 

    /**
     * Get the current specifier integer. CAUTION: the 'current
     * specifier integer' is a class member of Modifier!  
     */
    public static int getModifier() {
	return modi;
    } 

    /**
     * Set the current specifier integer. CAUTION: the 'current
     * specifier integer' is a class member of Modifier!  
     */
    public static void setModifier(int m) {
	modi = m;
    } 

    /**
     * Add the modifier identified by its String representation to the
     * current specifier integer. CAUTION: the 'current specifier
     * integer' is a class member of Modifier!
     */
    public static void setFromString(String s) {
	if (s.equals("abstract")) {
	    setAbstract();
	} else if (s.equals("final")) {
        setFinal();
	} else if (s.equals("static")) {
	    setStatic();
	} else if (s.equals("public")) {
	    setPublic();
	} else if (s.equals("protected")) {
	    setProtected();
	} else if (s.equals("private")) {
	    setPrivate();
	} else if (s.equals("synchonized")) {
	    setSynchronized();
	} else if (s.equals("strictfp")) {
	    setStrict();
	} else if (s.equals("volatile")) {
	    setVolatile();
	} else if (s.equals("transient")) {
	    setTransient();
	} else if (s.equals("native")) {
	    setNative();
        } 
        // if (s.equals(""))) setPackage();
    } 

    /**
     * Return a Java conform String representation of the given
     * specifier integer.  
     */
    public static String toJava(int mod) {
	if (mod == NOTHING) {
	    return "";
	} else {
	    return toString(mod);
	} 
    } 

    /**
     * Append the text to the StringBuffer and return the
     * StringBuffer. If the buffer already contains text, a space ("
     * ") will be appended before the text.
     * @param buffer the StringBuffer, to append the test to
     * @param text   the String to append to the buffer
     * @return the buffer (for convinience)
     */
    private static StringBuffer appendSeperated (StringBuffer buffer, 
                                                 String       text) {
        if (buffer.length() != 0) { 
            buffer.append(" "); 
        }
        buffer.append(text); 
        return buffer; 
    }

    /**
     * Return a String representation of the given specifier integer.  */
    public static String toString(int mod) {
        if (mod == NOTHING) {
	    return "[unreflected]";
	} 
	StringBuffer s = new StringBuffer();
	if (isAbstract(mod)) {
	    s.append("abstract");
	} 
	if (isFinal(mod)) {
	    appendSeperated(s,"final");
	} 
	if (isPublic(mod)) {
	    appendSeperated(s,"public");
	} 
	if (isProtected(mod)) {
            appendSeperated(s,"protected");
	} 
	if (isPrivate(mod)) {
	    appendSeperated(s,"private");
	} 
	if (isPackage(mod)) {
	    //appendSeperated(s,"");
	} 
	if (isStatic(mod)) {
	    appendSeperated(s,"static");
	} 
	if (isSynchronized(mod)) {
	    appendSeperated(s,"synchronized");
	} 
	if (isStrict(mod)) {
	    appendSeperated(s,"strictfp");
	} 
	if (isNative(mod)) {
	    appendSeperated(s,"native");
	} 
	if (isVolatile(mod)) {
	    appendSeperated(s,"volatile");
	} 
	if (isTransient(mod)) {
	    appendSeperated(s,"transient");
	} 
	return s.toString();
    } 

    /**
     * Is the first modifier more visible than the second? If both 
     * modifiers have the same visibility, the result will be false.
     */
    public static boolean isMoreVisible(int m2, int m1) {
	if (isPublic(m2) 
		&& (isProtected(m1) || isPackage(m1) || isPrivate(m1))) {
	    return true;
	} 

	if (isProtected(m2) && (isPackage(m1) || isPrivate(m1))) {
	    return true;
	} 

	if (isPackage(m2) && isPrivate(m1)) {
	    return true;
	} 

	return false;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

