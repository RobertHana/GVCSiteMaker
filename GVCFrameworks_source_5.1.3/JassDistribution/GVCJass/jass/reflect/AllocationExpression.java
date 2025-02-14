/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.PrintWriter;

/**
 * This represents an allacation expression. Something like: new ....
 * @see Expression
 */
public class AllocationExpression extends Expression {

    /**
     * Constructor declaration
     * 
     * 
     * @param _type
     * @param rside
     * 
     * @see
     */
    public AllocationExpression(Class _type, Expression rside) {
	type = _type;
	children = new Expression[1];
	children[0] = rside;
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
	pw.println(indent + "AllocationExpression [" + type + "]");

	for (int i = 0; i < children.length; i++) {
	    children[i].dump(pw, indent + "  ");
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
	if (children[0] instanceof ArgumentExpression) {

	    // analyse the arguments, find the constructor ...
	    Expression[] args = children[0].getChildren();
	    Class[]      params = new Class[args.length];

	    for (int i = 0; i < args.length; i++) {
		args[i].reflectExpression(c, dc);

		params[i] = args[i].getType();
	    } 

	    /* String conname = type.getName().substring(type.getName().lastIndexOf('.')+1,type.getName().length()); */
	    Constructor con = type.getConstructor(params);

	    if (con != null) {
		dc.addCreates(new Creation(type, con));
		dc.addCalls(con);
	    } else {
		StringBuffer sb = new StringBuffer(type.getName() + "(");
		boolean      first = true;

		for (int i = 0; i < params.length; i++) {
		    if (!first) {
			sb.append(",");
		    } else {
			first = false;
		    } 

		    sb.append(params[i].getName());
		} 

		sb.append(")");

		throw new ReflectExpressionError("<No constructor " 
						 + sb.toString() 
						 + " found in " 
						 + type.getName() + ".>", 
						 this);
	    } 
	} else {

	    // System.out.println(">>> "+children[0]);
	    children[0].reflectExpression(c, dc);

	    // check array brackets ????
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

