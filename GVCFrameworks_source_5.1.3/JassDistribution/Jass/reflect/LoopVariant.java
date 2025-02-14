/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.PrintWriter;
import jass.reflect.ErrorHandler;
import jass.GlobalFlags;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class LoopVariant extends BooleanAssertion {
    protected int loop_number;

    /**
     * Constructor declaration
     *
     *
     * @param nr
     *
     * @see
     */
    public LoopVariant(int nr) {
	loop_number = nr;
    }

    /**
     * Method declaration
     *
     *
     * @param nr
     *
     * @see
     */
    public void setLoopNumber(int nr) {
	loop_number = nr;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public int getLoopNumber() {
	return loop_number;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Expression getExpression() {
	return assExprs[0];
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    public void setExpression(Expression e) {
	assExprs = new BooleanAssertionExpression[1];

	BooleanAssertionExpression ae = new BooleanAssertionExpression(e);

	ae.setLabel(new AssertionLabel("loop variant"));

	assExprs[0] = ae;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflectExpressions() {
	super.reflectExpressions();

	if (assExprs[0].getType() != ClassPool.Int) {
	    throw new ReflectionError(container.getUnitName() 
				      + (container instanceof Method ? "." + ((Method) container).getIdString() : "") 
				      + ":" + assExprs[0].getLine() 
				      + " <Expression in loopvariant must be of type int: " 
				      + assExprs[0] + ">");
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param pw
     * @param indent
     * @param number
     *
     * @see
     */
    public void generateCodeWhileDoZero(PrintWriter pw, String indent, 
					int number) {
	pw.print(indent + "if(!(jassVariant" 
		 + (number > 1 ? new Integer(number).toString() : "") 
		 + ">=0)) " 
		 + ErrorHandler.generateTrigger(GlobalFlags.LOOPVARIANT_WHILE_AND_DO, 
						((Method) container).getDeclaringClass(), 
						((Method) container), 
						assExprs[0].getExpression().getLine(), 
						"Loopvariant is below zero."));
    } 

    /**
     * Method declaration
     *
     *
     * @param pw
     * @param indent
     * @param number
     *
     * @see
     */
    public void generateCodeWhileDoDec(PrintWriter pw, String indent, 
				       int number) {
	pw.print(indent + "if(!(jassVariant" 
		 + (number > 1 ? new Integer(number).toString() : "") + ">" 
		 + assExprs[0].getExpression() + ")) " 
		 + ErrorHandler.generateTrigger(GlobalFlags.LOOPVARIANT_WHILE_AND_DO, 
						((Method) container).getDeclaringClass(), 
						((Method) container), 
						assExprs[0].getExpression().getLine(), 
						"Loopvariant was not decreased."));
    } 

    /**
     * Method declaration
     *
     *
     * @param pw
     * @param indent
     * @param number
     *
     * @see
     */
    public void generateCodeForZero(PrintWriter pw, String indent, 
				    int number) {
	pw.print(",jassCheckLoop(jassVariant" 
		 + (number > 1 ? new Integer(number).toString() : "") 
		 + ">=0," 
		 + ErrorHandler.generateTrigger(GlobalFlags.LOOPVARIANT_FOR, ((Method) container).getDeclaringClass(), ((Method) container), assExprs[0].getExpression().getLine(), "Loopvariant is below zero.") 
		 + ")");
    } 

    /**
     * Method declaration
     *
     *
     * @param pw
     * @param indent
     * @param number
     *
     * @see
     */
    public void generateCodeForDec(PrintWriter pw, String indent, 
				   int number) {
	pw.print("jassCheckLoop(jassVariant" 
		 + (number > 1 ? new Integer(number).toString() : "") + ">" 
		 + assExprs[0].getExpression() + "," 
		 + ErrorHandler.generateTrigger(GlobalFlags.LOOPVARIANT_FOR, ((Method) container).getDeclaringClass(), ((Method) container), assExprs[0].getExpression().getLine(), "Loopvariant was not decreased.") 
		 + ")");
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
	return "LoopVariant (" + loop_number + ") [" + assExprs[0] + "]";
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

