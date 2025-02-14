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
public class InstanceOfExpression extends Expression {
    protected Class inst_type;

    /**
     * Constructor declaration
     *
     *
     * @param inst
     * @param of
     *
     * @see
     */
    public InstanceOfExpression(Expression inst, Class of) {
	children = new Expression[1];
	children[0] = inst;
	inst_type = of;
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
	return children[0] + " instanceof " + inst_type.getName();
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
	return children[0].toHTML() + " instanceof " + inst_type.getName();
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
	       + " instanceof " + inst_type.getName();
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
	pw.println(indent + "InstanceOfExpression [" + type + "]");
	children[0].dump(pw, indent + "  ");
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

	if (!children[0].isReference()) {
	    throw new ReflectExpressionError(" <Operand of instanceof expression must be of reference type.>", 
					     this);
	} 

	type = ClassPool.Boolean;
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

