/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class UnaryExpression extends Expression {
    protected String image;

    /**
     * Constructor declaration
     *
     *
     * @param e
     * @param image
     *
     * @see
     */
    public UnaryExpression(Expression e, String image) {
	children = new Expression[1];
	children[0] = e;
	this.image = image;
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
	StringBuffer sb = new StringBuffer(image);

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toString());
	} 

	return sb.toString();
    } 

    /**
     * The HTML representation of an unary expression simply is the 
     * expression itself.
     */
    public String toHTML() { 
	StringBuffer sb = new StringBuffer(image);

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
	StringBuffer sb = new StringBuffer(image);

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
    public void dump(java.io.PrintWriter pw, String indent) {
	pw.println(indent + "UnaryExpression: [" + type + "]");

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
	if (image.equals("+") 
		|| image.equals("-") &&!children[0].isNumeric()) {
	    throw new ReflectExpressionError("<Found " + children[0] 
					     + " in unary expression while " 
					     + image 
					     + " is only defined for numeric types.>", this);
	} 

	if (image.equals("~") &&!children[0].isIntegral()) {
	    throw new ReflectExpressionError("<Found " + children[0] 
					     + " in unary expression while " 
					     + image 
					     + " is only defined for integral types.>", this);
	} 

	if (image.equals("!") &&!children[0].isBoolean()) {
	    throw new ReflectExpressionError("<Found " + children[0] 
					     + " in unary expression while " 
					     + image 
					     + " is only defined for boolean types.>", this);
	} 

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

