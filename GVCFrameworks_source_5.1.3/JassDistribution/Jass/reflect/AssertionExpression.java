/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

/**
 * This class is the base for all Jass assertions. It is a normal 
 * expression extended by an optional label. 
 *
 * @author Detlef Bartetzko
 * @version %I%, %G%
 */
public class AssertionExpression extends Expression {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    protected AssertionLabel label;

    /**
     * @return  the label of this assertion
     */
    public AssertionLabel getLabel() {
	return label;
    } 

    /**
     * Set the label of this assertion
     * @param label  the new label 
     */
    public void setLabel(AssertionLabel label) {
	this.label = label;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

