/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.lang.reflect.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import jass.parser.SimpleNode;
import jass.util.Utility;
import jass.util.Set;
import jass.GlobalFlags;
import jass.reflect.traceAssertion.*;

/**
 * This class wraps a Java class or interface (type). It is an
 * analogon to java.lang.Class but the informations can be modified by
 * Java programms, not only by the virtual machine. Not all methods of
 * java.lang.Class are supported, some new methods are defined. A
 * "get" call may initiated a reflection process via the class
 * ClassPool if class is unreflected. A "set" call may set the state
 * of the object to "reflected".
 * @see jass.reflect.ClassPool
 * @see java.lang.Class 
 */
public class Class extends Entity implements Context {

    // The modifiers should be private protected, but this is not
    // supported in Java 1.1

    /**
     * The imported types of this class, import on demand if last
     * letter is a star (*) 
     */
    protected String[]		  importedTypes = new String[0];

    /**
     * Methods declared by this class or interface.
     */
    protected Method[]		  ms = new Method[0];

    /**
     * Methods inherited from superclass(es) or superinterfaces.
     */
    protected Method[]		  ms_in = new Method[0];

    /**
     * Fields declared by this class or interface.
     */
    protected Field[]		  fs = new Field[0];

    /**
     * Fields inherited from superclass(es) or superinterfaces.
     */
    protected Field[]		  fs_in = new Field[0];

    /**
     * Interfaces implemented by this class, or interfaces extended by
     * this interface.  
     */
    protected Class[]		  is = new Class[0];

    /**
     * Constructors declared by this class.
     */
    protected Constructor[]       cs = new Constructor[0];

    /**
     * Superclass of this class.
     */
    protected Class		  superClass = null;

    /**
     * Array dimensions. Greater zero if class represents an array.
     */
    protected int		  arrayDims = 0;

    /**
     * The type of the array components, if class represents an array.
     */
    protected Class		  componentType = null;

    /**
     * Invariant declared by this class.
     */
    protected Invariant		  inv = null;

    /**
     * Trace Assertion declared by this class.
     */
    protected TraceAssertion      traceAssertion = null;

    /**
     * Special labels which can be verified by the Trace Assertion
     * statement VERIFY 
     */

    // protected Vector labelsForVerification = new Vector();

    /**
     * Mapping from primary nodes to method call expression. Needed
     * for trace stack 
     */
    protected Hashtable		  primaryNodeToMethodCall = new Hashtable();

    /**
     * Flag if class is unreflected or not.
     */
    protected boolean		  unreflected = true;

    /**
     * Flag if class represents an interface. False if class
     * represents a class.  
     */
    protected boolean		  isInterface = false;

    /**
     * Refined class if Refinement signaled.
     */
    protected Class		  refines = null;

    /**
     * Expressions in initialisers of this class (field init, static
     * init, ...).  
     */
    protected Vector		  expr = new Vector();

    /**
     * Dataflowinformation of the expressions.
     */
    protected DependencyCollector dc = new DependencyCollector();

    /**
     * Flag if the inherited methods allready determined.
     */
    protected boolean		  inherited = false;

    /**
     * The forall/exists expression in the assertions of this class.
     */
    protected Expression[]	  forall_exists = new Expression[0];

    /**
     * @param _name the full qualified name of the class.
     */
    public Class(String _name) {
	super(_name);
	type = this;
	unreflected = true;
    }

