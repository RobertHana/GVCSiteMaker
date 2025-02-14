/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.*;
import java.util.Vector;
import jass.util.Set;
import jass.GlobalFlags;

/**
 * This is the superclass of all boolean assertions. Most semantic checks are 
 * done here. <br>
 * The subclasses call this code via 'super.reflect()'
 */
public abstract class BooleanAssertion extends Assertion {

    /**
     * The assertion expressions of this assertion
     */
    protected BooleanAssertionExpression[] assExprs = 
	new BooleanAssertionExpression[0];

    /**
     * Indicates if Old is use
     */
    protected boolean			   uses_jassOld = false;

    /**
     * Indicates if local variable or formal parameter are used
     */
    protected boolean			   uses_Locals = false;

    /**
     * Indicates if Result is used
     */
    protected boolean			   uses_jassResult = false;

    /**
     * The dataflow information
     */
    protected DependencyCollector	   dc;

    /**
     * Reflects the assertion and does many semantic checks.
     */
    public void reflectExpressions() {
	dc = new DependencyCollector();

	for (int i = 0; i < assExprs.length; i++) {
	    assExprs[i].reflectExpression(container, dc);
	} 

	Object[] reads = dc.getReads().elements();

	for (int i = 0; i < reads.length; i++) {
	    if (reads[i] instanceof JassOld) {
		uses_jassOld = true;
	    } 

	    if (reads[i] instanceof JassResult) {
		uses_jassResult = true;
	    } 

	    if (reads[i] instanceof LocalVariable) {
		uses_Locals = true;
	    } 

	    if (reads[i] instanceof Field) {
		
		if (this instanceof Precondition 
		    && Modifier.isMoreVisible
		    (((Method) container).getModifier(), 
		     ((Field) reads[i]).getModifier())) {
		    throw new ReflectionError
			(container.getUnitName() + "." 
			 + ((Method) container).getIdString() 
			 + ":" + getLineString() 
			 + " <Fields used in preconditions must be as visible as the method the assertion is declared in: " 
			 + ((Field) reads[i]).getName() + ">");

		    // References in invariants ?	Static or public field in invariant ?
		} 
		
		if (this instanceof Invariant && GlobalFlags.VERBOSE > 0) {
		    Class fieldClass = (Class) ((Field) reads[i]).container;
		    if (fieldClass != (Class) container) {
			System.out.println("Warning: " 
					   + container.getUnitName() + ":" 
					   + getLineString() 
					   + " <Invariant depends on field in superclass/another class: " 
					   + ((Field) reads[i]).getName() 
					   + ">");
		    } else {
			if (((Field) reads[i]).isPublic() 
			    || ((Field) reads[i]).isStatic()) {
			    System.out.println("Warning: " 
					       + container.getUnitName() 
					       + ":" + getLineString() 
					       + " <Invariant depends on field that is public or static: " 
					       + ((Field) reads[i]).getName() 
					       + ">");
			} 
		    } 
		} 
	    } 
	} 
	
	DataFlow df = new DataFlow();
	Object[] ms = dc.getCalls().elements();

	for (int i = 0; i < ms.length; i++) {

	    if (this instanceof Precondition
		&& Modifier.isMoreVisible(((Method) container).getModifier(), 
					  ((Method) ms[i]).getModifier())) {
		throw new ReflectionError(container.getUnitName() + "." 
					  + ((Method) container).getIdString() 
					  + ":" + getLineString() 
					  + " <Methods used in assertions must be as visible as the method the assertion is declared in: " 
					  + ((Method) ms[i]).getName() + ">");

		// check dataflow
	    } 

	    dc.union(df.analyseDataFlow((Method) ms[i]));
	} 

	Object[] ws = dc.getWrites().elements();

	for (int i = 0; i < ws.length; i++) {
	    if (ws[i] instanceof Field) {
		throw new ReflectionError(container.getUnitName() 
					  + (container instanceof Method ? "." + ((Method) container).getIdString() : "") 
					  + ":" + getLineString() 
					  + " <Assertion calls a method that writes field " 
					  + ((Field) ws[i]).getName()
					  + " but no side effects are permitted.>");
	    } 
	} 

	for (int i = 0; i < assExprs.length; i++) {
	    if (assExprs[i].getType() != ClassPool.Boolean 
		&& !(this instanceof LoopVariant)) {
		throw new ReflectionError(container.getUnitName() 
					  + (container instanceof Method
					     ? "." + ((Method) container).getIdString() : "") 
					  + ":" + assExprs[i].getLine() 
					  + " <Expressions in assertion must be of type boolean: " 
					  + assExprs[i] + ".>");
	    } 
	} 
    } 

