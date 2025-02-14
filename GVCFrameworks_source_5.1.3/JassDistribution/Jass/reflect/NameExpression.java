/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.util.StringTokenizer;
import java.io.PrintWriter;
import jass.util.Set;

/**
 * This represents an name expression, the 'point-operator': Something like: point.dimension.x
 * @see Expression
 */
public class NameExpression extends Expression {

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public NameExpression() {}

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
     * @param ref
     * @param locals
     *
     * @see
     */
    public void addChildren(String ref, Set locals) {
	StringTokenizer st = new StringTokenizer(ref, ".", false);
	Expression[]    help = 
	    new Expression[children.length + st.countTokens()];

	for (int i = 0; i < children.length; i++) {
	    help[i] = children[i];
	}

	for (int i = children.length; i < help.length; i++) {
	    help[i] = new SimpleNameExpression(st.nextToken());

	    help[i].setContainer(container);
	    help[i].setLine(line);
	    ((SimpleNameExpression) help[i]).setLocals(locals);

	    // System.out.println("#SimpleNameExpression: " + help[i]);
	} 

	// explicite garbage collect
	children = null;
	children = help;
	help = null;
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    public void addChild(Expression e) {
	Expression[] help = new Expression[children.length + 1];

	for (int i = 0; i < children.length; i++) {
	    help[i] = children[i];
	}

	help[children.length] = e;

	// explicite garbage collect
	children = null;
	children = help;
	help = null;
    } 

    /**
     * Method declaration
     *
     *
     * @param ae
     *
     * @see
     */
    public void insertMethodCall(ArgumentExpression ae) {
	children[children.length - 1] = 
	    new MethodCallExpression(((SimpleNameExpression) children[children.length - 1]).getName(), 
				     ae);

	children[children.length - 1].setContainer(container);
	children[children.length - 1].setLine(line);
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    public void insertArrayAccess(Expression e) {

	// System.out.println(">>>>>>>>>>AHHHHH");
	if (children[children.length - 1] instanceof ArrayAccessExpression) {
	    ((ArrayAccessExpression) children[children.length - 1]).addChild(e);
	} else {
	    children[children.length - 1] = 
		new ArrayAccessExpression(((Expression) children[children.length - 1]), 
					  e);

	    children[children.length - 1].setContainer(container);
	    children[children.length - 1].setLine(line);
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
    public String getName() {
	StringBuffer sb = new StringBuffer();
	boolean      first = true;

	for (int i = 0; i < children.length; i++) {
	    if (!first) {
		sb.append(".");
	    } else {
		first = false;
	    } 

	    sb.append(children[i].toString());
	} 

	return sb.toString();
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
	return getName();
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
	StringBuffer sb = new StringBuffer();
	boolean      first = true;

	for (int i = 0; i < children.length; i++) {
	    if (!first) {
		sb.append(".");
	    } else {
		first = false;
	    }

	    sb.append(children[i].toHTML());
	} 

	return sb.toString();
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
	StringBuffer sb = new StringBuffer();
	boolean      first = true;

	for (int i = 0; i < children.length; i++) {
	    if (!first) {
		sb.append(".");
		sb.append(children[i].toStringWithChangedContext(m_old, 
			m_new, ref, true));
	    } else {
		first = false;

		if (children[i] instanceof SimpleNameExpression 
			&& ((SimpleNameExpression) children[i]).getName().equals("this")) {
		    sb.append(ref);
		} else {
		    if (children[i] instanceof SimpleNameExpression 
			    && (((SimpleNameExpression) children[i]).getName().equals("Old") 
				|| ((SimpleNameExpression) children[i]).getName().equals("Result"))) {
			sb.append(children[i].toStringWithChangedContext(m_old, 
				m_new, ref, true));
		    } else {
			sb.append(ref + "." 
				  + children[i].toStringWithChangedContext(m_old, 
				  m_new, ref, true));
		    }
		} 
	    } 
	} 

	return sb.toString();
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
	pw.println(indent + "Name [" + type + "]");

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
	Context argContext = c;
	Context current_type = c;
	Class   container;

	if (c instanceof Method) {
	    container = (Class) ((Method) c).getContainer();
	} else {
	    container = (Class) c;
	}

	int start_loop = 0;

	// is this NameExpression a typename ?
	if (children[0] instanceof SimpleNameExpression) {

	    // a "short" type name will be found here ...
	    String classname = 
		NameAnalysis.expandTypeName(children[0].toString(), 
					    container);

	    if (classname != null) {
		start_loop++;
		current_type = ClassPool.getClass(classname);
	    } 

	    // type is full qualified ?
	    else {
		try {
		    int		 loop = 1;
		    StringBuffer sb = 
			new StringBuffer(children[0].toString());

		    while (loop < children.length 
			   && children[loop] instanceof SimpleNameExpression 
			   &&!NameAnalysis.lookUpInClassPath(sb.toString())) {
			sb.append(".");
			sb.append(children[loop++]);
		    } 

		    if (NameAnalysis.lookUpInClassPath(sb.toString())) {
			start_loop = loop;
			current_type = ClassPool.getClass(sb.toString());
		    } 
		} catch (java.io.IOException e) {
		    System.out.println(e);    // ????
		} 
	    } 
	} 

	for (int i = start_loop; i < children.length; i++) {
	    if (children[i] instanceof MethodCallExpression) {
		((MethodCallExpression) children[i]).reflectExpression(current_type, 
			argContext, dc);
	    } else if (children[i] instanceof ArrayAccessExpression) {
		((ArrayAccessExpression) children[i]).reflectExpression(current_type, 
			argContext, dc);
	    } else {
		children[i].reflectExpression(current_type, dc);
	    }

	    // type checking:
	    // all children except last must be of reference type !
	    if (i != children.length - 1 && children[i].isPrimitive()) {
		throw new ReflectExpressionError(" <Found " + children[i] 
						 + " which has primitive, not reference type in reference expression.>", this);
	    } 

	    current_type = children[i].getType();

	    // this error can not happen, probably
	    if (!(current_type instanceof Class) 
		    && children[i].isReference()) {
		System.err.println("Fatal Error: Expression " + children[i] 
				   + "has declared its type not as class !");
		System.exit(1);
	    } 

	    // if (current_type == null && children[i].isReference()) throw new UnreflectedClassException(((Class)current_type).getName());
	} 

	if (children[children.length - 1].isReference()) {
	    type = (Class) current_type;
	} else {
	    type = children[children.length - 1].getType();
	}

	// last one denotes the lvalue if present
	lvalue = children[children.length - 1].getLValue();
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

