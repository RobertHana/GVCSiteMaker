/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.util.Vector;
import java.io.PrintWriter;
import jass.util.Set;

/**
 * This represents an binary expression. Something like: true || getSize() > 0
 * @see Expression
 */

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class BinaryExpression extends Expression {
    public static final int CONDITIONAL_OR = 1;
    public static final int CONDITIONAL_AND = 2;
    public static final int INCLUSIVE_OR = 3;
    public static final int EXCLUSIVE_OR = 4;
    public static final int AND = 5;
    public static final int EQUALITY = 6;
    public static final int RELATIONAL = 7;
    public static final int SHIFT = 8;
    public static final int ADDITIVE = 9;
    public static final int MULTIPLICATIVE = 10;
    protected int	    op_type;

    /**
     * the operator string, like : + or ||
     */
    protected String[]      images = new String[0];

    /**
     * Method declaration
     *
     *
     * @param v
     *
     * @see
     */
    public void setImages(Vector v) {
	images = new String[v.size()];

	v.copyInto(images);
    } 

    /**
     * Method declaration
     *
     *
     * @param s
     *
     * @see
     */
    public void setImages(String[] s) {
	images = s;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String[] getImage() {
	return images;
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
	StringBuffer sb = new StringBuffer("");

	for (int i = 0; i < children.length; i++) {
	    if (i > 0) {
		sb.append(images[i - 1]);
	    } 

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
	StringBuffer sb = new StringBuffer("");

	for (int i = 0; i < children.length; i++) {
	    if (i > 0) {
		if (images[i-1] == ">") 
		    sb.append("&gt;"); 
		else if (images[i-1] == "<") 
		    sb.append("&lt;"); 
		else if (images[i-1] == "<=") 
		    sb.append("&lt;="); 
		else if (images[i-1] == ">=") 
		    sb.append("&lt;="); 
		else if (images[i-1] == "&") 
		    sb.append("&amp;"); 
		else if (images[i-1] == "&&") 
		    sb.append("&amp;&amp;"); 
		else if (images[i-1] == "\"") 
		    sb.append("&quot;"); 
		else 
		    sb.append(images[i - 1]);
	    }

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

	for (int i = 0; i < children.length; i++) {
	    if (i > 0) {
		sb.append(images[i - 1]);
	    } 

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
	pw.println(indent + "BinaryExpression [" + type + ":" + line + "]");

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

	// System.out.println("------------------------------->"+reads);
	// type checking ...
	String error_id = container.getName() + ":" + getLine();

	switch (op_type) {

	case CONDITIONAL_OR:

	case CONDITIONAL_AND:
	    for (int i = 0; i < children.length; i++) {
		if (!children[i].isBoolean()) {
		    throw new ReflectExpressionError(error_id + " <Found " 
						     + children[i] 
						     + " in conditional expression while " 
						     + images[gz(i - 1)] 
						     + " is only defined for boolean expressions.>", this);
		} 
	    }

	    type = ClassPool.Boolean;

	    break;

	case INCLUSIVE_OR:

	case EXCLUSIVE_OR:

	case AND:
	    boolean all_boolean = true;
	    boolean all_integral = true;

	    for (int i = 0; i < children.length; i++) {
		if (!children[i].isBoolean()) {
		    all_boolean = false;
		} 
	    }

	    for (int i = 0; i < children.length; i++) {
		if (!children[i].isIntegral()) {
		    all_integral = false;
		} 
	    }

	    if (!(all_boolean || all_integral)) {
		throw new ReflectExpressionError(error_id 
						 + " <All expressions of a bitwise logical operation must be wether of type boolean or integral: " 
						 + this + ".>", this);
	    } 

	    if (all_boolean) {
		type = ClassPool.Boolean;
	    } else {

		// binary numeric (integral) promotion
		type = ClassPool.Int;

		for (int i = 0; i < children.length; i++) {
		    if (children[i].getType().getName().equals("long")) {
			type = ClassPool.Long;
		    } 
		}
	    } 

	    break;

	case SHIFT:
	    for (int i = 0; i < children.length; i++) {
		if (!children[i].isIntegral()) {
		    throw new ReflectExpressionError(error_id + " <Found " 
						     + children[i] 
						     + " in shift expression while " 
						     + images[gz(i - 1)] 
						     + " is only defined for integral types.>", this);

		    // unary numeric promotion
		} 
	    }

	    if (children[0].getType() instanceof ByteClass 
		    || children[0].getType() instanceof ShortClass 
		    || children[0].getType() instanceof CharClass) {
		type = ClassPool.Int;
	    } else {
		type = children[0].getType();
	    }

	    break;

	case RELATIONAL:
	    for (int i = 0; i < children.length; i++) {
		if (!children[i].isNumeric()) {
		    throw new ReflectExpressionError(error_id + " <Found " 
						     + children[i] 
						     + " in relational expression while " 
						     + images[gz(i - 1)] 
						     + " is only defined for numeric types.>", this);
		} 
	    }

	    type = ClassPool.Boolean;

	    break;

	case MULTIPLICATIVE:
	    for (int i = 0; i < children.length; i++) {
		if (!children[i].isNumeric()) {
		    throw new ReflectExpressionError(error_id + " <Found " 
						     + children[i] 
						     + " in relational expression while " 
						     + images[gz(i - 1)] 
						     + " is only defined for numeric types.>", this);

		    // binary numeric promotion
		} 
	    }

	    type = ClassPool.Int;

	    for (int i = 0; i < children.length; i++) {
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

	    break;

	case ADDITIVE:
	    for (int i = 0; i < children.length; i++) {
		if (!(children[i].isNumeric() || children[i].isReference())) {
		    throw new ReflectExpressionError(error_id + " <Found " 
						     + children[i] 
						     + " in relational expression while " 
						     + images[gz(i - 1)] 
						     + " is only defined for numeric or string types.>", this);

		    // binary numeric promotion + string promotion
		} 
	    }

	    type = ClassPool.Int;

	    for (int i = 0; i < children.length; i++) {
		if (children[i].isReference()) {
		    type = ClassPool.getClass("java.lang.String");
		} 

		if (children[i].getType() instanceof DoubleClass 
			&&!type.getType().getName().equals("java.lang.String")) {
		    type = ClassPool.Double;
		} 

		if (children[i].getType() instanceof FloatClass 
			&&!(type instanceof DoubleClass) 
			&&!type.getType().getName().equals("java.lang.String")) {
		    type = ClassPool.Float;
		} 

		if (children[i].getType() instanceof LongClass 
			&&!(type instanceof DoubleClass) 
			&& type instanceof FloatClass 
			&&!type.getType().getName().equals("java.lang.String")) {
		    type = ClassPool.Long;
		} 
	    } 

	    break;

	case EQUALITY:

	    // special type promotion
	    type = children[0].getType();

	    for (int i = 1; i < children.length; i++) {
		if (type.isNumeric()) {

		    // numeric type
		    if (!children[i].isNumeric()) {
			throw new ReflectExpressionError(error_id 
							 + " <Found " 
							 + children[i] 
							 + " in equality expression but expected numeric type.>", this);
		    } 
		} 

		// boolean type
		else if (type.isBoolean()) {
		    if (!children[i].isBoolean()) {
			throw new ReflectExpressionError(error_id + "<Found " 
							 + children[i] 
							 + " in equality expression but expected boolean type.>", this);
		    } 
		} 

		// reference type
		else {
		    if (!children[i].isReference()) {
			throw new ReflectExpressionError(error_id 
							 + " <Found " 
							 + children[i] 
							 + " in equality expression but expected reference type.>", this);
		    } 
		} 

		type = ClassPool.Boolean;
	    } 
	}
    } 

    /**
     * Method declaration
     *
     *
     * @param op_type
     *
     * @see
     */
    public void setOperatorType(int op_type) {
	this.op_type = op_type;
    } 

    /**
     * Method declaration
     *
     *
     * @param i
     *
     * @return
     *
     * @see
     */
    private int gz(int i) {
	return i < 0 ? 0 : i;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

