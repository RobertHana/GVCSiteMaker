/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ConstructorCallExpression extends Expression {
    protected String cl;

    /**
     * Constructor declaration
     *
     *
     * @param cl
     * @param e
     *
     * @see
     */
    public ConstructorCallExpression(String cl, ArgumentExpression e) {
	children = e.getChildren();
	this.cl = cl;
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

	// reflect arguments and construct parameter array
	Class[] params = new Class[children.length];

	for (int i = 0; i < children.length; i++) {
	    children[i].reflectExpression(c, dc);

	    params[i] = children[i].getType();
	} 

	Class where;

	if (cl.equals("super")) {
	    where = container.getSuperclass();

	    // can not happen !!!! ????
	    if (where == null) {
		throw new ReflectExpressionError(" <Invocation of superclass constructor, but no superclass found.>", 
						 this);
	    } 
	} else {
	    where = container;
	}

	/* String conname = where.getName().substring(where.getName().lastIndexOf('.')+1,where.getName().length()); */
	Constructor con = where.getConstructor(params);

	if (con == null) {
	    StringBuffer sb = new StringBuffer(where.getName() + "(");
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

	    throw new ReflectExpressionError(" <Constructor " + sb.toString() 
					     + " was not found in class " 
					     + where.getName() + ".>", this);
	} 

	dc.addCalls(con);
	((Constructor) c).setConstructorCall(con);
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

