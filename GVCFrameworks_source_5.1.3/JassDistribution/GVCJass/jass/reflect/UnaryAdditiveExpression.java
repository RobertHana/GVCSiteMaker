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
public class UnaryAdditiveExpression extends Expression {
    protected boolean pre;
    protected String  image;

    /**
     * Constructor declaration
     *
     *
     * @param e
     * @param pre
     * @param image
     *
     * @see
     */
    public UnaryAdditiveExpression(Expression e, boolean pre, String image) {
	children = new Expression[1];
	children[0] = e;
	this.pre = pre;
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
	StringBuffer sb = new StringBuffer();

	if (pre) {
	    sb.append(image);
	} 

	sb.append(children[0]);

	if (!pre) {
	    sb.append(image);
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

	if (pre) {
	    sb.append(image);
	} 

	sb.append(children[0].toHTML());

	if (!pre) {
	    sb.append(image);
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
	pw.println(indent + "UnaryAdditivExpression: [" + type + "]");

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

	// this needs no type checking, cause its not included in assertions !
	children[0].reflectExpression(c, dc);

	type = children[0].getType();

	Entity lval = children[0].getLValue();

	if (lval != null) {
	    dc.addWrites(lval);
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

