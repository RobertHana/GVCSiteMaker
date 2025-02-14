/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class NestedExpression extends Expression {

    /**
     * Constructor declaration
     *
     *
     * @param e
     *
     * @see
     */
    public NestedExpression(Expression e) {
	children = new Expression[1];
	children[0] = e;
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
	StringBuffer sb = new StringBuffer("(");

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toString());
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
	StringBuffer sb = new StringBuffer("(");

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toHTML());
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
	StringBuffer sb = new StringBuffer("(");

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toStringWithChangedContext(m_old, m_new, 
		    ref, referenced));
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
    public void dump(java.io.PrintWriter pw, String indent) {
	pw.println(indent + "NestedExpression: [" + type + "]");

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

	type = children[0].getType();
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

