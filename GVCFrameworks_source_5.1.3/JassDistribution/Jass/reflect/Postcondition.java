/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.util.*;
import java.io.*;
import jass.reflect.*;
import jass.reflect.ErrorHandler;
import jass.GlobalFlags;
import jass.util.Set;

/**
 * This class reflects the Jass postcondition assertion
 *
 *
 * @author  Detlef Bartetzko
 * @version %I%, %G%
 */
public class Postcondition extends BooleanAssertion {

    /**
     * The changelist in form of expression, this can only be SimpleNames,
     * but we the reflection of the expression class is used.
     */
    protected Expression[]	  changeList = new Expression[0];
    protected boolean		  changeAll = true;
    protected DependencyCollector cl_dc = new DependencyCollector();

    /**
     * A refined postcondition; null if there is no refinement
     */
    protected Postcondition       refines;
    protected Set		  changedFields = null;

    /**
     * Set the list of members, that are allowed to be modified (the
     * changelist).
     *
     * @param cl the changelist to use
     */
    public void setChangeList(Vector cl) {
	changeList = new Expression[cl.size()];
	cl.copyInto(changeList);
	changeAll = false;
    } 

    /**
     * Get the list of members, that are allowed to be modified (the
     * changelist).
     *
     * @return the chnagelist of this postcondition
     */
    public Expression[] getChangeList() {
	return changeList;
    } 

    /**
     * Return whether everything is changable. This is the case, when
     * no changelist is given (by changeonly), abbreviating a changelist
     * containing everything.
     *
     * @return true, if everything is changable 
     *
     * @see setChangeAll(boolean)
     */
    public boolean isChangeAll() {
	return changeAll;
    } 

    /**
     * Set the flag for an empty changelist. 
     *
     * @param b  the new flag.
     *
     * @see boolean isChangeAll()
     */
    public void setChangeAll(boolean b) {
	changeAll = b;
    } 

    /**
     * Convert this assertion to a String
     *
     * @return  the String representation
     */
    public String toString() {
	StringBuffer sb = new StringBuffer();

	sb.append("Postcondition [");

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

	    sb.append(" | ");
	} 

	sb.append("changeList:" + changeList);
	sb.append("]");

