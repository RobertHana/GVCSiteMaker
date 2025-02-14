/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class RangeExpression extends Expression {

    /**
     * Constructor declaration
     *
     *
     * @param bottom
     * @param top
     *
     * @see
     */
    public RangeExpression(Expression bottom, Expression top) {
	children = new Expression[2];
	children[0] = bottom;
	children[1] = top;
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
	return "{" + children[0].toHTML() + ".." + children[1].toHTML() + "}";
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
	for (int i = 0; i < children.length; i++) {
	    children[i].reflectExpression(c, dc);

	    if (!children[i].isIntegral() 
		    || children[i].getType() == ClassPool.Long) {
		throw new ReflectExpressionError(" <Border of range expression must be of type integral but not of type long: " 
						 + children[i] + ".>", this);
	    } 
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
    public Expression getTop() {
	return children[1];
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Expression getBottom() {
	return children[0];
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

