/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import jass.GlobalFlags;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class Constructor extends Method {
    protected Constructor		exCall = null;
    protected ConstructorCallExpression e = null;

    /**
     * Method declaration
     *
     *
     * @param c
     *
     * @see
     */
    public void init(java.lang.reflect.Constructor c) {
	type = ClassPool.getClass(c.getDeclaringClass().getName());

	java.lang.Class[] pt = c.getParameterTypes();

	formalParameters = new FormalParameter[pt.length];

	for (int i = 0; i < pt.length; i++) {
	    formalParameters[i] = new FormalParameter();

	    formalParameters[i].init(pt[i]);
	} 

	modifiers = Modifier.convert(c.getModifiers());
	name = c.getName();
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    public void setConstructorCall(ConstructorCallExpression e) {
	this.e = e;
    } 

    /**
     * Method declaration
     *
     *
     * @param c
     *
     * @see
     */
    public void setConstructorCall(Constructor c) {
	this.exCall = c;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	if (e != null) {
	    e.reflectExpression(this, dc);
	} else {
	    Class sc = ((Class) container).getSuperclass();

	    if (sc != null) {
		Constructor con = sc.getDeclaredConstructor(sc.getName() 
							    + "()");

		if (con != null) {
		    dc.addCalls(con);
		} 

		/*
		 * else
		 * {
		 * throw new ReflectionError(container.getName()+" <No default constructor found in "+sc.getName()+".>");
		 * }
		 */
	    } 
	} 

	super.reflect();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Class getReturnType() {
	return ClassPool.Void;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean hasExpliciteConstructorCall() {
	return exCall != null;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

