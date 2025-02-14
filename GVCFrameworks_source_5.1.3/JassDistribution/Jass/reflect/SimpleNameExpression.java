/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.PrintWriter;
import jass.util.Set;
import jass.reflect.Class;

/**
 * This represents an simple reference. Something like: field
 * @see Expression
 *
 * @author Detlef Bartetzko
 * @version %I%, %G%
 */
public class SimpleNameExpression extends Expression {
    protected String	    name;

    /**
     * the local variables of this context
     */
    protected jass.util.Set locals;

    /**
     * other locals declared by forall/exists, not 'real' locals
     */
    protected jass.util.Set known;

    // next is used for context changes ...
    protected boolean       formpar = false;

    /**
     * Constructor declaration
     *
     *
     * @param name
     *
     * @see
     */
    public SimpleNameExpression(String name) {
	this.name = name;
    }

    /**
     * Constructor declaration
     *
     *
     * @param name
     * @param line
     * @param c
     *
     * @see
     */
    public SimpleNameExpression(String name, int line, Class c) {
	this.name = name;
	this.line = line;
	this.container = c;
    }

    /**
     * Method declaration
     *
     *
     * @param s
     *
     * @see
     */
    public void setName(String s) {
	name = s;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getName() {
	return name;
    } 

    /**
     * Method declaration
     *
     *
     * @param c
     *
     * @see
     */
    public void setContainer(Class c) {
	container = c;
    } 

    /**
     * Method declaration
     *
     *
     * @param locals
     *
     * @see
     */
    public void setLocals(Set locals) {
	this.locals = locals;
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
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toString());
	} 

	if (name.equals("Old") || name.equals("Result")) {
	    sb.append("jass" + name);
	} else {
	    sb.append(name);
	}

	return sb.toString();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String toHTML() {

	// print out children (can only be CastExpression)
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toHTML());
	} 

	sb.append(name);

	return sb.toString();
    } 

    /**
     * Method declaration
     *
     *
     * @param m_old the old Method context
     * @param m_new the new Method context
     * @param ref
     * @param referenced
     *
     * @return
     *
     * @see
     */
    public String toStringWithChangedContext(Method m_old, Method m_new, 
					     String ref, boolean referenced) {

	// print out children (can only be CastExpression)
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toStringWithChangedContext(m_old, m_new, 
		    ref, referenced));
	} 

	// rename a formal parameter
	if (formpar) {
	    int number = m_old.getFormalParameterNumber(name);

	    sb.append(m_new.getFormalParameter(number).getName());
	} else {

	    // ?????????? Achtung Old und Result in Nachbedingung ! jassGetSuperState definiert
	    if (name.equals("Old")) {
		sb.append("jassOld.jassGetSuperState()");
	    } else {
		if (name.equals("Result")) {
		    sb.append("jassResult");
		} else {
		    sb.append((referenced ? "" : ref + ".") + name);
		}
	    } 
	} 

	return sb.toString();
    } 

    /**
     * Method declaration
     *
     *
     * @param pw
     * @param indent
     *
     * @see
     */
    public void dump(PrintWriter pw, String indent) {
	pw.println(indent + "SimpleName: [" + type + "] " + name);

	for (int i = 0; i < children.length; i++) {
	    children[i].dump(pw, indent + "  ");
	} 
    } 

    /**
     * return the name as idString
     */
    public String getIdString() {
	return name;
    } 

    /**
     * Reflect this SimpleExpression.
     *
     * @param context the context where to reflect this expression,
     *                e.g. Method or Class.
     * @param dc      the DependencyCollector, to store access
     */
    public void reflectExpression(Context context, DependencyCollector dc) {
	boolean reflected;
	if (context instanceof Method) {
	    reflected = reflectMethodExpression((Method)context, dc);
	    if (reflected) {
		return;
	    }
	}
	if (known != null) {
	    LocalVariable local = 
		(LocalVariable) known.get(new LocalVariable(name));

	    if (local != null) {
		type = local.getType();

		/* new */

		// dc.addReads(local);
		lvalue = local;

		return;
	    } 
	} 

	// formal parameter ?
	FormalParameter fp = context.getFormalParameter(name);
	if (fp != null) {
	    formpar = true;
	    type = fp.getType();
	    dc.addReads(fp);
	    if (!fp.isFinal()) {
		lvalue = fp;
	    } 
	    return;
	} 

	// this ?
	if (name.equals("this")) {
	    type = ((Class)context).getType();
	    
	    return;
	} 
	// super ?
	if (name.equals("super")) {
	    type = ((Class) context).getSuperclass();
	    if (type == null) {
		throw new ReflectExpressionError
		    ("<No superclass was found for class " 
		     + container.getName() 
		     + ".>", this);
	    } 
	    return;
	} 

	// length in an array ?
	if (context instanceof Class && ((Class) context).isArray() 
		&& name.equals("length")) {
	    type = ClassPool.Int;
	    // reads ???
	    return;
	} 

	// field ?
	Field f = context.getField(name);

	// System.out.println(name + " -> " + (c instanceof Class));
	if (f != null) {
	    type = f.getType();
	    dc.addReads(f);
	    if (!f.isFinal()) {
		lvalue = f;
	    } 
	    return;
	} 

	throw new ReflectExpressionError("<Name " + name 
					 + " was not found in context: " 
					 + context.getUnitName() 
					 + (context.getMethodName() != null ? 
					    "." + context.getMethodName() : 
					    "") 
					 + ".>", this);
    } 



    /**
     * Reflect this SimpleExpression in a Method context.
     *
     * @param context the context where to reflect this expression,
     *                e.g. Method or Class.
     * @param dc      the DependencyCollector, to store access
     * @return true, if reflection was successfull
     * @throws ReflectExpressionError if no superclass was found but 
     *         needed
     */
    private boolean reflectMethodExpression(Method              context, 
					    DependencyCollector dc) {
	// local variable ?
	if (locals != null) {
	    LocalVariable local = 
		(LocalVariable) locals.get(new LocalVariable(name));

	    if (local != null) {
		type = local.getType();
		dc.addReads(local);
		lvalue = local;
		return true;
	    } 
	} 
	// jassOld ?
	if (name.equals("Old")) {
	    type = context.getContainer().getType();
	    dc.addReads(new JassOld(type));
	    return true;
	} 
	// jassResult ?
	if (name.equals("Result")) {
	    type = context.getType();
	    dc.addReads(new JassResult(type));
	    return true;
	} 
	// this ?
	if (name.equals("this")) {
	    type = context.getContainer().getType();
	    
	    return true;
	} 
	// super ?
	if (name.equals("super")) {
	    type = ((Class) context.getContainer()).getSuperclass();
	    if (type == null) {
		throw new ReflectExpressionError
		    ("<No superclass was found for class " 
		     + container.getName() 
		     + ".>", this);
	    } 
	    return true;
	} 
	
	return false;
    }


    /**
     * Add the LocalVariable to the set of 'known' variables. The
     * 'known' variables are those, that were e.g. declared in forall
     * statements.
     *
     * @param l the LaocalVariable to add
     * @see known 
     */
    public void addKnownLocalVariable(LocalVariable l) {

	// System.out.println("!!!!!!!!!!!!!!!!!!!!"+l.getName());
	if (locals.contains(l) 
		|| container.getFormalParameter(name) != null) {
	    throw new ReflectionError(container.getName() + ":" + line 
				      + " <Name " + l.getName() 
				      + " already declared.>");
	} 
	if (known == null) {
	    known = new Set();
	} 
	known.addElement(l);
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