    /**
     * This init method allows the mapping of a reflection class
     * produced by the VM to this reflection class.  All nessesary
     * informations are copied. State of object is reflected after
     * execution of method.
     * @param c the class to map.  */
    public void init(java.lang.Class c) {
	/** require c != null; **/
	unreflected = false;

	java.lang.reflect.Method[]      cms = c.getDeclaredMethods();
	java.lang.reflect.Field[]       cfs = c.getDeclaredFields();
	java.lang.reflect.Constructor[] ccs = c.getDeclaredConstructors();

	ms = new Method[cms.length];
	fs = new Field[cfs.length];
	cs = new Constructor[ccs.length];

	for (int i = 0; i < cms.length; i++) {
	    ms[i] = new Method();
	    ms[i].init(cms[i]);
	} 

	for (int i = 0; i < cfs.length; i++) {
	    fs[i] = new Field();
	    fs[i].init(cfs[i]);
	} 

	for (int i = 0; i < ccs.length; i++) {
	    cs[i] = new Constructor();
	    cs[i].init(ccs[i]);
	} 

	if (c.getSuperclass() != null) {
	    superClass = ClassPool.getClass(c.getSuperclass().getName());
	} 

	java.lang.Class[] cis = c.getInterfaces();

	is = new Class[cis.length];

	for (int i = 0; i < cis.length; i++) {
	    is[i] = ClassPool.getClass(cis[i].getName());
	} 

	setContainer(fs);
	setContainer(ms);
	setContainer(cs);

	modifiers = Modifier.convert(c.getModifiers());
	isInterface = c.isInterface();
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Copies the nessesary informations from a given class. State of
     * object is reflected after execution of method.  Method is used
     * from the ClassPool to copy informations already reflected.
     * @param c the class to copy.  
     */
    public void init(Class c) {
	/** require c != null; **/
	unreflected = false;
	ms = c.getDeclaredMethods();
	fs = c.getDeclaredFields();
	cs = c.getDeclaredConstructors();
	superClass = c.getSuperclass();
	modifiers = c.getModifier();
	is = c.getInterfaces();
	isInterface = c.isInterface();
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     *
     * @return
     *
     * @see
     */
    protected static String idString(SimpleNode node) {
	String idString = "";

	while (node != null) {
	    idString += node.getId();

	    SimpleNode parent = (SimpleNode) node.jjtGetParent();

	    if (parent != null) {
		idString += parent.getChildNr(node);
	    } 

	    node = parent;
	} 

	return idString;
    } 

    /*
     * public void addLabelToVerify(AssertionLabel label)
     * {
     * labelsForVerification.add(label);
     * }
     */

    /**
     * Method declaration
     *
     *
     * @param node
     * @param expression
     *
     * @see
     */
    public void addPrimaryNode(SimpleNode node, 
			       MethodCallExpression expression) {
	String idString = idString(node);

	primaryNodeToMethodCall.put(idString, expression);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     *
     * @return
     *
     * @see
     */
    public MethodCallExpression getMethodCall(SimpleNode node) {
	String idString = idString(node);

	return (MethodCallExpression) primaryNodeToMethodCall.get(idString);
    } 

    /**
     * Gets the reflection state of this class.
     * @returns true if class is unreflected; false if class is reflected.
     */
    public boolean isUnreflected() {
	return unreflected;
    } 

    /**
     * Sets the reflection state of this class.
     * @param b the state of reclection: true if class is unreflected;
     *          false if class is reflected.  
     */
    public void setUnreflected(boolean b) {
	unreflected = b;
    } 

    /**
     * Returns the name of the package the class is contained in.
     * @returns the package name; null if no package is used.
     */
    public String getPackageName() {
	if (name.lastIndexOf(".") > -1) {
	    return name.substring(0, name.lastIndexOf("."));
	} 
	return null;
    } 

    /**
     * If this Class object represents an array type, returns true,
     * otherwise returns false.
     * @returns true iff class is an array type. 
     */
    public boolean isArray() {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 

	return arrayDims > 0;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Returns the dimension of the array.
     * @returns the dimension of array; zero if class is no array type.
     */
    public int getArrayDims() {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 

	return arrayDims;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Set the component type and the array dimensions of the class.
     * @param c the class of the components
     * @param d the dimensions
     */
    public void setComponentType(Class c, int d) {
	/** require (d > 0 && c != null) || (d == 0 && c == null); **/
	componentType = c;
	arrayDims = d;
    } 

    /**
     * If this class represents an array type, returns the Class
     * object representing the component type of the array; otherwise
     * returns null
     * @return the component class or null */
    public Class getComponentType() {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 

	return componentType;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Modifies the interface information.
     * @param b is true if class represents an interface; false if
     *          class represents a class.  
     */
    public void setInterface(boolean b) {
	isInterface = b;
    } 

    /**
     * Determines if the specified Class object represents an interface type.
     * @return true if class is interface; false if class represents a class
     */
    public boolean isInterface() {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 

	return isInterface;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Returns an array of Method objects reflecting all the methods
     * declared by the class or interface represented by this Class
     * object.
     * @returns the array of the methods; array of length zero if no
     * methods are declared.  
     */
    public jass.reflect.Method[] getDeclaredMethods() {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 

	return ms;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Method declaration
     *
     *
     * @param identifier
     * @param parameters
     *
     * @return
     *
     * @see
     */
    public jass.reflect.Method getMethodOrConstructor(String identifier, 
	    Class[] parameters) {
	Method matchedMethod;

	if (identifier.equals(getIdentifier())) {
	    matchedMethod = getConstructor(parameters);
	} else {
	    matchedMethod = getMethod(identifier, parameters);
	} 

	return matchedMethod;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public jass.reflect.Method[] getMethodsAndConstructors() {

	// System.out.println("Name:" + name);
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 

	jass.reflect.Method[] methods = 
	    new jass.reflect.Method[ms.length + ms_in.length + cs.length];

	// System.out.println(ms_in.length);
	int		      iMethod = 0;
	int		      i;

	i = 0;

	while (i < ms.length) {
	    methods[iMethod++] = ms[i++];

	    // System.out.println(methods[iMethod -1]);
	} 

	i = 0;

	while (i < ms_in.length) {
	    methods[iMethod++] = ms_in[i++];

	    // System.out.println(methods[iMethod -1]);
	} 

	i = 0;

	while (i < cs.length) {
	    methods[iMethod++] = cs[i++];

	    // System.out.println(methods[iMethod -1]);
	} 

	/*
	 * Hashtable methodTable = getMethods(new Hashtable());
	 * Method[] methods = new Method[methodTable.size()];
	 * Enumeration methodEnumeration = methodTable.elements();
	 * 
	 * int iMethod = 0;
	 * while(methodEnumeration.hasMoreElements())
	 * {
	 * methods[iMethod++] = (Method) methodEnumeration.nextElement();
	 * }
	 */
	return methods;
    } 

    /*
     * protected Hashtable getMethods(Hashtable collectedMethods)
     * {
     * jass.reflect.Method[] declaredMethods = getDeclaredMethods();
     * int numberOfDeclaredMethods = declaredMethods.length;
     * for(int iMethod = 0; iMethod < numberOfDeclaredMethods; ++iMethod)
     * {
     * jass.reflect.Method method = declaredMethods[iMethod];
     * String methodHashKey = method.getIdString();
     * // Don't collect overwritten methods
     * 
     * if(!collectedMethods.contains(methodHashKey));
     * {
     * collectedMethods.put(methodHashKey, method);
     * }
     * }
     * Class superclass = getSuperclass();
     * if(superclass != null)
     * {
     * superclass.getMethods(collectedMethods);
     * }
     * return collectedMethods;
     * }
     */

    /**
     * Returns an array of Field objects reflecting all the fields
     * declared by the class or interface represented by this Class
     * object.
     * @returns the array of the fields; array of length zero if no
     *          fields are declared. 
     */
    public Field[] getDeclaredFields() {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 

	return fs;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Returns an array of Field objects reflecting all the fields
     * inherited from the superclass(es) or superinterface(s)
     * represented by this Class object.
     * @returns the array of inherited fields; array of length zero
     *          if no fields are inherited. 
     */
    public Field[] getInheritedFields() {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 

	return fs_in;
	/** ensure !isUnreflected(); **/
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Method[] getMethods() {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 

	return ms;
    } 

    /**
     * Returns an array of Method objects reflecting all the methods
     * inherited from the superclass(es) or superinterface(s)
     * represented by this Class object.
     * @returns the array of inherited methods; array of length zero
     *          if no methods are inherited. 
     */
    public Method[] getInheritedMethods() {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 
	return ms_in;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Returns an array of Constructor objects reflecting all the
     * constructors declared by the class represented by this Class
     * object. If no constructor is declared the method
     * setDeclaredConstructors will add a default constructor.
     * @returns the array of the contructors. 
     */
    public Constructor[] getDeclaredConstructors() {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 
	return cs;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * If this object represents any class other than the class
     * Object, then the object that represents the superclass of that
     * class is returned.
     * @returns the superclass 
     */
    public Class getSuperclass() {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 
	return superClass;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Determines the interfaces implemented by the class or interface
     * represented by this object.  If the class represents an
     * interface this interface are extended by the superinterfaces.
     */
    public Class[] getInterfaces() {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 
	return is;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Sets the invariant declared by this class.
     * @param inv the invariant
     */
    public void setInvariant(Invariant inv) {
	/** require inv != null; **/
	this.inv = inv;

	inv.setContainer(this);
    } 

    /**
     * Method declaration
     *
     *
     * @param traceAssertion
     *
     * @see
     */
    public void setTraceAssertion(TraceAssertion traceAssertion) {
	this.traceAssertion = traceAssertion;

	traceAssertion.setContainer(this);
    } 

    /**
     * Gets the invariant declared by this class.
     * @return the invariant
     */
    public Invariant getInvariant() {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 
	return inv;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public TraceAssertion getTraceAssertion() {
	return traceAssertion;
    } 

    /**
     * Sets the imported types of the class.
     * @param v a vector containing strings representing the import
     *          declarations. 
     */
    public void setImportedTypes(Vector v) {
	/** require v != null; **/
	importedTypes = new String[v.size()];

	v.copyInto(importedTypes);
    } 

    /**
     * Return the imported types of this class.
     * @return the imported types.
     */
    public String[] getImportedTypes() {
	return importedTypes;
    } 

    /**
     * Sets the declared methods of this class or interface.
     * @param v a vector containing Method objects.
     */
    public void setDeclaredMethods(Vector v) {
	/** require v != null; **/
	ms = new jass.reflect.Method[v.size()];

	v.copyInto(ms);
	setContainer(ms);
    } 

    /**
     * Sets the declared constructors of this class. If no
     * constructors are specifies, a default constructor will be
     * added.  A constructor can only be set for a class not for an
     * interface.
     * @param v a vector containing the Constructor objects.  
     */
    public void setDeclaredConstructors(Vector v) {
	/** require !isInterface(); **/
	if (v.size() > 0) {
	    cs = new jass.reflect.Constructor[v.size()];

	    v.copyInto(cs);
	    setContainer(cs);
	} 

	/*
	 * // create standard constructor
	 * else {
	 * cs = new jass.reflect.Constructor[1];
	 * cs[0] = new DefaultConstructor(name);
	 * cs[0].setContainer(this);
	 * cs[0].reflect();
	 * }
	 */
    } 

    /**
     * Sets the declared fields of this class or interface.
     * @param v a vector containing Field objects.
     */
    public void setDeclaredFields(Vector v) {
	/** require v != null; **/
	fs = new jass.reflect.Field[v.size()];

	v.copyInto(fs);
	setContainer(fs);
    } 

    /**
     * Sets the implemented or extended interfaces of this class / interface.
     * @param v a vector containing Class objects.
     */
    public void setInterfaces(Vector v) {
	/** require v != null; **/
	is = new Class[v.size()];

	v.copyInto(is);
    } 

    /**
     * Sets the superclass of this class. This cannot be done for an interface.
     * @param c the superclass
     */
    public void setSuperclass(Class c) {
	/** require !isInterface(); **/
	superClass = c;
    } 

    /**
     * Get class identifier without leading package names or something
     * like that.  Suitable for naming a constructor.
     *
     * I would really like to get rid of it!!!!
     */
    public static String getIdentifier(String classname) {
	int beginOfIdentifier = classname.lastIndexOf(".") + 1;
	return classname.substring(beginOfIdentifier);
    }

    /**
     * Get class identifier without leading package names or something
     * like that.  Suitable for naming a constructor.
     *
     * @return the short class name.
     */
    public String getIdentifier() {
	/* Note: this works even if there is no dot in the class name 
	 * It doesn't handle inner classes though.
	 */	
	return name.substring(name.lastIndexOf(".") + 1);
    } 

    /**
     * Gets a method (declared or inherited) of this class. The
     * method is indentified by its id string, containing method name
     * and the comma seperated parameter list in brackets.
     * @param idString the id string of the method
     * @return the Method object matching the id string; null if
     *         method is not declared or inherited. 
     */
    public Method getMethod(String idString) {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 

	for (int j = 0; j < ms.length; j++) {
	    if (ms[j].getIdString().equals(idString)) {
		return ms[j];
	    } 
	} 

	// inherited field ?
	if (!inherited) {
	    detectInheritedEntities();
	}
	for (int j = 0; j < ms_in.length; j++) {
	    if (ms_in[j].getIdString().equals(idString)) {
		return ms_in[j];
	    } 
	} 

	return null;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Gets a method (only declared ones) of this class. The method is
     * indentified by its id string, containing method name and the
     * comma seperated parameter list in brackets.
     * @param idString the id string of the method
     * @return the Method object matching the id string; null if
     *         method is not declared.  
     */
    public Method getDeclaredMethod(String idString) {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 

	for (int j = 0; j < ms.length; j++) {
	    if (ms[j].getIdString().equals(idString)) {
		return ms[j];
	    } 
	} 

	return null;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Gets a method (declared or inherited) of this class. The
     * method is indentified by its name and an array of formal
     * parameters. In the parameterlist assignable types are allowed.
     * @param name the name of the method
     * @param params the array of formal parameters of the method
     * @return the Method object; null if method is not declared or inherited.  */
    public Method getMethod(String name, Class[] params) {
	/** require params != null; 
	            forall i : { 0 .. params.length-1 } # params[i] != null;
	**/
	if (unreflected) {
	    ClassPool.reflectClass(getName());
	} 

	for (int j = 0; j < ms.length; j++) {
	    FormalParameter[] fp = ms[j].getFormalParameters();

	    // System.out.println(ms[j].getName() + " <-> " + name);
	    if (ms[j].getName().equals(name) && fp.length == params.length) {
		boolean found = true;

		for (int i = 0; i < fp.length; i++) {
		    if (!fp[i].getType().isAssignableFrom(params[i])) {
			found = false;
		    } 
		} 

		if (found) {
		    return ms[j];
		} 
	    } 
	} 

	for (int j = 0; j < ms_in.length; j++) {

	    // System.out.println(ms_in[j].getName() + " <-> " + name);
	    FormalParameter[] fp = ms_in[j].getFormalParameters();

	    if (ms_in[j].getName().equals(name) 
		    && fp.length == params.length) {
		boolean found = true;

		for (int i = 0; i < fp.length; i++) {
		    if (!fp[i].getType().isAssignableFrom(params[i])) {
			found = false;
		    } 
		} 

		if (found) {
		    return ms_in[j];
		} 
	    } 
	} 

	return null;
	/** ensure !isUnreflected(); **/
    } 

    /*
     * public Method getMethod(String name, Class[] parameters)
     * {
     * Method method = null;
     * if (unreflected) ClassPool.reflectClass(getName());
     * String classnameToMatch = Method.separateClassIdentifier(name);
     * Class searchingClass = this;
     * 
     * while(searchingClass != null && searchingClass.getName().equals(classnameToMatch))
     * {
     * searchingClass = searchingClass.getSuperclass();
     * }
     * if(searchingClass != null)
     * {
     * Method[] methods = searchingClass.getMethods();
     * 
     * for (int iMethod = 0; method == null && iMethod < methods.length; ++iMethod)
     * {
     * Method methodToMatch = methods[iMethod];
     * System.out.println("Match " + methodToMatch.getName() + " <-> " + name);
     * FormalParameter[] parametersToMatch = methodToMatch.getFormalParameters();
     * 
     * if
     * (
     * methodToMatch.getName().equals(name)
     * && parametersToMatch.length == parameters.length
     * )
     * {
     * boolean methodFound = true;
     * for
     * (
     * int iParameter = 0;
     * !methodFound && iParameter < parametersToMatch.length;
     * ++iParameter
     * )
     * {
     * FormalParameter parameterToMatch = parametersToMatch[iParameter];
     * 
     * if (!parameterToMatch.getType().isAssignableFrom(parameters[iParameter]))
     * {
     * methodFound = false;
     * }
     * }
     * if(methodFound)
     * {
     * method = methodToMatch;
     * }
     * }
     * }
     * }
     * 
     * return method;
     * }
     */

    /**
     * Gets a constructor of this class. The constructor is
     * indentified by its id string, containing method name and the
     * comma seperated parameter list in brackets.
     * @param idString the id string of the method
     * @return the Constructor object matching the id string; null if
     *         constructor is not declared.  
     */
    public Constructor getDeclaredConstructor(String idString) {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 

	for (int j = 0; j < cs.length; j++) {
	    if (cs[j].getIdString().equals(idString)) {
		return cs[j];
	    } 
	} 

	return null;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Gets a constructor of this class. The constructor is
     * indentified by its name and an array of formal parameters. In
     * the parameterlist assignable types are allowed.
     * @param name the name of the constructor
     * @param params the array of formal parameters of the constructor
     * @return the Constructor object; null if constructor is not
     *         declared.  
     */
    public Constructor getConstructor(Class[] params) {
	/** require params != null; 
	            forall i : { 0 .. params.length-1 } # params[i] != null;
	**/
	if (unreflected) {
	    ClassPool.reflectClass(getName());
	} 

	for (int j = 0; j < cs.length; j++) {
	    FormalParameter[] fp = cs[j].getFormalParameters();

	    if (fp.length == params.length) {
		boolean found = true;

		for (int i = 0; i < fp.length; i++) {
		    if (!fp[i].getType().isAssignableFrom(params[i])) {
			found = false;
		    } 
		} 

		if (found) {
		    return cs[j];
		} 
	    } 
	} 

	return null;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Gets a field (declared or inherited) of this class.
     * @param name the name of the field
     * @return the Field object matching the field name; null if field
     *         is not declared or inherited.  
     */
    public Field getField(String name) {
	if (unreflected) {
	    ClassPool.reflectClass(getName());

	    // field ?
	} 

	for (int j = 0; j < fs.length; j++) {
	    if (fs[j].getName().equals(name)) {
		return fs[j];
	    } 
	} 

	// inherited field ?
	//System.err.println("looking for '" + name + "' in fs_in:");
	if (!inherited) {
	    detectInheritedEntities();
	}
	for (int j = 0; j < fs_in.length; j++) {
	    //System.err.println("fs_in " + fs_in[j].getName());
	    if (fs_in[j].getName().equals(name)) {
		
		return fs_in[j];
	    } 
	} 

	// field of interface ?
	Field[] hfs;

	for (int j = 0; j < is.length; j++) {
	    hfs = is[j].getDeclaredFields();

	    for (int i = 0; i < hfs.length; i++) {
		if (hfs[i].getName().equals(name)) {
		    return hfs[i];
		} 
	    } 
	} 

	return null;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Test if a field is declared or inherited by this class.
     * @param field the Field object for the check.
     * @return the Field object matching the Field; null if field is
     *         not declared or inherited.  
     */
    public Field getField(Field field) {
	if (unreflected) {
	    ClassPool.reflectClass(getName());

	    // field ?
	} 

	for (int j = 0; j < fs.length; j++) {
	    if (fs[j].equals(field)) {
		return fs[j];
	    } 
	} 

	// inherited field ?
	if (!inherited) {
	    detectInheritedEntities();
	}
	for (int j = 0; j < fs_in.length; j++) {
	    if (fs_in[j].equals(field)) {
		return fs_in[j];
	    } 
	} 

	return null;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Gets a field (only declared ones) of this class.
     * @param name the name of the field
     * @return the Field object matching the field name; null if field
     *         is not declared by this class. 
     */
    public Field getDeclaredField(String name) {
	if (unreflected) {
	    ClassPool.reflectClass(getName());

	    // field ?
	} 

	for (int j = 0; j < fs.length; j++) {
	    if (fs[j].getName().equals(name)) {
		return fs[j];
	    } 
	} 

	return null;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * Returns the name of this class. Same as getName.
     * @return the name.
     */
    public String getUnitName() {
	return name;
    } 

    /**
     * Returns null, cause a class is not a method. This method is
     * part of the Context interface.
     * @return null.  
     */
    public String getMethodName() {
	return null;
    } 

    /**
     * Returns null, cause a class has no formal parameters. This
     * method is part of the Context interface.
     * @return null.  
     */
    public FormalParameter getFormalParameter(String name) {
	return null;
    } 

    /**
     * Tests if an entity (field or method) is accessible from an
     * other class.
     * @param m the entity for which the test is invoked.
     * @param c the class which wants to see the entity.
     * @return true if the entity is accessible; false if not.  */
    public boolean showsEntityToClass(Entity m, Class c) {
	/** require m != null; c != null; **/

	// who is c related to this class ?
	// same package ?
	boolean same_package;

	String thisPackage = getPackageName();
	String otherPackage = c.getPackageName();
	if (thisPackage != null && otherPackage != null) {

	    // are the package names equal ?
	    same_package = thisPackage.equals(otherPackage);
	} else {
	    same_package = false;
	}

	// c is subclass ?
	boolean subclass;
	Class   current = c;

	while (current.getSuperclass() != null 
	       &&!current.getName().equals(name)) {
	    current = current.getSuperclass();
	} 

	subclass = current.getName().equals(name);

	if (m.isPublic()) {
	    return true;
	} 

	if (m.isProtected() && (subclass || same_package)) {
	    return true;
	} 

	if (m.isPackage() && same_package) {
	    return true;
	} 

	return false;
    } 

    /**
     * Tests if this class is a subtype of another class.
     * @param c the Class which should be a SUPERtype.
     * @return true if this class is a subtype of c; false otherwise.
     */
    public boolean isSubTypeOf(Class c) {
	if (c == this) {
	    return true;
	} 

	if (getSuperclass() != null) {
	    return getSuperclass().isSubTypeOf(c);
	} else {
	    return false;
	}
    } 

    /**
     * Tests if a given is class assignment compatible with the
     * class/interface represented by this class.  Means: The given
     * class can apear when a class of this type is needed.
     * @param c the class which should apear when this type is needed.
     * @return true if class c is assignment compatible; false otherwise.  */
    public boolean isAssignableFrom(Class c) {
	/** require c != null; **/

	/*
	 * JavaLanguageSpecification:
	 *
	 * From any class type S to any class type T, provided that S
	 * is a subclass of T. (An important special case is that
	 * there is a widening conversion to the class type Object
	 * from any other class type.)
	 *
	 * From any class type S to any interface type K, provided
	 * that S implements K.
	 *	 
	 * From the null type to any class type, interface type, or
	 * array type.
	 *	 
	 * From any interface type J to any interface type K, provided
	 * that J is a subinterface of K.
	 *	 
	 * From any interface type to type Object.
	 *	 
	 * From any array type to type Object.
	 *	 
	 * From any array type to type Cloneable.
	 *	 
	 * From any array type SC[] to any array type TC[], provided
	 * that SC and TC are reference types and there is a widening
	 * conversion from SC to TC.  
	 */
	if (name.equals("java.lang.Object") && c.isReference()) {
	    return true;
	} 

	// / ?????
	if (c == ClassPool.Null && isReference()) {
	    return true;
	} 

	if (c.isArray()) {
	    if (name.equals("java.lang.Cloneable")) {
		return true;
	    } 

	    if (c.getArrayDims() != getArrayDims()) {
		return false;
	    } 

	    return getComponentType().isAssignableFrom(c.getComponentType());
	} 

	if (isInterface()) {
	    return c.doesImplement(this);
	} 

	return c.isSubTypeOf(this);
    } 

    /**
     * Test if this class/interface implements/extends a given interface.
     * @param c the class which should be implemented.
     * @return true if class is implemented; false if not.
     */
    public boolean doesImplement(Class c) {
	/** require c != null; **/
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 

	if (name.equals(c.getName())) {
	    return true;
	} 

	for (int i = 0; i < is.length; i++)
	    /** variant is.length - i **/ {
	    if (is[i].doesImplement(c)) {
		return true;
	    } 
	} 

	if (getSuperclass() != null) {
	    return getSuperclass().doesImplement(c);
	} 

	return false;
	/** ensure !isUnreflected(); **/
    } 

    /**
     * This method determines the inherited entities (fields and
     * methods) of this class.  Method is recursiv an processes the
     * all superclasses too. Method is NOT marked as reflected after
     * this progress! 
     */
    public void detectInheritedEntities() {
	if (getSuperclass() != null) {

	    // first do this for the superclass
	    superClass.detectInheritedEntities();

	    if (!inherited) {
		inherited = true;

		// get the declared and the inherited methods of the
		// superclass
		Method[] super_in = superClass.getInheritedMethods();
		Method[] super_ms = superClass.getDeclaredMethods();

		// class inherites the visible methods, which are not
		// declared by it self
		Vector   in = new Vector();

		// inherited methods:
		for (int i = 0; i < super_in.length; i++) 
		    /** variant super_in.length-i **/ {
		    if (superClass.showsEntityToClass(super_in[i], this) 
			    && getMethod(super_in[i].getIdString()) == null) {
			in.addElement(super_in[i]);
		    } 
		} 

		for (int i = 0; i < super_ms.length; i++) 
		    /** variant super_ms.length-i **/ {
		    if (superClass.showsEntityToClass(super_ms[i], this) 
			    && getMethod(super_ms[i].getIdString()) == null) {
			in.addElement(super_ms[i]);
		    } 
		} 

		ms_in = new Method[in.size()];

		in.copyInto(ms_in);

		Field[] super_fin = superClass.getInheritedFields();
		Field[] super_fs = superClass.getDeclaredFields();
		Vector  fin = new Vector();

		for (int i = 0; i < super_fin.length; i++) 
		    /** variant super_fin.length-i **/ {
		    if (superClass.showsEntityToClass(super_fin[i], this) 
			    && getField(super_fin[i].getName()) == null) {
			fin.addElement(super_fin[i]);
		    } 
		} 

		for (int i = 0; i < super_fs.length; i++) 
		    /** variant super_fs.length-i **/ {
		    if (superClass.showsEntityToClass(super_fs[i], this) 
			    && getField(super_fs[i].getName()) == null) {
			fin.addElement(super_fs[i]);
		    } 
		} 

		fs_in = new Field[fin.size()];

		fin.copyInto(fs_in);
	    } 
	} 
    } 

    /**
     * This method initiates the reflection process for this class.
     * All components (invariant, methods, expressions, ...) are
     * reflected too. (Fields have nothing to reflect.)  Semantic
     * checks for all this components are made. This method is called
     * by the ClassPool.  */
    public void reflect() {

	// System.out.println(name + " is being reflected");
	if (inv != null) {
	    inv.reflectExpressions();
	} 

	if (!inherited) {
	    detectInheritedEntities();

	    // reflect informations about the methods (+semantic check) ...
	    // make semantic checks for refinement ...
	} 

	if (doesImplement(ClassPool.getClass("jass.runtime.Refinement"))) {
	    Class superclass = getSuperclass();

	    if (superclass != null) {
		if (NameAnalysis.isJass(superclass.getName())) {
		    Method gss = getMethod("jassGetSuperState()");

		    if (gss == null || gss.getReturnType() != superclass) {
			throw new ReflectionError(name 
						  + " <Class signals a refinement, but does not implement method 'jassGetSuperState()' with return type " 
						  + superclass.getName() 
						  + ".>");
		    } 

		    refines = superclass;

		    if (GlobalFlags.DEPEND) {
			ClassPool.addClassToCompile(superclass);
		    } 

		    Invariant super_inv = refines.getInvariant();

		    if (super_inv != null && inv == null) {
			throw new ReflectionError(name 
						  + " <Class signals a refinement, but does not declare an invariant while superclass does.>");
		    } 

		    if (super_inv != null) {
			super_inv.reflectExpressions();
			inv.setRefinedInvariant(super_inv);
			
			// check access
			Object[] reads = super_inv.getReads().elements();
			
			for (int i = 0; i < reads.length; i++) {
			    if (reads[i] instanceof Field) {
				if (!superclass.showsEntityToClass((Field) reads[i], 
								   this)) {
				    throw new ReflectionError(name + " <Field " 
							      + ((Field) reads[i]).getName() 
							      + " of class " 
							      + superclass.getName() 
							      + " must be accessible from this class for refinement.>");
				} 
			    } 
			} 
			
			Object[] calls = super_inv.getCalls().elements();
			
			for (int i = 0; i < calls.length; i++) {
			    if (!superclass.showsEntityToClass((Method) calls[i], 
							       this)) {
				throw new ReflectionError(name + " <Method " 
							  + ((Method) calls[i]).getIdString() 
							  + " of class " 
							  + superclass.getName() 
							  + " must be accessible from this class for refinement.>");
			    } 
			} 
		    }
		} else {
		    throw new ReflectionError(name 
					      + " <Class signals a refinement, but superclass " 
					      + superclass.getName() 
					      + " has no jass source.>");
		} 
	    } 

	    // This can not happen, cause all classes have superclass Object and Object cannot refine something ...
	    else {
		System.err.println("Fatal internal error: Class " + name 
				   + " signals a refinement, but has no superclass!");
		System.exit(1);
	    } 
	} 

	// reflect methods
	for (int i = 0; i < ms.length; i++) {
	    ms[i].reflect();
	} 

	// reflect methods
	for (int i = 0; i < ms_in.length; i++) {
	    ms_in[i].reflect();
	} 

	// reflect constructors
	for (int i = 0; i < cs.length; i++) {
	    cs[i].reflect();
	} 

	// reflect expressions
	for (int i = 0; i < expr.size(); i++) {
	    ((Expression) expr.elementAt(i)).reflectExpression(this, dc);
	} 

	/*
	 * MethodCallExpression[] methodCalls
	 * = new MethodCallExpression[primaryNodeToMethodCall.size()];
	 * 
	 * primaryNodeToMethodCall.values().toArray(methodCalls);
	 * for(int iMethod = 0; iMethod < methodCalls.length; ++iMethod)
	 * {
	 * MethodCallExpression expression = methodCalls[iMethod];
	 * expression.reflectExpression(expression.getContainer(), new DependencyCollector());
	 * }
	 */

	// IMPORTANT
	// Reflection of Trace Assertions must be after class reflection.
	// This is, because a trace assertion collects many class informations
	// which can not be retrieved when the class is unreflected.
	if (traceAssertion != null) {
	    traceAssertion.reflectExpressions();
	} 

	// System.out.println(name + " reflected");
	setUnreflected(false);
    } 

    /**
     * Test if class represents a numeric type.
     * @return false, cause class is of reference type.
     */
    public boolean isNumeric() {
	return false;
    } 

    /**
     * Test if class represents a integral type.
     * @return false, cause class is of reference type.
     */
    public boolean isIntegral() {
	return false;
    } 

    /**
     * Test if class represents a boolean type.
     * @return false, cause class is of reference type.
     */
    public boolean isBoolean() {
	return false;
    } 

    /**
     * Test if class represents a primitive type. Primitive types have
     * special classes.
     * @return false, cause class is of reference type.  
     */
    public boolean isPrimitive() {
	return false;
    } 

    /**
     * Test if class represents a reference type.
     * @return true, cause class is of reference type.
     */
    public boolean isReference() {
	return true;
    } 

    /**
     * Test if class represents the empty type "void".
     * @return false, cause class is of reference type and not void.
     */
    public boolean isVoid() {
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
    public String toJava() {
	return ClassPool.arrayToTypeName(getName());
    } 

    /**
     * Produces a simple description string.
     */
    public String toString() {
	String superClassStr = (superClass != null) ? 
	    "<" + superClass.toString() + ">" : "";
	if (unreflected) {
	    return "[unreflected] " + name + superClassStr;
	} else {
	    return name + superClassStr;
	}
    } 

    /**
     * Gets the refined class (the superclass).
     * @return the refined class if a refinement is signaled, null otherwise;
     */
    public Class getRefinedClass() {

	/* new */
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 

	return refines;
	/**  ensure !isUnreflected(); **/
    } 

    /**
     * This methods adds an expression apearing in an initialiser of
     * this class.  The expression is added if the ANALYSE_DATAFLOW
     * option is set.
     * @see jass.GlobalFlags
     * @param e the expression 
     */
    public void addExpression(Expression e) {
	/** require e != null; **/
	if (GlobalFlags.ANALYSE_DATAFLOW) {
	    expr.addElement(e);
	} 
    } 

    /**
     * Sets the forall/exists expressions.
     * @param v a vector containing Expression objects.
     * @see jass.reflect.Expression
     */
    public void setForAll_Exists(Vector v) {
	/** require v != null; **/
	forall_exists = new Expression[v.size()];

	v.copyInto(forall_exists);
    } 

    /**
     * Returns the forall/exists expressions.
     */
    public Expression[] getForAll_Exists() {
	return forall_exists;
    } 

    /**
     * Returns the dataflow information of this class.
     * @return the dataflow info
     */
    public DependencyCollector getDependencies() {
	if (unreflected) {
	    ClassPool.reflectClass(name);
	} 

	return dc;
    } 


    /**
     * decide if we have to generate methods or not. The generation may be
     * turned off by the global options.
     * @param assertion the assertion to test
     * @return true, if global flags allow to generate that kind of 
     *         assertion, otherwise returns false.
     */
    private boolean generateAssertionFor (Assertion assertion) {
	if (assertion instanceof Precondition) {
	    return GlobalFlags.GENERATE_PRE;
	} else if (assertion instanceof Postcondition) {
	    return GlobalFlags.GENERATE_POST;
	} else if (assertion instanceof LoopInvariant
		   || assertion instanceof LoopVariant) {
	    return GlobalFlags.GENERATE_LOOP;
	} else if (assertion instanceof Check) {
	    return GlobalFlags.GENERATE_CHECK;
	} else {
	    // this makes it equal to the previous code. Is it ok? ???
	    return false;
	}
    }

    /**
     * This method returns a set of the methods that are called in the
     * assertions of the class.  The method is used by the
     * OutputVisitor to generate "internal" methods. The result varies
     * with the compile options.
     * @return a Set object representing the needed informations.
     * @see jass.util.Set
     * @see jass.visitor.OutputVisitor 
     */
    public Set callsInAssertions() {
	if (unreflected) {
	    ClassPool.reflectClass(getName());
	} 

	Set set = new Set();

	if (inv != null && GlobalFlags.GENERATE_INV) {
	    set.union(inv.getCalls());
	} 

	for (int i = 0; i < cs.length; i++) {
	    BooleanAssertion[] as = cs[i].getAssertions();

	    for (int j = 0; j < as.length; j++) {

		// must methods be generated or is the assertion type
		// "switched off" ?
		if (generateAssertionFor(as[j])) {
		    set.union(as[j].getInternalCalls());
		} 
	    }
	} 

	for (int i = 0; i < ms.length; i++) {
	    BooleanAssertion[] as = ms[i].getAssertions();

	    for (int j = 0; j < as.length; j++) {

		// must methods be generated or is the assertion type
		// "switched off" ?
		if (generateAssertionFor(as[j])) {
		    set.union(as[j].getInternalCalls());
		} 
	    }
	} 

	for (int i = 0; i < ms_in.length; i++) {
	    BooleanAssertion[] as = ms_in[i].getAssertions();

	    for (int j = 0; j < as.length; j++) {

		// must methods be generated or is the assertion type
		// "switched off" ?
		if (generateAssertionFor(as[j])) {
		    set.union(as[j].getInternalCalls());
		} 
	    }
	} 

	return set;
    } 

    /**
     * Method declaration
     *
     *
     * @param es
     *
     * @see
     */
    private void setContainer(Entity[] es) {
	for (int i = 0; i < es.length; i++) {
	    es[i].setContainer(this);
	} 
    } 

    /**
     * Simply dumps some informations about this class to a
     * PrintWriter with given indent. Method is use for debugging.
     * @param pw the PrintWriter.
     * @param indent the first level indent of the output.  
     */
    public void dump(java.io.PrintWriter pw, String indent) {
	/** require pw != null; **/
	ClassPool.classCount++;

	String modis = jass.reflect.Modifier.toString(modifiers);

	pw.print(indent + modis + (isInterface ? " interface " : " class ") 
		 + name);

	if (superClass != null) {
	    pw.print(" extends " + superClass.getName());
	} 

	if (is.length > 0) {
	    if (isInterface()) {
		pw.println(" extends");
	    } else {
		pw.println(" implements");
	    }

	    pw.print(indent + "   ");

	    boolean first = true;

	    for (int i = 0; i < is.length; i++) {
		if (!first) {
		    pw.print(",");
		} else {
		    first = false;
		}

		pw.print(is[i]);
	    } 
	} 

	if (inv != null) {
	    pw.println();
	    pw.print(indent + "   ");
	    pw.print("invariant: " + inv);
	} 

	pw.println();

	if (fs.length > 0) {
	    pw.println(indent + "   --- field(s) ---");

	    for (int i = 0; i < fs.length; i++) {
		ClassPool.fieldCount++;

		pw.println(indent + "   " + fs[i]);
	    } 
	} 

	if (ms.length > 0) {
	    pw.println(indent + "   --- method(s) ---");

	    for (int i = 0; i < ms.length; i++) {
		ClassPool.methodCount++;

		pw.print(indent + ms[i].getIdString());
		pw.println();
	    } 
	} 

	if (fs_in.length > 0) {
	    pw.println(indent + "   --- inherited field(s) ---");

	    for (int i = 0; i < fs_in.length; i++) {
		pw.println(indent + "   " + fs_in[i]);
	    } 
	} 

	if (ms_in.length > 0) {
	    pw.println(indent + "   --- inherited method(s) ---");

	    for (int i = 0; i < ms_in.length; i++) {
		pw.print(indent + ms[i].getIdString());
		pw.println();
	    } 
	} 
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

