/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.PrintWriter;
import jass.reflect.ErrorHandler;
import jass.GlobalFlags;

/**
 * Class represents a Loopinvariant and supports methods to generate code for while, do and for loops.
 * A loopnumber identifies the loop(invariant) in a method.
 */
public class LoopInvariant extends BooleanAssertion {
    private int loop_number;

    /**
     * Constructs a new Loopinvariant with given number.
     * @param nr the number of the loop
     */
    public LoopInvariant(int nr) {
	loop_number = nr;

	/**
	 * ensure loop_number >= 0;
	 */
    }

    /**
     * Sets the loopnumber.
     * @param nr the number of the loop
     */
    public void setLoopNumber(int nr) {
	loop_number = nr;

	/**
	 * ensure loop_number >= 0
	 */
    } 

    /**
     * Gets the loopnumber.
     * @return the number of the loop
     */
    public int getLoopNumber() {
	return loop_number;
    } 

    /**
     * Generates the basic code for the loopinvariant (while and do).
     * @param pw the PrintWriter wich produces the output
     * @indent the current indent of the produced code
     */
    public void generateCodeWhileDo(PrintWriter pw, String indent) {
	for (int i = 0; i < assExprs.length; i++) {
	    pw.println();
	    pw.print(indent + "if(!(" + assExprs[i].getExpression() + ")) ");
	    pw.print(ErrorHandler.generateTrigger(GlobalFlags.LOOPINVARIANT_WHILE_AND_DO, 
						  ((Method) container).getDeclaringClass(), 
						  ((Method) container), 
						  assExprs[i].getExpression().getLine(), 
						  assExprs[i].getLabel()));
	} 
    } 

    /**
     * Generates the basic code for the loopinvariant (for).
     * @param pw the PrintWriter wich produces the output
     * @param indent the current indent of the produced code
     * @param first a flag, if the test apears in front of the for head
     */
    public void generateCodeFor(PrintWriter pw, String indent, 
				boolean first) {
	for (int i = 0; i < assExprs.length; i++) {
	    if (!first) {
		pw.print(",");
	    } else {
		first = false;
	    }

	    pw.print("jassCheckLoop(" + assExprs[i].getExpression() + "," 
		     + ErrorHandler.generateTrigger(GlobalFlags.LOOPINVARIANT_FOR, ((Method) container).getDeclaringClass(), ((Method) container), assExprs[i].getExpression().getLine(), assExprs[i].getLabel()) 
		     + ")");
	    pw.println();
	    pw.print(indent);
	} 
    } 

    /**
     * Produces a human readable string of the loopinvariant.
     * @return the string
     */
    public String toString() {
	StringBuffer sb = new StringBuffer();

	sb.append("LoopInvariant (" + loop_number + ") [");

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

