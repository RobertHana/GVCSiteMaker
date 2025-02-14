/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.util.Vector;
import jass.util.Set;
import jass.GlobalFlags;

/**
 * This class wraps a Java method. It is an analogon to
 * java.lang.reflect.Method but the informations can be modified by
 * Java programms, not only by the virtual machine.
 * @see java.lang.reflect.Method 
 */
public class Method extends Entity implements Context {
    protected String[]		  exceptionTypes = new String[0];
    protected FormalParameter[]   formalParameters = new FormalParameter[0];
    protected BooleanAssertion[]  assertions = 
	new BooleanAssertion[0];    // better, less null pointer tests !
    protected DependencyCollector dc = new DependencyCollector();
    protected Vector		  expr = new Vector();
    protected FormalParameter[]   rescues = new FormalParameter[0];
    protected Boolean[]		  retries = new Boolean[0];
    protected boolean		  reflected = false;
    protected boolean		  returnInLoop = false;
    protected String		  idString = null;

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public Method() {}

    /**
     * This init method allows the mapping of a reflection method
     * produced by the VM to this reflection method.  All nessesary
     * informations are copied.
     * @param m the method to map.  
     */
    public void init(java.lang.reflect.Method m) {
	java.lang.Class[] pt = m.getParameterTypes();

	formalParameters = new FormalParameter[pt.length];

	for (int i = 0; i < pt.length; i++) {
	    formalParameters[i] = new FormalParameter();

	    formalParameters[i].init(pt[i]);
	} 

	modifiers = Modifier.convert(m.getModifiers());
	name = m.getName();
	type = ClassPool.getClass(m.getReturnType().getName());

	java.lang.Class[] exceptionClasses = m.getExceptionTypes();

	exceptionTypes = new String[exceptionClasses.length];

	for (int iException = 0; iException < exceptionTypes.length; 
		++iException) {
	    exceptionTypes[iException] = 
		exceptionClasses[iException].getName();
	} 
    } 

    /**
     * Returns the Class object representing the class or interface
     * that declares the method represented by this Method object.
     * @return the declaring class 
     */
    public Class getDeclaringClass() {
	return (Class) container;
    } 

    /**
     * Returns an array of Strings decribing the exceptions thrown by
     * this method.
     * @return the exceptions 
     */
    public String[] getExceptionTypes() {
	return exceptionTypes;
    } 

    /**
     * Returns a Class object that represents the formal return type
     * of the method represented by this Method object.
     * @return the return type 
     */
    public Class getReturnType() {
	return type;
    } 

    /**
     * Returns an array of the delcared assertions in this method.
     * @retrun the assertions. An array of length zero, if no
     *         assertions are declared.  
     */
    public BooleanAssertion[] getAssertions() {
	return assertions;
    } 

    /**
     * Sets the Assertions declared by this method.
     * @param a vector containing the assertions (type:
     *          jass.reflect.BooleanAssertion) 
     */
    public void setAssertions(Vector asserts) {
	/** require asserts != null; **/
	assertions = new BooleanAssertion[asserts.size()];
	asserts.copyInto(assertions);
    } 

    /**
     * Sets the Assertions declared by this method.
     * @param an array containing the assertions
     */
    public void setAssertions(BooleanAssertion[] asserts) {
	/** require asserts != null; **/
	assertions = asserts;
    } 

    /**
     * Adds an assertion to the assertions declared by this method.
     * @param a the assertion to add
     */
    public void addAssertion(BooleanAssertion a) {
	a.setContainer(this);

	// get help structure
	BooleanAssertion[] assertions_help = 
	    new BooleanAssertion[assertions.length + 1];

	// copy
	System.arraycopy(assertions, 0, assertions_help, 0, 
			 assertions.length);

	assertions = assertions_help;

	// assign a to last field
	assertions[assertions.length - 1] = a;

	// explicite carbage collect of help structure
	assertions_help = null;
    } 

