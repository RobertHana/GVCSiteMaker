/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.PrintWriter;
import jass.reflect.ErrorHandler;
import jass.reflect.traceAssertion.TraceAssertion;
import jass.GlobalFlags;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class Check extends BooleanAssertion {
    protected int check_number;

    /**
     * Constructor declaration
     *
     *
     * @param nr
     *
     * @see
     */
    public Check(int nr) {
	check_number = nr;
    }

    /**
     * Method declaration
     *
     *
     * @param nr
     *
     * @see
     */
    public void setCheckNumber(int nr) {
	check_number = nr;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public int getCheckNumber() {
	return check_number;
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

	pw.println();
	pw.print(indent + "/* check */");

	for (int i = 0; i < assExprs.length; i++) {
	    BooleanAssertionExpression assertionExpression = assExprs[i];
	    ;
	    AssertionLabel	       assertionLabel = 
		assertionExpression.getLabel();

	    pw.println();
	    pw.print(indent + "if (!(");
	    pw.print(assertionExpression.getExpression());
	    pw.print(")) ");
	    pw.print(ErrorHandler.generateTrigger(GlobalFlags.CHECK, c, m, 
						  assertionExpression.getExpression().getLine(), 
						  assertionLabel));

	    /*
	     * TraceAssertion traceAssertion = clazz.getTraceAssertion();
	     * 
	     * if
	     * (
	     * assertionLabel != null && assertionLabel.hasParameters()
	     * )
	     * {
	     * pw.println();
	     * pw.println(indent + assertionLabel.toJava());
	     * }
	     */
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
	StringBuffer sb = new StringBuffer();

	sb.append("Check (" + check_number + ") [");

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

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

