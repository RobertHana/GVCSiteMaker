/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.PrintWriter;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class Literal extends Expression {
    protected String image;

    /**
     * Constructor declaration
     *
     *
     * @param image
     *
     * @see
     */
    public Literal(String image) {
	this.image = image;
    }

    /**
     * Method declaration
     *
     *
     * @param s
     *
     * @see
     */
    public void setImage(String s) {
	image = s;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getImage() {
	return image;
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

	// print out children (can only be CastExpression)
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toString());
	} 

	sb.append(image);

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
    public String toHTML() {

	// print out children (can only be CastExpression)
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toHTML());
	} 

	sb.append(image);

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

	// print out children (can only be CastExpression)
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < children.length; i++) {
	    sb.append(children[i].toStringWithChangedContext(m_old, m_new, 
		    ref, referenced));
	} 

	sb.append(image);

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
	pw.println(indent + "Literal: [" + type + "] " + image);

	for (int i = 0; i < children.length; i++) {
	    children[i].dump(pw, indent + "  ");
	} 
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

