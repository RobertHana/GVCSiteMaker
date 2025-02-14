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
public class ToString {
    public static final String NEWLINE = System.getProperty("line.separator");

    /**
     * Get the String representation for a token id.
     *
     * @param kind  the token id
     * @return  the String representation
     *
     * @see JassParserConstants
     */
    public static String token(int kind) {
	String string = "";
	String tokenImage = JassParserConstants.tokenImage[kind];

	string += tokenImage.substring(1, tokenImage.length() - 1);

	return string;
    } 

    /**
     * Retrun the sequence of tokens of a node in the syntax tree as String
     *
     * @param node  the node of the syntax tree
     * @return  the token sequence as String
     *
     * @see tokenSequence
     */
    public static String node(Node node) {
	return tokenSequence(node.getFirstToken(), node.getLastToken());
    } 

    /**
     * Return a sequence of tokens as one String
     *
     * @param firstToken  the token that starts the sequence
     * @param lastToken   the token that stops the sequence
     * @return  the token sequence as String
     */
    public static String tokenSequence(Token firstToken, Token lastToken) {
	String javaCode = "";
	Token  token = firstToken;

	if (token != null) {
	    javaCode += token.image;

	    while (token != lastToken) {
		token = token.next;

		Token  specialToken = token.specialToken;
		String specialImages = "";

		while (specialToken != null) {
		    specialImages = specialToken.image + specialImages;
		    specialToken = specialToken.specialToken;
		} 

		javaCode += specialImages;
		javaCode += token.image;
	    } 
	} 

	return javaCode;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

