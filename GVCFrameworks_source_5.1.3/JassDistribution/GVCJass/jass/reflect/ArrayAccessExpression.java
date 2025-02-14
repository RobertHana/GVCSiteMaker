/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.PrintWriter;

/**
 * This represents an array access. Something like: buffer[f.a+4]
 * @see Expression
 */
public class ArrayAccessExpression extends Expression {

    /**
     * Constructor declaration
     *
     *
     * @param name
     * @param ae
     *
     * @see
     */
    public ArrayAccessExpression(Expression name, Expression ae) {
	children = new Expression[2];
	children[0] = name;
	children[1] = ae.getFirstChild();
    }

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    public void addChild(Expression e) {
	Expression[] help = new Expression[children.length + 1];

	for (int i = 0; i < children.length; i++) {
	    help[i] = children[i];
	}

	help[children.length] = e.getFirstChild();
	children = help;

	// explicite garbage collect
	help = null;
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

	sb.append(children[0].toString());

	for (int i = 1; i < children.length; i++) {
	    sb.append("[");
	    sb.append(children[i].toString());
	    sb.append("]");
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
	StringBuffer sb = new StringBuffer();

	sb.append(children[0].toHTML());

	for (int i = 1; i < children.length; i++) {
	    sb.append("[");
	    sb.append(children[i].toHTML());
	    sb.append("]");
	} 

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

	sb.append(children[0].toStringWithChangedContext(m_old, m_new, ref, 
		referenced));

	for (int i = 1; i < children.length; i++) {
	    sb.append("[");
	    sb.append(children[i].toStringWithChangedContext(m_old, m_new, 
		    ref, referenced));
	    sb.append("]");
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
	pw.println(indent + "ArrayAccess [" + type + "]");

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

	// reflect the name expression (method call or field)
	// System.out.println(">>>>>>>>>"+children[0]);
	// System.out.println(">>>>>>>>>"+children[1]);
	children[0].reflectExpression(c, dc);

	Class array_type = children[0].getType();

	// enough dimensions ?
	int   anz_dim = array_type.getType().getName().lastIndexOf("[") + 1;

	if (anz_dim > array_type.getArrayDims()) {
	    throw new ReflectExpressionError(" <Too many array reference expressions: " 
					     + toString() + ">", this);
	} 

	for (int i = 1; i < children.length; i++) {
	    children[i].reflectExpression(argContext, dc);

	    if (!children[i].isIntegral() 
		    || children[i].getType() == ClassPool.Long) {
		throw new ReflectExpressionError("Reference expression in array access must be of type byte,char,short or int: " 
						 + children[i].toString(), this);
	    } 
	} 

	type = 
	    ClassPool.getClass(ClassPool.componentToArray(ClassPool.arrayToComponent(array_type.getName()), 
		array_type.getArrayDims() - anz_dim));
	lvalue = children[0].getLValue();
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

	// System.out.println(">>>>>>>>>"+children[0]);
	// System.out.println(">>>>>>>>>"+children[1]);
	// reflect the name expression (method call or field)
	children[0].reflectExpression(c, dc);

	Class array_type = children[0].getType();

	// enough dimensions ?
	int   anz_dim = array_type.getType().getName().lastIndexOf("[") + 1;

	if (anz_dim > array_type.getArrayDims()) {
	    throw new ReflectExpressionError(" <Too many array reference expressions: " 
					     + toString() + ">", this);
	} 

	for (int i = 1; i < children.length; i++) {
	    children[i].reflectExpression(c, dc);

	    if (children[i].getType() != ClassPool.Int) {
		throw new ReflectExpressionError("Reference expression in array access must be of type byte,char,short or int: " 
						 + children[i].toString(), this);
	    } 
	} 

	type = 
	    ClassPool.getClass(ClassPool.componentToArray(ClassPool.arrayToComponent(array_type.getName()), 
		array_type.getArrayDims() - anz_dim));
	lvalue = children[0].getLValue();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public int getLine() {
	if (children.length > 0 && children[0] != null) {
	    return children[0].getLine();
	} else {
	    return -1;
	}
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Class getContainer() {
	if (children.length > 0 && children[0] != null) {
	    return children[0].getContainer();
	} else {
	    return null;
	}
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

