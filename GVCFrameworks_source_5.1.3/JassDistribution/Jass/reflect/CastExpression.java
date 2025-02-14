/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.PrintWriter;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class CastExpression extends Expression {

    /**
     * Constructor declaration
     *
     *
     * @param _type
     *
     * @see
     */
    public CastExpression(Class _type) {
	type = _type;
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

	sb.append("(" + type.getName() + ")");

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toString());
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

	sb.append("(" + type.getName() + ")");

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toHTML());
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

	sb.append("(" + type.getName() + ")");

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toStringWithChangedContext(m_old, m_new, 
		    ref, referenced));
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
	pw.println(indent + "CastExpression: [" + type + "]");

	for (int i = 0; i < children.length; i++) {
	    children[i].dump(pw, "  " + indent);
	} 
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
	children[0].reflectExpression(c, dc);

	// Type checking ...
	if (children[0].isPrimitive() &&!type.isPrimitive()) {
	    throw new ReflectionError(container.getName() + ":" + getLine() 
				      + " <Primitive type can not be casted to reference type: " 
				      + children[0].toString() + ".>");
	} 

	if (!children[0].isPrimitive() && type.isPrimitive()) {
	    throw new ReflectionError(container.getName() + ":" + getLine() 
				      + " <Reference type can not be casted to primitive type: " 
				      + children[0].toString() + ".>");
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
    public int getLine() {
	return children[0].getLine();
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
	return children[0].getContainer();
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

