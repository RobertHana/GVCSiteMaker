/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.util;

import jass.parser.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ToJava implements JassParserConstants {
    public static final String NEWLINE = System.getProperty("line.separator");
    public static final String SEPARATOR = " ";
    public static final String INDENT1 = "  ";
    public static final String INDENT2 = INDENT1 + INDENT1;
    public static final String INDENT3 = INDENT2 + INDENT1;
    public static final String INDENT4 = INDENT3 + INDENT1;

    /**
     * Method declaration
     *
     *
     * @param token
     *
     * @return
     *
     * @see
     */
    public static String token(Token token) {
	String toJava = "";

	if (token.kind == JassParserConstants.ASSERTION_COMMENT) {
	    toJava += "/*";
	    toJava += 
		token.image.substring(tokenImage[IN_ASSERTION_COMMENT].length(), 
				      token.image.length() 
				      - tokenImage[ASSERTION_COMMENT].length());
	    toJava += "*/";
	} 

	return toJava;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     *
     * @return
     *
     * @see
     */
    public static String node(Node node) {
	return tokenSequence(node.getFirstToken(), node.getLastToken());
    } 

    /**
     * Method declaration
     *
     *
     * @param string
     *
     * @return
     *
     * @see
     */
    public static String string(String string) {
	string = StringTools.replaceSubstring(string, "\"", "\\\"");

	return string;
    } 

    /**
     * Method declaration
     *
     *
     * @param array
     *
     * @return
     *
     * @see
     */
    public static String stringArray(String[] array) {
	String toJava = "";

	toJava += "new String[]{";

	for (int i = 0; i < array.length; ++i) {
	    Object entry = array[i];

	    toJava += "\"" + string(entry.toString()) + "\"";

	    if (i + 1 < array.length) {
		toJava += ", ";
	    } 
	} 

	toJava += "}";

	return toJava;
    } 

    /**
     * Method declaration
     *
     *
     * @param firstToken
     * @param lastToken
     *
     * @return
     *
     * @see
     */
    public static String tokenSequence(Token firstToken, Token lastToken) {
	String javaCode = "";
	Token  token = firstToken;

	if (token != null) {
	    while (true) {
		Token  specialToken = token.specialToken;
		String specialImages = "";

		while (specialToken != null) {
		    specialImages = specialToken.image + specialImages;
		    specialToken = specialToken.specialToken;
		} 

		javaCode += specialImages;
		javaCode += token.image;

		if (token == lastToken) {
		    break;
		} else {
		    token = token.next;
		} 
	    } 
	} 

	return javaCode;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

