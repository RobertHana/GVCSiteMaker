/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import jass.reflect.ErrorHandler;
import jass.GlobalFlags;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class Precondition extends BooleanAssertion {
    protected Precondition refines = null;

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

	sb.append("Precondition [");

	if (assExprs.length > 0) {
	    boolean first = true;

	    for (int i = 0; i < assExprs.length; i++) {
		if (!first) {
		    sb.append("; ");
		} else {
		    first = false;
		}

		sb.append(assExprs[i]);
	    } 
	} 

	sb.append("]");

	return sb.toString();
    } 

    /**
     * Method declaration
     *
     *
     * @param p
     *
     * @see
     */
    public void setRefinedPrecondition(Precondition p) {
	refines = p;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Precondition getRefinedPrecondition() {
	return refines;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflectExpressions() {
	super.reflectExpressions();

	Class  c = ((Method) container).getDeclaringClass();
	Method m = ((Method) container);

	if (uses_Locals) {
	    throw new ReflectionError(c.getName() + "." + m.getIdString() 
				      + ":" + getLineString() 
				      + " <No local variables are allowed in preconditons.>");
	} 

	if (uses_jassOld) {
	    throw new ReflectionError(c.getName() + "." + m.getIdString() 
				      + ":" + getLineString() 
				      + " <Variable jassOld may only be used in postconditions.>");
	} 

	if (uses_jassResult) {
	    throw new ReflectionError(c.getName() + "." + m.getIdString() 
				      + ":" + getLineString() 
				      + " <Variable jassResult may only be used in postconditions.>");
	} 
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
    public void generateCode(PrintWriter pw, String indent) {
	Class  c = ((Method) container).getDeclaringClass();
	Method m = ((Method) container);

	// refines c his superclass and overwrittes this method a method in the superclass ?
	if (refines != null && GlobalFlags.GENERATE_REFINE) {
	    pw.println();
	    pw.println(indent + "/* refinement */");
	    pw.println(indent + c.getSuperclass().getName() 
		       + " jassSuperState = jassGetSuperState();");

	    // pre(A) && !pre(C) --> Exception
	    BooleanAssertionExpression[] superExprs = 
		refines.getAssertionExpressions();
	    boolean			 first = true;

	    pw.print(indent + "if (");

	    for (int i = 0; i < superExprs.length; i++) {
		Expression e = superExprs[i].getExpression();

		// map all names A that are not formal parameters to jassSuperState.A
		// formal parameters must be renamed (e.g.: i - > n)
		if (!first) {
		    pw.print("&&");
		} else {
		    first = false;
		}

		pw.print(e.toStringWithChangedContext((Method) refines.getContainer(), 
						      m, "jassSuperState", 
						      false));
	    } 

	    pw.print("&&!(");

	    first = true;

	    for (int i = 0; i < assExprs.length; i++) {
		if (!first) {
		    pw.print("&&");
		} else {
		    first = false;
		}

		pw.print(assExprs[i].getExpression());
	    } 

	    pw.print(")) ");
	    pw.print(ErrorHandler.generateTrigger(GlobalFlags.REFINEMENT, c, 
						  m, -1, 
						  "Precondition is not weaker than precondition of refined class."));
	} 

	pw.println();
	pw.print(indent + "/* precondition */");

	for (int i = 0; i < assExprs.length; i++) {
	    pw.println();
	    pw.print(indent + "if (!(");
	    pw.print(assExprs[i].getExpression());
	    pw.print(")) ");
	    pw.print(ErrorHandler.generateTrigger(GlobalFlags.PRECONDITION, 
						  c, m, 
						  assExprs[i].getExpression().getLine(), 
						  assExprs[i].getLabel()));
	} 
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

