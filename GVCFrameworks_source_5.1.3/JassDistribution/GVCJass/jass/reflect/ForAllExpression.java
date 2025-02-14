/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.PrintWriter;
import jass.util.Set;
import jass.GlobalFlags;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ForAllExpression extends Expression {
    protected String  i;
    protected Set     locals = new Set();
    protected int     nr;
    protected boolean forall = true;

    // protected Set iterators = new Set();

    /**
     * Constructor declaration
     *
     *
     * @param i
     * @param enum
     * @param child
     * @param nr
     * @param forall
     *
     * @see
     */
    public ForAllExpression(String i, Expression enum, Expression child, 
			    int nr, boolean forall) {
	children = new Expression[2];
	children[0] = enum;
	children[1] = child;
	this.i = i;
	this.nr = nr;
	this.forall = forall;
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
	pw.println(indent + "ForAllEpression [" + type + "]");

	for (int i = 0; i < children.length; i++) {
	    children[i].dump(pw, indent + "  ");
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
    public String toString() {
	if (GlobalFlags.GENERATE_FORALL) {
	    StringBuffer sb = 
		new StringBuffer("jassCheck" + (forall ? "ForAll" : "Exists") 
				 + "_" + nr + "_" 
				 + container.getName().replace('.', '_') 
				 + "(");
	    boolean      first = true;
	    Object[]     os = locals.elements();

	    for (int i = 0; i < os.length; i++) {
		if (!first) {
		    sb.append(",");
		} else {
		    first = false;
		}

		sb.append(((Entity) os[i]).getName());
	    } 

	    sb.append(")");

	    return sb.toString();
	} else {
	    return "true";
	}
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
	if (GlobalFlags.GENERATE_FORALL) {
	    StringBuffer sb = new StringBuffer();

	    sb.append((referenced ? "" : ref + ".") + "jassCheck" 
		      + (forall ? "ForAll" : "Exists") + "_" + nr + "_" 
		      + container.getName().replace('.', '_') + "(");

	    boolean  first = true;
	    Object[] os = locals.elements();

	    for (int i = 0; i < os.length; i++) {
		if (!first) {
		    sb.append(",");
		} else {
		    first = false;
		}

		if (os[i] instanceof JassOld) {
		    sb.append(((Entity) os[i]).getName() 
			      + ".jassGetSuperState()");
		} else {
		    sb.append(((Entity) os[i]).getName());
		}
	    } 

	    sb.append(")");

	    // no locals can be used (precondition and postconditions contain no locals. Old, Result ?????????????????
	    // formals !
	    return sb.toString();
	} else {
	    return "true";
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
	LocalVariable       l = new LocalVariable(i);
	DependencyCollector hdc = new DependencyCollector();

	if (children[0] instanceof AllocationExpression) {
	    l.setType(ClassPool.getClass("java.lang.Object"));
	} else {
	    l.setType(ClassPool.Int);
	} 

	// children[0].addKnownLocalVariable(l);
	children[0].reflectExpression(c, hdc);

	if (children[0] instanceof AllocationExpression) {
	    if (!children[0].getType().doesImplement(ClassPool.getClass("jass.runtime.FiniteEnumeration"))) {
		throw new ReflectionError(container.getName() + ":" 
					  + getLine() 
					  + " <Class in forall/exists expression must implement jass.runtime.FiniteEnumeration: " 
					  + children[0].getType().getName() 
					  + ">");
	    } 
	} 

	children[1].addKnownLocalVariable(l);
	children[1].reflectExpression(c, hdc);

	Object[] os = hdc.getReads().elements();

	for (int i = 0; i < os.length; i++) {
	    if (os[i] instanceof LocalVariable 
		    || os[i] instanceof FormalParameter 
		    || os[i] instanceof JassOld 
		    || os[i] instanceof JassResult) {
		locals.addElement(os[i]);
	    } 
	} 

	dc.union(hdc);

	type = ClassPool.Boolean;
    } 

    /**
     * Method declaration
     *
     *
     * @param l
     *
     * @see
     */
    public void addKnownLocalVariable(LocalVariable l) {

	/*
	 * System.out.println(l);
	 * System.out.println(children[1]);
	 */
	locals.addElement(l);
	super.addKnownLocalVariable(l);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public int getNr() {
	return nr;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Set getLocals() {
	return locals;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getIdentifier() {
	return i;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Expression getArgs() {
	return children[0];
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Expression getChild() {
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
    public boolean isForAll() {
	return forall;
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
	return (forall ? "forall " : "exists ") + i + " : " 
	       + children[0].toHTML() + " " + children[1].toHTML();
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

