/*-- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.util.*;
import java.io.*;
import jass.GlobalFlags;
import jass.Jass;
import jass.parser.*;
import jass.runtime.util.FileTools;
import jass.visitor.*;
import jass.util.SourceFile;

/**
 * The ClassPool stores a hashtable of class names and their related
 * jass.reflect.Class object (if exists).
 *
 *
 * @author Detlef Bartetzko, Michael Plath
 * @version %I%, %G%
 */
public final class ClassPool {
    public static final Class       Null = new NullClass();
    public static final Class       Void = new VoidClass();
    public static final Class       Short = new ShortClass();
    public static final Class       Byte = new ByteClass();
    public static final Class       Int = new IntClass();
    public static final Class       Long = new LongClass();
    public static final Class       Boolean = new BooleanClass();
    public static final Class       Char = new CharClass();
    public static final Class       Float = new FloatClass();
    public static final Class       Double = new DoubleClass();
    public static int		    methodCount;
    public static int		    fieldCount;
    public static int		    classCount;
    private static Hashtable	    classes = new Hashtable();
    protected static boolean	    hasChanged = true;
    protected static JassParser     jp;
    protected static ReflectVisitor refV;
    protected static Vector	    classesToCompile = new Vector();
    protected static Vector	    compiledClasses = new Vector();

    /**
     * Initialize the ClassPool with the given parser and predifined 
     * types. Predefined types include the scalar types, <code>void</code>
     * and <code>null</code>.
     *
     * @param _jp  the Jass parser
     */
    public static void initialize(JassParser _jp) {
	jp = _jp;
	classes = new Hashtable();

	classes.put("byte", Byte);
	classes.put("short", Short);
	classes.put("boolean", Boolean);
	classes.put("char", Char);
	classes.put("int", Int);
	classes.put("long", Long);
	classes.put("float", Float);
	classes.put("double", Double);
	classes.put("void", Void);
	classes.put("null", Null);
    } 

    /**
     * Strip the extension (introduced by the last "." in the String) 
     * from a filename (String).
     *
     * @param filename The filename to strip
     *
     * @return the same String without the part after the last "."
     */
    protected static String filenameWithoutExtension(String filename) {
	return filename.substring(0, filename.lastIndexOf('.'));
    } 

    /**
     * Add a class to the pool, if it is not already included. If the class
     * was already found in the pool, it will not be added again, but the
     * return value will be <code>null</code>. In case of success, the class
     * itself will be returned
     *
     * @param c  the class to add
     *
     * @return the same object (success), or <code>null</code> (already 
     *         inserted)
     */
    public static jass.reflect.Class addClass(jass.reflect.Class c) {
	if (!classes.containsKey(c.getName())) {
	    classes.put(c.getName(), c);
	} else {

	    // System.out.println("Error: ClassPool: Class already contained: "+c.getName());
	    c = null;
	} 

	return c;
    } 

    private static String[][] PRIMITIVE_TYPES =
    { {"boolean", "Z"}, {"byte", "B"}, {"char", "C"}, {"short", "S"}, 
      {"int", "I"}, {"long", "J"}, {"float", "F"}, {"double", "D"} };

