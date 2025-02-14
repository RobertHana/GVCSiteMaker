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
public class Invariant extends BooleanAssertion {
    private Invariant refines = null;

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
	return sb.toString();
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflectExpressions() {
	super.reflectExpressions();

	if (uses_jassOld) {
	    throw new ReflectionError(container.getUnitName() + ":" 
				      + getLineString() 
				      + " <Variable jassOld may only be used in postconditions.>");
	} 

	if (uses_jassResult) {
	    throw new ReflectionError(container.getUnitName() + ":" 
				      + getLineString() 
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
	if (refines != null && GlobalFlags.GENERATE_REFINE) {
	    pw.println();
	    pw.println(indent + "/* refinement */");
	    pw.println(indent + ((Class) container).getSuperclass().getName() 
		       + " jassSuperState = jassGetSuperState();");

	    // pre(A) && !pre(C) --> Exception
	    BooleanAssertionExpression[] superExprs = 
		refines.getAssertionExpressions();
	    boolean			 first = true;
	    Method			 dummy = new Method();

	    pw.print(indent + "if (!(");

	    for (int i = 0; i < superExprs.length; i++) {
		Expression e = superExprs[i].getExpression();

		// map all names A that are not formal parameters to jassSuperState.A
		// formal parameters must be renamed (e.g.: i - > n)
		if (!first) {
		    pw.print("&&");
		} else {
		    first = false;
		}

		pw.print(e.toStringWithChangedContext(dummy, dummy, 
						      "jassSuperState", 
						      false));
	    } 

	    pw.print(")) ");
	    pw.print(ErrorHandler.generateTrigger(GlobalFlags.REFINEMENT, 
						  (Class) container, null, 
						  -1, 
						  "Invariant of refined class must be valid."));
	} 

	for (int i = 0; i < assExprs.length; i++) {
	    pw.println();
	    pw.print(indent + "if (!(");
	    pw.print(assExprs[i].getExpression());
	    pw.print(")) ");
	    pw.print(ErrorHandler.generateTrigger(GlobalFlags.INVARIANT, 
						  (Class) container, null, 
						  assExprs[i].getExpression().getLine(), 
						  "msg+\" (" 
						  + assExprs[i].getLabel() 
						  + ")"));
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param inv
     *
     * @see
     */
    public void setRefinedInvariant(Invariant inv) {
	refines = inv;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