	return sb.toString();
    } 

    /**
     * Return an HTML representation of the postcondition. This method will 
     * add the changeonly list to the representation. The result will not 
     * include any additional information (e.g. that it is the HTML 
     * representation of a postcondition).
     * 
     * @return an HTML representation of this postcondition
     */
    public String toHTML() {
	if (isChangeAll()) {
	    return super.toHTML();
	}
	StringBuffer html = new StringBuffer("<b>changeonly</b> {");
	for (int i = 0; i < changeList.length; i++) {
	    if (i != 0) {
		html.append(", ");
	    }
	    html.append(changeList[i].toHTML());
	}
	html.append(" }; <br>").append(GlobalFlags.NEWLINE)
	    .append(super.toHTML());

	return html.toString();
    }
 

    /**
     * used for reflection(?)
     *
     * @see super.reflectExpressions()
     */
    public void reflectExpressions() {
	super.reflectExpressions();

	for (int i = 0; i < changeList.length; i++) {
	    changeList[i].reflectExpression(container, cl_dc);
	} 

	Class  c = ((Method) container).getDeclaringClass();
	Method m = ((Method) container);

	if (uses_Locals) {
	    throw new ReflectionError(c.getName() + "." + m.getIdString() 
				      + ":" + getLineString() 
				      + " <No local variables are allowed in postconditons!>");
	} 

	Object[] os = cl_dc.getReads().elements();

	for (int i = 0; i < os.length; i++) {
	    if (!(os[i] instanceof Field)) {
		throw new ReflectionError(c.getName() + "." + m.getIdString() 
					  + ":" + getLineString() 
					  + " <Names in changeonly-list may only refer to fields!>");
	    } 

	    if (container.getField((Field) os[i]) == null) {
		throw new ReflectionError(c.getName() + "." + m.getIdString() 
					  + ":" + getLineString() 
					  + " <Field in changeonly-list must be attributs in current class.>");
	    } 
	} 

	if ((uses_jassOld ||!isChangeAll()) 
		&&!c.doesImplement(ClassPool.getClass("java.lang.Cloneable"))) {
	    throw new ReflectionError(c.getName() + "." + m.getIdString() 
				      + ":" + getLineString() 
				      + " <Class must implement java.lang.Cloneable if jassOld or changeonly is used.>");
	} 
    } 

    /**
     * Generates the Code.
     */
    public void generateCode(PrintWriter pw, String indent) {
	Class  c = ((Method) container).getDeclaringClass();
	Method m = ((Method) container);

	// refines c his superclass and overwrittes this method a method in the superclass ?
	if (refines != null && GlobalFlags.GENERATE_REFINE 
		&& assExprs.length > 0) {
	    pw.println();
	    pw.println(indent + "/* refinement */");

	    // already declared jassSuperState ?
	    if (m.getPrecondition() != null 
		    && m.getPrecondition().getRefinedPrecondition() != null) {
		pw.println(indent + "jassSuperState = jassGetSuperState();");
	    } else {
		pw.println(indent + c.getSuperclass().getName() 
			   + " jassSuperState = jassGetSuperState();");
	    }

	    // pre(A) && !pre(C) --> Exception
	    BooleanAssertionExpression[] superExprs = 
		refines.getAssertionExpressions();
	    boolean			 first = true;

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

		pw.print(e.toStringWithChangedContext((Method) refines.getContainer(), 
						      m, "jassSuperState", 
						      false));
	    } 

	    pw.print(")");
	    pw.print("&&(");

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
						  "Postcondition of refined class is not weaker than postcondition of concrete class."));
	} 

	// generate the check code for the postcondition
	if (assExprs.length > 0 ||!isChangeAll()) {
	    pw.println();
	    pw.print(indent + "/* postcondition */");
	} 

	for (int i = 0; i < assExprs.length; i++) {
	    pw.println();
	    pw.print(indent + "if (!(");
	    pw.print(assExprs[i].getExpression());
	    pw.print(")) ");
	    pw.print(ErrorHandler.generateTrigger(GlobalFlags.POSTCONDITION, 
						  c, m, 
						  assExprs[i].getExpression().getLine(), 
						  assExprs[i].getLabel()));
	} 

	// generate changelist code
	if (!isChangeAll()) {
	    if (GlobalFlags.OPTIMIZING) {
		if (!changedFields.empty()) {
		    Object[] fields = changedFields.elements();
		    boolean  first = true;

		    for (int j = 0; j < fields.length; j++) {
			if (first) {
			    pw.println();
			    pw.print(indent + "if (!(");

			    first = false;
			} else {
			    pw.print(" && ");
			}

			if (((Field) fields[j]).isPrimitive()) {
			    pw.print(((Field) fields[j]).getName() 
				     + " == jassOld." 
				     + ((Field) fields[j]).getName());
			} else if (((Field) fields[j]).getType().isArray()) {
			    pw.print("jass.runtime.Tool.arrayEquals(" 
				     + ((Field) fields[j]).getName() 
				     + ",jassOld." 
				     + ((Field) fields[j]).getName() + ")");
			} else {
			    pw.print("jass.runtime.Tool.referenceEquals(" 
				     + ((Field) fields[j]).getName() 
				     + ",jassOld." 
				     + ((Field) fields[j]).getName() + ")");
			}
		    } 

		    pw.print(")) ");
		    pw.print(ErrorHandler.generateTrigger(GlobalFlags.POSTCONDITION, 
							  c, m, -1, 
							  "Method has changed old value."));
		} else {
		    pw.println();
		    pw.print(indent 
			     + "/* changelist check dropped through optimizing */");
		} 
	    } else {

		// no optimizing !
		boolean something_done = false;
		Field[] fields = c.getDeclaredFields();
		boolean first = true;

		for (int j = 0; j < fields.length; j++) {
		    if (!cl_dc.getReads().contains(fields[j])) {
			if (first) {
			    pw.println();
			    pw.print(indent + "if (!(");

			    first = false;
			    something_done = true;
			} else {
			    pw.print(" && ");
			}

			if (((Field) fields[j]).isPrimitive()) {
			    pw.print(((Field) fields[j]).getName() 
				     + " == jassOld." 
				     + ((Field) fields[j]).getName());
			} else if (((Field) fields[j]).getType().isArray()) {
			    pw.print("jass.runtime.Tool.arrayEquals(" 
				     + ((Field) fields[j]).getName() 
				     + ",jassOld." 
				     + ((Field) fields[j]).getName() + ")");
			} else {
			    pw.print("jass.runtime.Tool.referenceEquals(" 
				     + ((Field) fields[j]).getName() 
				     + ",jassOld." 
				     + ((Field) fields[j]).getName() + ")");
			}
		    } 
		} 

		fields = c.getInheritedFields();

		for (int j = 0; j < fields.length; j++) {
		    if (!cl_dc.getReads().contains(fields[j])) {
			if (first) {
			    pw.println();
			    pw.print(indent + "if (!(");

			    first = false;
			    something_done = true;
			} else {
			    pw.print(" && ");
			}

			if (((Field) fields[j]).isPrimitive()) {
			    pw.print(((Field) fields[j]).getName() 
				     + " == jassOld." 
				     + ((Field) fields[j]).getName());
			} else if (((Field) fields[j]).getType().isArray()) {
			    pw.print("jass.runtime.Tool.arrayEquals(" 
				     + ((Field) fields[j]).getName() 
				     + ",jassOld." 
				     + ((Field) fields[j]).getName() + ")");
			} else {
			    pw.print("jass.runtime.Tool.referenceEquals(" 
				     + ((Field) fields[j]).getName() 
				     + ",jassOld." 
				     + ((Field) fields[j]).getName() + ")");
			}
		    } 
		} 

		if (something_done) {
		    pw.print(")) ");
		    pw.print(ErrorHandler.generateTrigger(GlobalFlags.POSTCONDITION, 
							  c, m, -1, 
							  "Method has changed old value."));
		} 
	    } 
	} 
    } 

    /**
     * Set the reference to a refined postcondition.
     *
     * @param p  the refined postcondition
     *
     * @see getRefinedPostcondition()
     */
    public void setRefinedPostcondition(Postcondition p) {
	refines = p;
    } 

    /**
     * Get the reference to a refined postcondition.
     *
     * @return  the refined postcondition
     *
     * @see setRefinedPostcondition()
     */
    public Postcondition getRefinedPostcondition() {
	return refines;
    } 

    /**
     * Does the refined postcondition use the Old keyword
     *
     * @return  true, if the refined postcondition uses Old. Returns false 
     *          if the refined postcondition does not use Old or if this 
     *          postcondition has no refined postcondition.
     *
     * @see getRefinedPostcondition()
     */
    public boolean refinedUsesJassOld() {
	if (refines == null) {
	    return false;
	} 

	return refines.usesJassOld();
    } 

    /**
     * optimizing changed fields (?)
     *
     * @return  unknown
     *
     */
    public boolean dropsChangeList() {
	if (changedFields != null) {
	    return changedFields.empty() && GlobalFlags.OPTIMIZING;
	} else {
	    return false;
	}
    } 

    /**
     * optimizing changed fields (?)
     *
     * @see getChangedFields(), dropsChangeList()
     */
    public void determineChangedFields() {
	if (GlobalFlags.OPTIMIZING && changedFields == null) {
	    if (!isChangeAll()) {
		changedFields = getChangedFields();
	    } else {
		changedFields = new Set();
	    }
	} 
    } 

    /**
     * optimizing changed fields (?)
     *
     * @return  the set of changed fields
     *
     * @see determineChangedFields()
     */
    public Set getChangedFields() {
	Method		     m = (Method) container;
	Class		     c = m.getDeclaringClass();
	Set		     s = new Set();
	DataFlow	     df = new DataFlow();
	DependencyCollector  m_dc = df.analyseDataFlow(m);
	jass.reflect.Field[] fields = c.getDeclaredFields();

	for (int j = 0; j < fields.length; j++) {
	    if (

	    // field not in changeonly list
	    !cl_dc.getReads().contains(fields[j]) 

	    // method writes field
	    && m_dc.getWrites().contains(fields[j])) {
		s.addElement(fields[j]);
	    } 
	}

	fields = c.getInheritedFields();

	for (int j = 0; j < fields.length; j++) {
	    if (

	    // field not in changeonly list
	    !cl_dc.getReads().contains(fields[j]) 

	    // method writes field
	    && m_dc.getWrites().contains(fields[j])) {
		s.addElement(fields[j]);
	    } 
	}

	return s;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