    /**
     * Return if this method declares assertions.
     * @return true if method has assertions; false otherwise.
     */
    public boolean hasAssertion() {
	return assertions.length > 0;
    } 

    /**
     * Returns the precondition of this method.
     * @return the precondition if declared; null otherwise
     */
    public Precondition getPrecondition() {
	for (int i = 0; i < assertions.length; i++) {
	    if (assertions[i] instanceof Precondition) {
		return (Precondition) assertions[i];
	    } 
	} 

	return null;
    } 

    /**
     * Returns the postcondition of this method.
     * @return the postcondition if declared; null otherwise
     */
    public Postcondition getPostcondition() {
	for (int i = 0; i < assertions.length; i++) {
	    if (assertions[i] instanceof Postcondition) {
		return (Postcondition) assertions[i];
	    } 
	} 

	return null;
    } 

    /**
     * Returns a loop invariant with given loop number.
     * @param nr the loop number
     * @return the invariant if declared with this number; null otherwise
     */
    public LoopInvariant getLoopInvariant(int nr) {
	for (int i = 0; i < assertions.length; i++) {
	    if (assertions[i] instanceof LoopInvariant 
		    && ((LoopInvariant) assertions[i]).getLoopNumber() 
		       == nr) {
		return (LoopInvariant) assertions[i];
	    } 
	} 

	return null;
    } 

    /**
     * Returns if this method has a loop invariant
     * @return true if method declares a loop invariant
     */
    public boolean hasLoopInvariant() {
	for (int i = 0; i < assertions.length; i++) {
	    if (assertions[i] instanceof LoopInvariant) {
		return true;
	    } 
	} 

	return false;
    } 

    /**
     * Returns a loop variant with given loop number.
     * @param nr the loop number
     * @return the variant if declared with this number; null otherwise
     */
    public LoopVariant getLoopVariant(int nr) {
	for (int i = 0; i < assertions.length; i++) {
	    if (assertions[i] instanceof LoopVariant 
		    && ((LoopVariant) assertions[i]).getLoopNumber() == nr) {
		return (LoopVariant) assertions[i];
	    } 
	} 

	return null;
    } 

    /**
     * Returns a check assertion with given number.
     * @param nr the check assertion number
     * @return the assertion if declared with this number; null otherwise
     */
    public Check getCheck(int nr) {
	for (int i = 0; i < assertions.length; i++) {
	    if (assertions[i] instanceof Check 
		    && ((Check) assertions[i]).getCheckNumber() == nr) {
		return (Check) assertions[i];
	    } 
	} 

	return null;
    } 

    /**
     * Sets the return type of this method.
     * @param _type the return type
     */
    public void setReturnType(Class _type) {
	type = _type;
    } 

    /**
     * Sets the declaring class of this method.
     * @param c the declaring class
     */
    public void setDeclaringClass(jass.reflect.Class c) {
	container = c;
    } 

    /**
     * Sets the exception types of this method. This are strings, no
     * wrapper objects.
     * @param v a vector of strings describing the exceptions.  
     */
    public void setExceptionTypes(java.util.Vector v) {
	/** require v != null; **/
	exceptionTypes = new String[v.size()];
	v.copyInto(exceptionTypes);
    } 

    /**
     * Sets the formal parameters of this method.
     * @param v a vector of formal parameters (type:
     *          jass.reflect.FormalParameter) 
     */
    public void setFormalParameters(java.util.Vector v) {
	/** require v != null; **/
	formalParameters = new FormalParameter[v.size()];
	v.copyInto(formalParameters);

	for (int i = 0; i < formalParameters.length; i++) {
	    formalParameters[i].setContainer(this);
	} 
    } 

    /**
     * Gets the formal parameters.
     * @return the formal parameters
     */
    public FormalParameter[] getFormalParameters() {
	return formalParameters;
    } 

    /**
     * Returns an id string for this method. This contains the method
     * name and the formal parameters. Used to identifiy the methods
     * signature.
     * @return the id string 
     */
    public String getIdString() {
	if (idString != null) {
	    return idString;
	} 

	idString = name + toString(formalParameters);

	return idString;
    } 

