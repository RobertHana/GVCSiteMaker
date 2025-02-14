/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import jass.parser.JassParserConstants;
import jass.reflect.Class;
import jass.reflect.*;
import jass.runtime.util.ToString;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ExchangeParameter extends CommunicationParameter 
    implements JassParserConstants, FileBounded {
    SimpleNameExpression nameExpression;

    /**
     * Constructor declaration
     *
     *
     * @param unreflectedType
     * @param nameExpression
     *
     * @see
     */
    public ExchangeParameter(int unreflectedType, 
			     SimpleNameExpression nameExpression) {
	this.unreflectedType = unreflectedType;
	this.nameExpression = nameExpression;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isInputParameter() {
	return unreflectedType == UNREFLECTEDTYPE_INPUT;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isOutputParameter() {
	return !isInputParameter();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isNull() {
	return false;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isWildcard() {
	return false;
    } 

    // interface FileBounded

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Class getContainer() {
	return nameExpression.getContainer();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public int getLine() {
	return nameExpression.getLine();
    } 

    // public SimpleNameExpression getExpression(){return nameExpression;}

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getName() {
	return nameExpression.getName();
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	nameExpression.reflectExpression(nameExpression.getContainer(), 
					 new DependencyCollector());

	this.type = nameExpression.getType();
	this.typename = type.getName();
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
	String string = "";
	String tokenString;

	if (isInputParameter()) {
	    tokenString = ToString.token(HOOK);
	} else {
	    tokenString = ToString.token(BANG);
	} 

	string += tokenString;
	string += getName();

	return string;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

