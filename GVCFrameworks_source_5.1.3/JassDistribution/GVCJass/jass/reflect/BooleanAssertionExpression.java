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
public class BooleanAssertionExpression extends AssertionExpression {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */

    // none

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    /**
     * Constructor for converting an Expression to a 
     * BooleanAssertionExpression.
     *
     * @param expr  the Expression
     *
     */
    public BooleanAssertionExpression(Expression expr) {
	children = new Expression[1];
	children[0] = expr;
	line = expr.getLine();
	container = expr.getContainer();
    }

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */

    /**
     * Dump this assertion to the given PrintWriter with the indention.
     *
     * @param pw      the PrintWriter to dump to
     * @param indent  the indention that should be used
     *
     */
    public void dump(PrintWriter pw, String indent) {
	pw.println(indent + "BooleanAssertionExpression [" + type + ":" 
		   + line + "] " + label);

	for (int i = 0; i < children.length; i++) {
	    children[i].dump(pw, indent + "  ");
	} 
    } 

    /**
     * return the expression
     *
     * @return the expression
     */
    public Expression getExpression() {
	return getFirstChild();
    } 

    /**
     * reflect the the expression
     *
     * @param c   the context of the expression
     * @param dc  the dependencies collocting object
     *
     * @see Expression.reflectExpression(Context, DependencyCollector);
     */
    public void reflectExpression(Context c, DependencyCollector dc) {

	/*
	 * if(label != null)
	 * {
	 * label.reflect();
	 * }
	 */
	children[0].reflectExpression(c, dc);
	type = children[0].getType();
    } 

    /**
     * Return a HTML representation of this assertion. This is a 
     * String representation where the reserved HTML characters are
     * escaped. Additionally the label is set in bold face.
     *
     * @return an HTML representation of this assertion
     *
     * @see toString()
     */
    public String toHTML() {
	StringBuffer html = new StringBuffer();
	Expression expression = getExpression();
	
	if (label != null) {
	    html.append("<b>").append(label).append("</b>: ");
	}
	if (expression != null) {
	    html.append("<code>")
		.append(expression.toHTML())
		.append("</code>");
	}
	else {
	    html.append("&lt;without children&gt;");
	}
	return html.toString();
    } 

    /**
     * Return a String representation of this assertion. The String
     * will also include the label (optionally) as prefix seperated
     * by a colon from the expression itself.
     *
     * @return a String representation of this assertion
     *
     * @see toHTML()
     */
    public String toString() {
	StringBuffer sb = new StringBuffer();
	Expression expression = getExpression();
	
	if (label != null) {
	    sb.append(label).append(":");
	}
	sb.append(expression != null ? expression.toString() 
		  : "<without children>");

	return sb.toString();
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