    /**
     * Method declaration
     *
     *
     * @param name
     * @param parameter
     *
     * @return
     *
     * @see
     */
    public static String getIdString(String name, Class[] parameter) {
	return name + toString(parameter);
    } 

    /**
     * Reflects this method. All assertions and expressions are
     * reflected. Some semantic checks for refinement are done.  
     */
    public void reflect() {
	if (!reflected) {
	    if (GlobalFlags.VERBOSE > 2) {
		System.out.println("Message: Reflecting method " 
				   + ((Class) container).getName() + "." 
				   + getIdString() + " ...");
	    } 

	    reflected = true;

	    String error_id = ((Class) container).getName() + "." 
			      + getIdString();
	    Class  refines = ((Class) container).getRefinedClass();

	    for (int i = 0; i < assertions.length; i++) {
		if (assertions[i] instanceof Precondition) {
		    assertions[i].reflectExpressions();

		    if (assertions[i].getAssertionExpressions().length > 0) {

			// refines c his superclass and overwrittes this method a method in the superclass ?
			if (refines != null 
				&& refines.getMethod(getIdString()) != null) {
			    Precondition super_pre = 
				refines.getMethod(getIdString()).getPrecondition();

			    if (super_pre == null) {
				throw new ReflectionError(error_id 
							  + " <There must be a precondition declared in class " 
							  + refines.getName() 
							  + " for refinement.>");
			    } 

			    if (super_pre.getAssertionExpressions().length 
				    == 0) {
				throw new ReflectionError(error_id 
							  + " <Precondition declared in class " 
							  + refines.getName() 
							  + " contains no assertion expressions.>");
			    } 

			    super_pre.reflectExpressions();
			    ((Precondition) assertions[i]).setRefinedPrecondition(super_pre);

			    // check access
			    Object[] reads = super_pre.getReads().elements();

			    for (int k = 0; k < reads.length; k++) {
				if (reads[k] instanceof Field) {
				    if (!refines.showsEntityToClass((Field) reads[k], 
								    (Class) container)) {
					throw new ReflectionError(error_id 
								  + " <Field " 
								  + ((Field) reads[k]).getName() 
								  + " of class " 
								  + refines.getName() 
								  + " must be accessible from this class for refinement.>");
				    } 
				} 
			    } 

			    Object[] calls = super_pre.getCalls().elements();

			    for (int k = 0; k < calls.length; k++) {
				if (!refines.showsEntityToClass((Method) calls[k], 
								(Class) container)) {
				    throw new ReflectionError(error_id 
							      + " <Method " 
							      + ((Method) calls[k]).getIdString() 
							      + " of class " 
							      + refines.getName() 
							      + " must be accessible from this class for refinement.>");
				} 
			    } 
			} 
		    } 
		} 

		if (assertions[i] instanceof Postcondition) {
		    assertions[i].reflectExpressions();

		    // refines c his superclass and overwrittes this method a method in the superclass ?
		    if (refines != null 
			    && refines.getMethod(getIdString()) != null) {
			Postcondition super_post = 
			    refines.getMethod(getIdString()).getPostcondition();

			/*
			 * if (super_post == null)
			 * throw new ReflectionError((Class)container+"."+getIdString()+" <There must be a postcondition declared in class "+refines.getName()+" for refinement.>");
			 */
			if (assertions[i].getAssertionExpressions().length 
				== 0 && super_post != null 
				     && super_post.getAssertionExpressions().length 
					> 0) {
			    throw new ReflectionError(error_id 
						      + " <Postcondition contains no assertion expressions, but must strengthen the postcondition in class " 
						      + refines.getName() 
						      + ".>");
			} 

			if (super_post != null) {
			    super_post.reflectExpressions();
			    ((Postcondition) assertions[i]).setRefinedPostcondition(super_post);

			    Object[] reads = super_post.getReads().elements();

			    for (int k = 0; k < reads.length; k++) {
				if (reads[k] instanceof Field) {
				    if (!refines.showsEntityToClass((Field) reads[k], 
								    (Class) container)) {
					throw new ReflectionError(error_id 
								  + " <Field " 
								  + ((Field) reads[k]).getName() 
								  + " of class " 
								  + refines.getName() 
								  + " must be accessible from this class for refinement.>");
				    } 
				} 
			    } 

			    Object[] calls = super_post.getCalls().elements();

			    for (int k = 0; k < calls.length; k++) {
				if (!refines.showsEntityToClass((Method) calls[k], 
								(Class) container)) {
				    throw new ReflectionError(error_id 
							      + " <Method " 
							      + ((Method) calls[k]).getIdString() 
							      + " of class " 
							      + refines.getName() 
							      + " must be accessible from this class for refinement.>");
				} 
			    } 
			} 
		    } 
		} 

		if (assertions[i] instanceof LoopInvariant 
			|| assertions[i] instanceof LoopVariant 
			|| assertions[i] instanceof Check) {
		    assertions[i].reflectExpressions();
		} 
	    } 

	    for (int i = 0; i < expr.size(); i++) {
		((Expression) expr.elementAt(i)).reflectExpression(this, dc);
	    } 

	    for (int i = 0; i < rescues.length; i++) {
		if (!rescues[i].getType().isSubTypeOf(ClassPool.getClass("jass.runtime.AssertionException"))) {
		    throw new ReflectionError(error_id 
					      + " <Exception catched in rescue clause must be a subclass of jass.runtime.AssertionException: " 
					      + rescues[i].getType() + ".>");
		} 
	    } 

	    if (GlobalFlags.VERBOSE > 2) {
		System.out.println("Message: Ended reflecting " 
				   + ((Class) container).getName() + "." 
				   + getIdString() + ".");
	    } 
	} 
    } 

