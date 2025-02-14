/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.util;

import java.io.InputStream;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class StringInputStream extends InputStream {
    String string;
    int    currentPosition;

    /**
     * Constructor declaration
     *
     *
     * @param string
     *
     * @see
     */
    StringInputStream(String string) {
	this.string = string;
	currentPosition = 0;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public int read() {
	if (currentPosition < string.length()) {
	    return (string.charAt(currentPosition++));
	} else {
	    return (-1);
	} 
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

