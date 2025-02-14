/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class Source {
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.Source";
    String		       filename;
    int			       lineNr;

    /**
     * Constructor declaration
     *
     *
     * @param filename
     * @param lineNr
     *
     * @see
     */
    public Source(String filename, int lineNr) {
	this.filename = filename;
	this.lineNr = lineNr;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getFilename() {
	return filename;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public int getLineNr() {
	return lineNr;
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
	String javaCode = "";

	javaCode += "new " + CLASSNAME + "(\"" + filename + "\", " + lineNr 
		    + ")";

	return javaCode;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