    /**
     * A debbuging method. Not longer used.
     */
    public void dump(java.io.PrintWriter pw, String indent) {
	String modis = jass.reflect.Modifier.toString(modifiers);

	// System.out.println(modifiers);
	pw.print(indent + modis + " ");
	pw.print(type.getName());
	pw.print(" " + name + "(");

	if (formalParameters.length > 0) {
	    boolean first = true;

	    for (int i = 0; i < formalParameters.length; i++) {
		if (!first) {
		    pw.print(",");
		} else {
		    first = false;
		}

		pw.print(formalParameters[i]);
	    } 
	} 

	pw.print(")");

	if (exceptionTypes.length > 0) {
	    boolean first = true;

	    pw.println(" throws");
	    pw.print(indent + "   ");

	    for (int i = 0; i < exceptionTypes.length; i++) {
		if (!first) {
		    pw.print(",");
		} else {
		    first = false;
		}

		pw.print(exceptionTypes[i]);
	    } 
	} 

	if (assertions.length > 0) {
	    boolean first = true;

	    pw.println();
	    pw.println(indent + "   assertions:");

	    for (int i = 0; i < assertions.length; i++) {
		if (!first) {
		    pw.println();
		} else {
		    first = false;
		}

		pw.print(indent + "      " + assertions[i]);

		// ((Postcondition)assertions[i]).detReadFields().dump(pw,indent+"      ");
	    } 
	} 

	dc.dump(pw, indent + "    ");
    } 

    // implements Context

    /**
     * Returns the name of the unit this method is declared in.
     * @return the name ogf the unit
     */
    public String getUnitName() {
	return container.getName();
    } 

    /**
     * Method declaration
     *
     *
     * @param methodname
     *
     * @return
     *
     * @see
     */
    public static String getName(String methodname) {
	String identifier;
	int    beginOfName = methodname.lastIndexOf(".") + 1;

	identifier = beginOfName == -1 ? "" 
		     : methodname.substring(beginOfName, methodname.length());

	return identifier;
    } 

