/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.util.Vector;
import java.io.PrintWriter;
import jass.util.Set;

/**
 * This class represents an expression of the Java language. There are
 * many subclasses, representing special expression: BinaryExpression,
 * MethodCallExpression, ArrayAccessExpression, ... All of them
 * implement an own methods to reflect the type and make semantic
 * (type) checks. <br> Mostly this class apears as a node in a tree
 * structure that represents a 'higher' expression 
 */
public class Expression implements FileBounded {

    /**
     * the subexpressions
     */
    protected Expression[] children = new Expression[0];

    /**
     * the l-value of this expression, null if expression is not a l-value.
     */
    protected Entity       lvalue = null;

    /**
     * the type of this expression
     */
    protected Class	   type;

    /**
     * the line at which this expressions apears in the source code
     */
    protected int	   line;

    /**
     * the class that contains this expression
     */
    protected Class	   container;

    /**
     * Sets the subexpression of the expression.
     * @param v a vector containing expression (type: Expression)
     */
    public void setChildren(Vector v) {
	children = new Expression[v.size()];

	v.copyInto(children);
    } 

    /**
     * Alternatic set method for the subexpressions.
     */
    public void setChildren(Expression[] e) {
	children = e;
    } 

    /**
     * Returns all subexpression except the last.
     */
    public Expression withoutLastChild() {
	Expression   e = new Expression();
	Expression[] es = new Expression[children.length - 1];

	for (int i = 0; i < children.length - 1; i++) {
	    es[i] = children[i];
	} 

	e.setChildren(es);

	return e;
    } 

    /**
     * Returns the last subexpression.
     */
    public Expression getLastChild() {
	return children[children.length - 1];
    } 

    /**
     * Returns the first subexpression.
     */
    public Expression getFirstChild() {
	return children[0];
    } 

    /**
     * Returns an array with the subexpression.
     * @return the subexpression; array of length zero if no se exists.
     */
    public Expression[] getChildren() {
	return children;
    } 

    /**
     * Gets a subsexpression for a given number.
     * @param i the number of the subexpression (starting with zero) 
     */
    public Expression getChild(int i) {
	/** require 0<=i<getNumChildren(); **/
	return children[i];
    } 

    /**
     * Returns the number of the children
     */
    public int getNumChildren() {
	return children.length;
    } 

    /**
     * This is a code string for this expression that can be evaluated
     * by the java compiler. 
     */
    public String toString() {
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toString());
	} 

	return sb.toString();
    } 

    /**
     * Description String in HTML.
     */
    public String toHTML() {
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toHTML());
	} 

	return sb.toString();
    } 

    /**
     * Shifts this expression to another context and gives the
     * resulting code string. Old and new context is given in form of
     * methods.  A reference string gives a name that should be
     * inserted in front of 'point'-Operations of this
     * expression. Referenced says if this expression has already a
     * new context, cause the superexpression is already changed. <br>
     * Example: in <= 0 && out >= 0 is shifted to 
     * superState.in <= 0 && superState.out >= 0
     * @param m_old the old contex
     * @param m_new the new contex
     * @param ref the new reference that should apear in front of the
     *            'point-ops'
     * @param indicates if there is already a reference prefix in
     *                  front of the 'point-ops' 
     */
    public String toStringWithChangedContext(Method m_old, Method m_new, 
					     String ref, boolean referenced) {
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toStringWithChangedContext(m_old, m_new, 
		    ref, referenced));
	} 

	return sb.toString();
    } 

    /**
     * For debugging, not longer used.
     */
    public void dump(PrintWriter pw, String indent) {
	pw.println(indent + "Expression [" + type + ":" + line + "]");

	for (int i = 0; i < children.length; i++) {
	    children[i].dump(pw, indent + "  ");
	} 
    } 

    /**
     * Reflects the informations about this expression. This includes
     * the type checks.  All subexpressions should overload this
     * method !
     * @param c the context in which this expression is evaluated
     * @param dc a DependencyCollector that collects the data flow
     *           informations 
     */
    public void reflectExpression(Context c, DependencyCollector dc) {
	for (int i = 0; i < children.length; i++) {
	    children[i].reflectExpression(c, dc);
	} 
    } 

    /**
     * @return  whether the type of this expression is numeric
     */
    public boolean isNumeric() {
	return type.isNumeric();
    } 

    /**
     * @return whether the type of this expression is integral
     */
    public boolean isIntegral() {
	return type.isIntegral();
    } 

    /**
     * @return whether the type of this expression is boolean
     */
    public boolean isBoolean() {
	return type.isBoolean();
    } 

    /**
     * @return whether the type of this expression is an object reference
     */
    public boolean isReference() {
	return type.isReference();
    } 

    /**
     * @return whether the type of this expression is primitive
     */
    public boolean isPrimitive() {
	return type.isPrimitive();
    } 

    /**
     * @return whether the type of this expression is void (i.e. has
     *         no type)
     */
    public boolean isVoid() {
	return type.isVoid();
    } 

    /**
     * @return the type;
     */
    public Class getType() {
	return type;
    } 

    /**
     * Set the type of this expression
     * @param type  the new type
     */
    public void setType(Class type) {
	this.type = type;
    } 

    /**
     * Set the line attribute, that is used to refer to the source 
     * code line where this expression was defined.
     * @param l  the new line
     */
    public void setLine(int l) {
	line = l;
    } 

    /**
     * @return  the line where this expression was defined.
     */
    public int getLine() {
	return line;
    } 

    /**
     * Set the container ?? (I don't know what this is.)
     * @param c a container class (?)
     */
    public void setContainer(Class c) {
	container = c;
    } 

    /**
     * @return the container of this expression
     *
     * @see setContainer(jass.reflect.Class)
     */
    public Class getContainer() {
	return container;
    } 

    /**
     * If this is an assignment expression, this method will return the 
     * entity the assignment goes to (the l-value).
     *
     * @return  the entity (l-value) of this expression
     * @see setLValue(Entity)
     */
    public Entity getLValue() {
	return lvalue;
    } 

    /**
     * This method is used to set the entity of an assignment expression
     * @param lval  the entity the assignment goes to.
     */
    public void setLValue(Entity lval) {
	lvalue = lval;
    } 

    /**
     * Add a local variable to all child expression.
     * @param l  the local variable to add.
     */
    public void addKnownLocalVariable(LocalVariable l) {
	for (int i = 0; i < children.length; i++) {
	    children[i].addKnownLocalVariable(l);
	} 
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

