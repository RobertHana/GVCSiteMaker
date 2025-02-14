/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.PrintWriter;

/**
 * This represents an assignment. Something like: a = 3 * 5
 * @see Expression
 */

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class AssignmentExpression extends Expression {
    protected String image;

    /**
     * Constructor declaration
     *
     *
     * @param lh
     * @param op
     * @param rh
     *
     * @see
     */
    public AssignmentExpression(Expression lh, String op, Expression rh) {
	children = new Expression[2];
	children[0] = lh;
	children[1] = rh;
	this.image = op;
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

	sb.append(children[0]);
	sb.append(image);
	sb.append(children[1]);

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
	pw.println(indent + "AssignmentExpression: [" + type + "]");

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
	children[1].reflectExpression(c, dc);

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