    /**
     * Method declaration
     *
     *
     * @param methodname
     *
     * @return
     *
     * @see
     */
    public static String getUnitName(String methodname) {
	String  identifier;
	int     endOfName = methodname.lastIndexOf(".");
	boolean hasName = endOfName != -1;

	identifier = (hasName ? methodname.substring(0, endOfName) : "");

	return identifier;
    } 

    /*
     * public String getIdentifier()
     * {
     * return separateIdentifier(name);
     * }
     * public String getClassIdentifier()
     * {
     * return separateClassIdentifier(name);
     * }
     * 
     * public static String separateIdentifier(String methodname)
     * {
     * String identifier;
     * identifier = methodname.substring(methodname.lastIndexOf(".") + 1, methodname.length());
     * return identifier;
     * }
     * 
     * public static String separateClassIdentifier(String methodname)
     * {
     * String identifier;
     * identifier = methodname.substring(0, methodname.lastIndexOf("."));
     * return identifier;
     * }
     */

    /**
     * Returns the name of this method. (Is the same as getIdString!)
     * @return the id string
     */
    public String getMethodName() {
	return getIdString();
    } 

    /**
     * Return the declared fields of the class of this method. Used for context search of names.
     * @return the declared fields.
     */
    public Field[] getDeclaredFields() {
	return ((Class) container).getDeclaredFields();
    } 

    /**
     * Return a formal parameter for a given name
     * @param name the name of the parameter
     * @return the formal parametr of this name; null if no fp of this
     *         name exists 
     */
    public FormalParameter getFormalParameter(String name) {
	for (int j = 0; j < formalParameters.length; j++) {
	    if (formalParameters[j].getName().equals(name)) {
		return formalParameters[j];
	    } 
	} 

	return null;
    } 

    /**
     * Returns a method for given name and formal parameters of the
     * class this method is declared in.  Used for context search of
     * names.
     * @param name the method name
     * @param params an array of the params
     * @return the method specified by the parameters; null if method
     *         does not exists in declaring class 
     */
    public Method getMethod(String name, Class[] params) {
	return ((Class) container).getMethod(name, params);
    } 

    /**
     * Returns a field for given name of the class this method is declared in.
     * Used for context search of names.
     * @param name the field name
     * @return the field specified by the name; null if field does not
     *         exists in declaring class 
     */
    public Field getField(String name) {
	return ((Class) container).getField(name);
    } 

    /**
     * Tests if a given exists in the class this method is declared in.
     * Used for context search of names.
     * @param field the field
     * @return the field if exists; null if field does not exists in
     *         declaring class 
     */
    public Field getField(Field field) {
	return ((Class) container).getField(field);
    } 

    /**
     * Adds an expression for dataflow analysis. Expression is only
     * added if DATAFLOW is aktiv.
     * @param e the expression 
     */
    public void addExpression(Expression e) {
	if (GlobalFlags.ANALYSE_DATAFLOW) {
	    expr.addElement(e);
	} 
    } 

    /**
     * Returns the variables read by this method. The returned Set can
     * contain instances of LocalVariable, Field and FormalParameter.
     * @return a Set of variables read by this method. The empty set
     *         if there are no read fields or the method has not been
     *         reflected.  
     */
    public Set getReads() {
	return dc.getReads();
    } 

    /**
     * Returns the variables written by this method. The returned Set
     * can contain instances of LocalVariable, Field and
     * FormalParameter.
     * @return a Set of variables written by this method. Returns the
     *         empty set if there are no written variables or the
     *         method has not been reflected. 
     */
    public Set getWrites() {
	return dc.getWrites();
    } 

    /**
     * Returns instances created by this method. The returned Set can
     * contain instances of Creation.
     * @return a Set of creation objects, representing the instances
     *         this method does created.  The empty set is returned if
     *         there are no instance creations or the method has not
     *         been reflected. 
     */
    public Set getCreates() {
	return dc.getCreates();
    } 

