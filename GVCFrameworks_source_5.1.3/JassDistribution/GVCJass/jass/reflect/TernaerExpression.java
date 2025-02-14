/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.util.Vector;
import java.io.PrintWriter;
import jass.util.Set;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class TernaerExpression extends Expression {

    /**
     * Constructor declaration
     *
     *
     * @param cond
     * @param first
     * @param second
     *
     * @see
     */
    public TernaerExpression(Expression cond, Expression first, 
			     Expression second) {
	children = new Expression[3];
	children[0] = cond;
	children[1] = first;
	children[2] = second;
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
	return children[0] + "?" + children[1] + ":" + children[2];
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
	return children[0].toHTML() + "?" + children[1].toHTML() + ":" 
	       + children[2].toHTML();
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
	return children[0].toStringWithChangedContext(m_old, m_new, ref, referenced) 
	       + "?" 
	       + children[1].toStringWithChangedContext(m_old, m_new, ref, referenced) 
	       + ":" 
	       + children[2].toStringWithChangedContext(m_old, m_new, ref, 
		referenced);
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
	pw.println(indent + "TernaryExpression [" + type + "]");

	for (int i = 0; i < children.length; i++) {
	    children[i].dump(pw, indent + "  ");
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
	for (int i = 0; i < children.length; i++) {
	    children[i].reflectExpression(c, dc);
	} 

	if (!children[0].isBoolean()) {
	    throw new ReflectExpressionError(" <First expression in the ?-operator must be of type boolean.>", 
					     this);
	} 

	if (children[1].isVoid() || children[2].isVoid()) {
	    throw new ReflectExpressionError("<No operand of type void is allowed in ?-expression.>", 
					     this);
	} 

	if (children[1].getType().equals(children[2].getType())) {
	    type = children[1].getType();

	    return;
	} 

	if (children[1].isNumeric() && children[2].isNumeric()) {

	    // this is different to the JLS ! ???
	    // only
	    // binary numeric promotion
	    type = ClassPool.Int;

	    for (int i = 1; i < children.length; i++) {
		if (children[i].getType() instanceof DoubleClass) {
		    type = ClassPool.Double;
		} 

		if (children[i].getType() instanceof FloatClass 
			&&!(type instanceof DoubleClass)) {
		    type = ClassPool.Float;
		} 

		if (children[i].getType() instanceof LongClass 
			&&!(type instanceof DoubleClass) 
			&& type instanceof FloatClass) {
		    type = ClassPool.Long;
		} 
	    } 

	    return;
	} 

	if (children[1].isReference() && children[2].isReference()) {
	    if (children[1].getType() instanceof NullClass) {
		type = children[2].getType();

		return;
	    } 

	    if (children[2].getType() instanceof NullClass) {
		type = children[1].getType();

		return;
	    } 

	    // is the one reference assignable to the other ?
	    if (children[1].getType().isAssignableFrom(children[2].getType())) {
		type = children[1].getType();

		return;
	    } 

	    if (children[2].getType().isAssignableFrom(children[1].getType())) {
		type = children[2].getType();

		return;
	    } else {
		throw new ReflectExpressionError("<Incompatible types in ?-operator.>", 
						 this);
	    }
	} else {
	    throw new ReflectExpressionError("<Incompatible types in ?-operator.>", 
					     this);
	}
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