    /**
     * Set the assertion expressions, that are associated to this condition.
     *
     * @param assExprs  the expressions
     *
     * @see getAssertionExpressions()
     */
    public void setAssertionExpressions(BooleanAssertionExpression[] assExprs) {
	this.assExprs = assExprs;
    } 

    /**
     * Get the assertion expressions, that are associated to this condition.
     *
     * @return the assertion expressions
     *
     * @see setAssertionExpressions(BooleanAssertionExpression[]),
     *      setAssertionExpressions(Vector)
     */
    public BooleanAssertionExpression[] getAssertionExpressions() {
	return assExprs;
    } 

    /**
     * Set the assertion expressions, that are associated to this condition.
     * This is a convinient method to store the assertions in a Vector
     * rather than an array.
     *
     * @param exprs a Vector containing BooleanAssertionExpression s.
     *
     * @see setAssertionExpressions(BooleanAssertionExpression[])
     */
    public void setAssertionExpressions(Vector exprs) {
	assExprs = new BooleanAssertionExpression[exprs.size()];

	exprs.copyInto(assExprs);
    } 

    /**
     * Does this assertion refer to the prestate via Old keyword
     *
     * @return true, if it does refer
     */
    public boolean usesJassOld() {
	return uses_jassOld;
    } 

    /**
     * Method declaration
     */
    public Set getReads() {
	return dc.getReads();
    } 

    /**
     * Method declaration
     */
    public Set getCalls() {
	return dc.getCalls();
    } 

    /**
     * Return the internal calls of this assertions. This are calls, 
     * that have a target in the same class the assertion is declared in.
     */
    public Set getInternalCalls() {
	Class c;

	if (container instanceof Class) {
	    c = (Class) container;
	} else {
	    c = ((Method) container).getDeclaringClass();
	}

	Set      set = new Set();
	Object[] os = dc.getCalls().elements();

	for (int i = 0; i < os.length; i++) {
	    if (((Method) os[i]).getDeclaringClass().equals(c)) {
		set.addElement(os[i]);
	    } 
	}

	return set;
    } 

    /**
     * Convert this assertion to HTML code.
     *
     * @return the HTML represention as String
     * 
     * @see BooleanAssertionExpression.toHTML()
     */
    public String toHTML() {
	StringBuffer sb = new StringBuffer();
	boolean      first = true;

	for (int i = 0; i < assExprs.length; i++) {
	    if (!first) {
		sb.append("; <br>");
	    } else {
		first = false;
	    }

	    sb.append(assExprs[i].toHTML());
	} 

	return sb.toString();
    } 

    /**
     * Get the line number(s) of this assertion as String. The string will
     * represent the line number the assertion starts at, and - if the 
     * assertion exceeds one line - the line of the end seperated by "-".
     *
     * @return  a String representing the line number(s) of this assertion
     */
    public String getLineString() {
	if (assExprs.length > 0) {
	    int start = assExprs[0].getLine();
	    int end = assExprs[assExprs.length - 1].getLine();

	    if (start < end) {
		return start + "-" + end;
	    } else {
		return new Integer(start).toString();
	    }
	} else {
	    return "";
	}
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