    /**
     * Returns the methods called by this method. The returned Set can
     * contain instances of Method.
     * @return a Set of methods called this method. Returns the empty
     *         set if there are no called methods or the method has
     *         not been reflected.  */
    public Set getCalls() {
	return dc.getCalls();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isNative() {
	return Modifier.isNative(modifiers);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isAbstract() {
	return Modifier.isAbstract(modifiers);
    } 

    /**
     * Returns if the method is declared as static.
     * @return true if method is static; false otherwise
     */
    public boolean isStatic() {
	return Modifier.isStatic(modifiers);
    } 

    /**
     * Gets the number of a given formal parameter.
     * @param name the name of the parameter
     * @return the number of the fp (starting by zero); -1 if no
     *         formal parameter with this name is declared 
     */
    public int getFormalParameterNumber(String name) {
	for (int j = 0; j < formalParameters.length; j++) {
	    if (formalParameters[j].getName().equals(name)) {
		return j;
	    } 
	} 

	return -1;
    } 

    /**
     * Gets aformal parameter with given number.
     * @param i the number (starting by zero)
     * @return the fp; null if there is no fp with this number
     */
    public FormalParameter getFormalParameter(int i) {
	if (0 <= i && i < formalParameters.length) {
	    return formalParameters[i];
	} else {
	    return null;
	}
    } 

    /**
     * Sets the 'rescued' exceptions (declared by formal parameters)
     * of this method.
     * @param v a vector containing objects of type FormalParameter
     *          representing the 'rescues' 
     */
    public void setRescues(Vector v) {
	/** require v != null; **/
	rescues = new FormalParameter[v.size()];
	v.copyInto(rescues);
    } 

    /**
     * Returns if this method has a rescue clause.
     * @return true if method has a rescue clause; false otherwise
     */
    public boolean hasRescueClause() {
	return rescues.length > 0;
    } 

    /**
     * Returns the  'rescues' of this method.
     * @return an array containing FormalParameters representing the
     *         rescues; array of length zero if no rescues are declared 
     */
    public FormalParameter[] getRescues() {
	return rescues;
    } 

    /**
     * Returns the Dependencies of this method. Method will be
     * reflected if not already done.
     * @return the data flow informations 
     */
    public DependencyCollector getDependencies() {
	if (!reflected) {
	    reflect();
	} 

	return dc;
    } 

    /**
     * A description string. (class and id string)
     */
    public String toString() {
	return ((Class) container).getName() + "." + getIdString();
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
    public static String toString(FormalParameter[] parameters) {
	Class[] classes = new Class[parameters.length];

	for (int iParameter = 0; iParameter < parameters.length; 
		++iParameter) {
	    classes[iParameter] = parameters[iParameter].getType();
	} 

	return toString(classes);
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
    public static String toString(Class[] parameters) {
	if (parameters == null) {
	    return "()";
	} 

	StringBuffer sb = new StringBuffer();

	sb.append("(");

	boolean first = true;

	for (int i = 0; i < parameters.length; i++) {
	    if (!first) {
		sb.append(",");
	    } else {
		first = false;
	    }

	    sb.append(ClassPool.arrayToTypeName(parameters[i].getName()));
	} 

	sb.append(")");

	return sb.toString();
    } 

    /**
     * Indicates which rescue contains a retry.
     * @param v a vector containg instance of class java.lang.Boolean,
     *          stating which rescue has a retry.  
     */
    public void setRetries(Vector v) {
	/** require v != null; **/
	retries = new Boolean[v.size()];
	v.copyInto(retries);
    } 

    /**
     * Gets an array indicating which rescue has a retry.
     */
    public Boolean[] getRetries() {
	return retries;
    } 

    /**
     * Returns if this method has retries.
     */
    public boolean hasRetry() {
	return retries.length > 0;
    } 

    /**
     * Returns if this method has a return statement in a loop.
     */
    public boolean hasReturnInLoop() {
	return returnInLoop;
    } 

    /**
     * Sets if this method has a return in a loop.
     */
    public void setReturnInLoop(boolean b) {
	returnInLoop = b;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