    /**
     * Check wheter name of a type specifies a primitive type. These
     * primitive types are <code>boolean, byte, char, short, int,
     * long, float</code> and <code>double</code>.
     *
     * @param n  the name of the type.
     *
     * @return true, if the name was one of the above.
     */
    private static boolean isPrimitiveName(String n) {
	for (int i = 0; i < PRIMITIVE_TYPES.length; i++) {
	    if (n.equals(PRIMITIVE_TYPES[i][0])) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Check if the name starts with "["
     *
     * @param n  the name
     *
     * @return  true, if it starts with "["
     */
    private static boolean isArrayName(String n) {
	return n.startsWith("[");
    } 

    /**
     * Get a Class object by it's name.
     *
     * @param classname   the class name
     *
     * @return  the class object
     */
    public static Class getClass(String classname) {

	// System.out.println("ClassPool.getClass(" + classname + ")");
	// if the classname is an array name (starting with some "[") extract the component name !
	String component = arrayToComponent(classname);

	try {
	    if (classes.containsKey(classname)) {
		return (Class) classes.get(classname);
	    } else {
		if (isPrimitiveName(component) 
			|| NameAnalysis.lookUpInClassPath(component)) {
		    if (isArrayName(classname)) {

			// array
			if (GlobalFlags.VERBOSE > 3) {
			    System.out.println("Message: Adding class [array] " 
					       + classname + ".");
			} 

			Class ac = new Class(classname);

			ac.setComponentType(ClassPool.getClass(component), 
					    classname.lastIndexOf("[") + 1);
			classes.put(classname, ac);
			ac.setUnreflected(false);

			return ac;
		    } else {

			// no array
			if (GlobalFlags.VERBOSE > 3) {
			    System.out.println("Message: Adding class " 
					       + classname + ".");
			} 

			Class c = new Class(classname);

			c.setUnreflected(true);
			classes.put(classname, c);

			hasChanged = true;

			return c;
		    } 
		} 

		ReflectionError e = 
		    new ReflectionError("Fatal Error: Could not find class '" 
					+ classname 
					+ "', maybe missing packagename in source file");

		e.printStackTrace();

		throw e;
	    } 
	} catch (IOException io) {
	    System.err.println("Fatal Error:" + io 
			       + " occured while searching in classpath. Was looking up class " 
			       + classname + ".");
	    System.exit(1);
	} 

	throw new ReflectionError("Fatal Error: Could not find class " 
				  + classname + ".");
    } 

    /**
     * Get the component string from an array type string
     *
     * @param a  the array type
     *
     * @return  the component type
     */
    public static String arrayToComponent(String a) {

	// get the component name a la JLS
	// Delete the "[" in front if present
	if (a.startsWith("[")) {
	    String h = a.substring(a.lastIndexOf("[") + 1, a.length());

	    // what is the first letter ?
	    if (h.startsWith("L")) {
		return h.substring(1, 
				   h.length() 
				   - 1);    // delete first letter and last ";"
	    } 

	    for (int i = 0; i < PRIMITIVE_TYPES.length; i++) {
		if (h.startsWith(PRIMITIVE_TYPES[i][1])) {
		    return PRIMITIVE_TYPES[i][0];
		}
	    } 
	} 

	return a;
    } 

    /**
     * Convert an array type to a type name
     *
     * @param a  the array type
     *
     * @return  the type name
     *
     * @see componentToArray(java.lang.String, int)
     */
    public static String arrayToTypeName(String a) {
	if (a.startsWith("[")) {
	    StringBuffer sb = new StringBuffer();
	    String       h = a.substring(a.lastIndexOf("[") + 1, a.length());

	    // what is the first letter ?
	    if (h.startsWith("L")) {
		sb.append(h.substring(1, 
				      h.length() 
				      - 1));    // delete first letter and last ";"
	    } 


	    for (int i = 0; i < PRIMITIVE_TYPES.length; i++) {
		if (h.startsWith(PRIMITIVE_TYPES[i][1])) {
		    sb.append(PRIMITIVE_TYPES[i][0]);
		}
	    } 

	    for (int i = 0; i <= a.lastIndexOf("["); i++) {
		sb.append("[]");
	    }

	    return sb.toString();
	} else {
	    return a;
	}
    } 

    /**
     * Convert a component to an array 
     *
     * @param a  the type of the array
     * @param d  the array depth
     *
     * @return  the array
     *
     * @see arrayToTypeName(java.lang.String)
     */
    public static String componentToArray(String a, int d) {
	if (d > 0) {
	    StringBuffer sb = new StringBuffer();

	    for (int i = 0; i < d; i++) {
		sb.append("[");
	    }

	    for (int i = 0; i < PRIMITIVE_TYPES.length; i++) {
		if (a.equals(PRIMITIVE_TYPES[i][0])) {
		    return sb.append(PRIMITIVE_TYPES[i][1]).toString();
		}
	    } 

	    return sb.append("L").append(a).append(";").toString();
	} else {
	    return a;
	}
    } 

    /**
     * Dump the content of the class pool.
     *
     *
     * @param pw  the PrintWriter where to dump to
     * @param indent  the indention to be used
     */
    public static void dump(PrintWriter pw, String indent) {
	methodCount = 0;
	classCount = 0;
	fieldCount = 0;

	pw.println("Contents of ClassPool:");

	Enumeration e = classes.elements();

	while (e.hasMoreElements()) {
	    Class c = (Class) e.nextElement();

	    c.dump(pw, "   " + indent);
	} 

	pw.println();
	pw.println("jass.reflect.ClassPool Statistics:");
	pw.println("Fields  : " + fieldCount);
	pw.println("Methods : " + methodCount);
	pw.println("Classes : " + classCount);
    } 

    /**
     * Reflect all classes in the class pool.
     *
     * @see jass.reflect.Class.reflect()
     */
    public static void reflect() {
	Enumeration e = classes.elements();

	while (e.hasMoreElements()) {
	    jass.reflect.Class c = (jass.reflect.Class) e.nextElement();

	    c.reflect();
	} 
    } 

    /**
     * Reflect all classes in the ClassPool that are not yet reflected.
     *
     * @see jass.reflect
     */
    public static void reflectUnreflectedClasses() {
	hasChanged = true;

	while (hasChanged) {
	    hasChanged = false;

	    Enumeration e = classes.elements();

	    while (e.hasMoreElements()) {
		jass.reflect.Class c = (jass.reflect.Class) e.nextElement();

		if (c.isUnreflected()) {
		    reflect(c);
		} 
	    } 
	} 

	Enumeration e = classes.elements();

	while (e.hasMoreElements()) {
	    jass.reflect.Class c = (jass.reflect.Class) e.nextElement();

	    c.detectInheritedEntities();
	} 
    } 

    /**
     * Request a reflected class.
     *
     * @param name  the class name
     * @return the reflected class
     *
     * @see reflect(jass.reflect.Class)
     */
    public static Class getReflectedClass(String name) {
	Class c = getClass(name);

	if (c.isUnreflected()) {
	    reflect(c);
	} 

	return c;
    } 

    /**
     * Reflect a class given by name.
     *
     * @param name the name of the class to reflect
     *
     * @see reflect(jass.reflect.Class)
     */
    public static void reflectClass(String name) {
	Class c = (Class) classes.get(name);

	if (classes.containsKey(name) && c.isUnreflected()) {
	    reflect(c);

	    if (GlobalFlags.VERBOSE > 2) {
		System.out.println("Message: Ended reflecting " + c.getName() 
				   + ".");
	    } 
	} else {
	    System.err.println("Fatal internal error: Class reflection requested but class is not present in classpool or already reflected.");
	    System.exit(1);
	} 
    } 

    /**
     * Reflect a class.
     *
     * @param c the class to reflect.
     *
     * @see jass.reflect.Class
     */
    private static void reflect(Class c) {
	if (GlobalFlags.VERBOSE > 2) {
		System.out.println("Reflecting " + c.getName() + "...");
	}
	
	String  name_to_reflect = arrayToComponent(c.getName());
	boolean ref_component = false;

	if (c.getName().startsWith("[")) {
	    ref_component = true;
	} 

	try {
	    if (classes.containsKey(name_to_reflect) 
		    &&!((Class) classes.get(name_to_reflect)).isUnreflected()) {
		c.init((Class) classes.get(name_to_reflect));
	    } else {
		if (GlobalFlags.VERBOSE > 2) {
		    System.err.println("Message: Expand name " 
				       + name_to_reflect + ":");
		} 

		SourceFile path = NameAnalysis.expandFullPath(name_to_reflect);

		if (GlobalFlags.VERBOSE > 2) {
		    System.out.println("Message: Reflecting path " + path 
				       + ":");
		} 

		if ((path != null) && !path.hasUnknownExtension()) {
		    if (GlobalFlags.VERBOSE > 2) {
			System.out.println("Message: Reflecting sourcefile " 
					   + c.getName() + " ... ");
		    } 

		    refV = new ReflectVisitor();

		    jp.ReInit(new java.io.FileInputStream(path));

		    JassCompilationUnit n = jp.CompilationUnit();

		    // accepting !!
		    Class		cr = (Class) n.jjtAccept(refV, null);

		    if (classes.containsKey(name_to_reflect) 
			    && ref_component) {
			if (GlobalFlags.VERBOSE > 2) {
			    System.out.println("Message: Reflecting class " 
					       + name_to_reflect + " ...");
			} 

			Class component = 
			    (Class) classes.get(name_to_reflect);

			component.init(cr);
			component.detectInheritedEntities();
		    } 

		    c.init(cr);
		    c.detectInheritedEntities();
		} else {
		    if (GlobalFlags.VERBOSE > 2) {
			System.out.println("Message: Reflecting [via java.lang.reflect] class " 
					   + c.getName() + " ...");
		    } 

		    java.lang.Class cr = 
			java.lang.Class.forName(name_to_reflect);

		    if (classes.containsKey(name_to_reflect) 
			    && ref_component) {
			if (GlobalFlags.VERBOSE > 2) {
			    System.out.println("Message: Reflecting [via java.lang.reflect] class " 
					       + name_to_reflect + " ...");
			} 

			Class component = 
			    (Class) classes.get(name_to_reflect);

			component.init(cr);
			component.detectInheritedEntities();
		    } 

		    c.init(cr);
		    c.detectInheritedEntities();
		} 
	    } 
	} catch (IOException io) {
	    System.err.println("Fatal Error:" + io 
			       + " occured while searching in classpath. Was looking up class " 
			       + c + ".");
	    System.exit(1);
	} catch (ParseException parse) {
	    System.err.println("Fatal Error: ParseException occured while parsing class " 
			       + c + ":\n" + parse);
	    System.exit(1);
	} catch (ClassNotFoundException cnf) {
	    System.err.println("Fatal Error: ClassNotFoundException occured while trying to reflect class " 
			       + c + " via java.lang.relfect:\n" + cnf);
	    System.exit(1);
	} 
    } 

    /**
     * Returns a method of a class based on an index.
     * @param number the requested method number, starting by one !
     * @exception ArrayIndexOutOfBoundsException if number is out of range.
     * @exception NullPointerException if classname is not in ClassPool.
     */
    public static jass.reflect.Method getMethod(String classname, 
						int number) {
	jass.reflect.Class    c = getClass(classname);
	jass.reflect.Method[] ms = c.getDeclaredMethods();
	return ms[number - 1];
    } 

    /**
     * Gets the constructor of a class at given index.
     * @param classname name of class
     * @param number index of method, one based!
     * @return the number-th constructor of the class.
     *
     * @exception NullPointerException if classname is not in ClassPool.
     * @exception ArrayIndexOutOfBoundsException if number is out of range.
     */
    public static jass.reflect.Constructor getConstructor(String classname, 
	    int number) {
	jass.reflect.Class	       c = getClass(classname);
	jass.reflect.Constructor[] cs = c.getDeclaredConstructors();
	return cs[number - 1];
    } 

    /**
     * Add a class to the list of classes that need to be compiled.
     *
     * @param clazz the class to add
     *
     * @see getClassesToCompile()
     */
    public static void addClassToCompile(Class clazz) {

	// System.out.println("new:" + clazz);
	if (!compiledClasses.contains(clazz)) {

	    // System.out.println("added:" + clazz);
	    classesToCompile.add(clazz);
	} 
    } 


    /**
     * Get a class that has to be compiled. This will return one item of 
     * the list of classes to compile but not modify the list. 
     *
     * @return a class that is not yet compiled.
     *    
     * @see moreClassesToCompile(), markAsCompiled(jass.reflect.Class)
     */
    public static Class getClassToCompile() {
	return (Class) classesToCompile.firstElement();
    }

    /**
     * Test whether there are still classes left to be compiled. This 
     * test whether the list of classes to compile is not empty.
     *
     * @see getClassToCompile(), markAsCompiled(jass.reflect.Class)
     */
    public static boolean moreClassesToCompile() {
	return ! classesToCompile.isEmpty();
    }

    /**
     * Mark a class as compiled. The class will be removed from the list 
     * of classes that have to be compiled, and it will be added to a
     * list of compiled classes.
     *
     * @param clazz the class that was compiled
     *
     * @see getClassToCompile()
     */
    public static void markAsCompiled(Class clazz) {
	classesToCompile.remove(clazz);
	compiledClasses.add(clazz);
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

