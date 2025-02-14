/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import jass.reflect.Class;
import jass.reflect.Method;
import jass.reflect.*;
import jass.GlobalFlags;

/**
 * This class encapsulate the error event creation for all assertions. In mode CONTRACT the created error is an exception
 * in mode WARNING the error is an output on the terminal.
 */
public class ErrorHandler {

    /**
     * This method generates a code string that produces an exception or an output on the terminal via System.out.
     * Its used by all assertions to generate the demanded code string.
     * @param type the exception/message type that should be produced, constants of GlobalFlags are used
     * @param c the class in which the error occured
     * @param m the method in which the error occured, may be null
     * @param line the line in which the error occured, may -1 if not known
     * @param label the label of the error, may be null
     * @return the proper code string
     */
    public static String generateTrigger(int type, Class c, Method m, 
					 int line, String label) {

	/**
	 * require c != null && ( line == -1 || line > 0 )
	 */
	if (type < GlobalFlags.contract_strings.length) {
	    switch (type) {

	    case GlobalFlags.LOOPINVARIANT_FOR:

	    case GlobalFlags.LOOPVARIANT_FOR:
		if (GlobalFlags.MODE == GlobalFlags.CONTRACT) {
		    return "new " + GlobalFlags.contract_strings[type] 
			   + "(\"" + c.getName() + "\",\"" + m.getIdString() 
			   + "\"," + line + "," 
			   + (label != null ? "\"" + label + "\"" : null) 
			   + ")";
		} else {
		    return "\"" + GlobalFlags.warning_strings[type] + ":" 
			   + c.getName() + "." + m.getIdString() 
			   + (line > 0 ? ":" + line : "") 
			   + (label != null ? " <" + label + ">" : "") + "\"";
		}

	    case GlobalFlags.INVARIANT:
		if (GlobalFlags.MODE == GlobalFlags.CONTRACT) {
		    return "throw new " + GlobalFlags.contract_strings[type] 
			   + "(\"" + c.getName() + "\"," 
			   + (m != null ? "\"" + m.getIdString() + "\"" : null) 
			   + "," + line + ",\"Exception occured \"+" + label 
			   + "\");";
		} else {
		    return "System.out.println(\"" 
			   + GlobalFlags.warning_strings[type] + ":" 
			   + c.getName() 
			   + (m != null ? "." + m.getIdString() : "") 
			   + (line > 0 ? ":" + line : "") 
			   + " <Violation occured \"+" + label + ">\");";
		}

	    default:
		if (GlobalFlags.MODE == GlobalFlags.CONTRACT) {
		    return "throw new " + GlobalFlags.contract_strings[type] 
			   + "(\"" + c.getName() + "\"," 
			   + (m != null ? "\"" + m.getIdString() + "\"" : null) 
			   + "," + line + "," 
			   + (label != null ? "\"" + label + "\"" : null) 
			   + ");";
		} else {
		    return "System.out.println(\"" 
			   + GlobalFlags.warning_strings[type] + ":" 
			   + c.getName() 
			   + (m != null ? "." + m.getIdString() : "") 
			   + (line > 0 ? ":" + line : "") 
			   + (label != null ? " <" + label + ">" : "") 
			   + "\");";
		}
	    }
	} 

	// nothing matches ? return empty statement*/
	else {
	    return "/* assertion type not supported by jass */;";
	}

	/**
	 * ensures Result != null
	 */
    } 

    /**
     * @see generateTrigger(int, Class, Method, int, String)
     */
    public static String generateTrigger(int type, Class c, Method m, 
					 int line, 
					 AssertionLabel assertionLabel) {
	String label;

	label = (assertionLabel == null ? null : assertionLabel.getName());

	return generateTrigger(type, c, m, line, label);
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

