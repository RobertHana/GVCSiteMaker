/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.PrintWriter;

/**
 * This represents an method call. Something like: m(4,5,f(5))
 * @see Expression
 */

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class MethodCallExpression extends Expression {
    protected String methodname;
    protected Method m;

    /**
     * Method declaration
     *
     *
     * @param m
     *
     * @see
     */
    public void setMethodname(String m) {
	methodname = m;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getMethodname() {
	return methodname;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Method getMethod() {
	return m;
    } 

    /**
     * Constructor declaration
     *
     *
     * @param name
     * @param ae
     *
     * @see
     */
    public MethodCallExpression(String name, ArgumentExpression ae) {
	children = ae.getChildren();
	methodname = name;
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
	if (m == null) {
	    return methodname;
	} 

	StringBuffer sb = new StringBuffer();
	Entity       methodContainer = m.container;

	if (methodContainer != null && methodContainer.equals(container)) {
	    sb.append("jassInternal_");
	} 

	sb.append(methodname + "(");

	boolean first = true;

	for (int i = 0; i < children.length; i++) {
	    if (!first) {
		sb.append(",");
	    } else {
		first = false;
	    } 

	    if (children[i] != null) {
		sb.append(children[i].toString());
	    } 
	} 

	sb.append(")");

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
	StringBuffer sb = new StringBuffer();

	sb.append(methodname);
	sb.append("(");

	boolean first = true;

	for (int i = 0; i < children.length; i++) {
	    if (!first) {
		sb.append(",");
	    } else {
		first = false;
	    }

	    if (children[i] != null) {
		sb.append(children[i].toHTML());
	    } 
	} 

	sb.append(")");

	return sb.toString();
    } 

    /**
     * Method declaration
     *
     *
     * @param m_old
     * @param m_new
     * @param ref
     * @param referenced
     *
     * @return
     *
     * @see
     */
    public String toStringWithChangedContext(Method m_old, Method m_new, 
					     String ref, boolean referenced) {
	StringBuffer sb = new StringBuffer();

	sb.append((referenced ? "" : ref + "."));

	if (m.container.equals(container)) {
	    sb.append("jassInternal_");
	} 

	sb.append(methodname);
	sb.append("(");

	boolean first = true;

	for (int i = 0; i < children.length; i++) {
	    if (!first) {
		sb.append(",");
	    } else {
		first = false;
	    }

	    if (children[i] != null) {
		sb.append(children[i].toStringWithChangedContext(m_old, 
			m_new, ref, false));
	    } 
	} 

	sb.append(")");

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
	pw.println(indent + "MethodCall: [" + type + "] " + methodname);

	for (int i = 0; i < children.length; i++) {
	    children[i].dump(pw, indent + "  ");
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param c
     * @param argContext
     * @param dc
     *
     * @see
     */
    public void reflectExpression(Context c, Context argContext, 
				  DependencyCollector dc) {

	// reflect arguments and construct parameter array
	Class[] params = new Class[children.length];

	for (int i = 0; i < children.length; i++) {
	    children[i].reflectExpression(argContext, dc);

	    params[i] = children[i].getType();
	} 

	if (c instanceof Method) {
	    c = (Class) ((Method) c).getContainer();
	} 

	m = ((Class) c).getMethod(methodname, params);

	if (m == null) {
	    StringBuffer sb = new StringBuffer(methodname + "(");
	    boolean      first = true;

	    for (int i = 0; i < params.length; i++) {
		if (!first) {
		    sb.append(",");
		} else {
		    first = false;
		}

		sb.append(params[i].getName());
	    } 

	    sb.append(")");

	    throw new ReflectExpressionError(" <Method " + sb.toString() 
					     + " was not found in class " 
					     + ((Class) c).getName() + ".>", 
					     this);
	} 

	type = m.getType();

	dc.addCalls(m);
    } 

    /**
     * Method declaration
     *
     *
     * @param c
     * @param dc
     *
     * @see
     */
    public void reflectExpression(Context c, DependencyCollector dc) {

	// reflect arguments and construct parameter array
	Class[] params = new Class[children.length];

	for (int i = 0; i < children.length; i++) {
	    children[i].reflectExpression(c, dc);

	    params[i] = children[i].getType();
	} 

	if (c instanceof Method) {
	    c = (Class) ((Method) c).getContainer();
	} 

	m = ((Class) c).getMethod(methodname, params);

	if (m == null) {
	    StringBuffer sb = new StringBuffer(methodname + "(");
	    boolean      first = true;

	    for (int i = 0; i < params.length; i++) {
		if (!first) {
		    sb.append(",");
		} else {
		    first = false;
		}

		sb.append(params[i].getName());
	    } 

	    sb.append(")");

	    throw new ReflectExpressionError(" <Method " + sb.toString() 
					     + " was not found in class " 
					     + ((Class) c).getName() + ".>", 
					     this);
	} 

	type = m.getType();

	dc.addCalls(m);
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

