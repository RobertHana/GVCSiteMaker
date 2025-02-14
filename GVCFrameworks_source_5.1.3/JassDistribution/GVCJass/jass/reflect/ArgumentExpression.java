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
public class ArgumentExpression extends Expression {

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String[] asStringArray() {
	String[] classnames = new String[getNumChildren()];

	for (int iChild = 0; iChild < classnames.length; ++iChild) {
	    classnames[iChild] = getChild(iChild).getType().getName();
	} 

	return classnames;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String toJava() {
	return toString();
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

	sb.append("(");

	boolean first = true;

	for (int i = 0; i < children.length; i++) {
	    if (!first) {
		sb.append(",");
	    } else {
		first = false;
	    }

	    if (children[i] != null) {
		sb.append(children[i].toString());
	    } 
	} 

	sb.append(")");

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
	pw.println(indent + "Arguments");

	for (int i = 0; i < children.length; i++) {
	    children[i].dump(pw, indent + "  ");
	} 
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

