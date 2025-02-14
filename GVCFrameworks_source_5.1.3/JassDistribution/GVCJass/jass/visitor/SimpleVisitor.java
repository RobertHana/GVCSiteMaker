package jass.visitor;

import java.util.Enumeration;
import java.util.Vector;
import jass.parser.*;

public class SimpleVisitor implements JassParserConstants, JassParserVisitor
{
    /**
     * This is the simplest visitor in the jass framework.
     * It supports methods for all language contructs but does nothing 
     * relevant. Other visitors can extend this class, to override methods.
     * The SimpleVisitor supports a method getNonSpecialTokens to access the 
     * tokens of the production unit (node) and getStringRepresentation 
     * to return a string made of the tokens of a node.
     */

    public Object visit(SimpleNode node, Object data){
	return data;
    }
    public Object visit(JassCompilationUnit node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassPackageDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassImportDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassTypeDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassClassDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassUnmodifiedClassDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassClassBody node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassNestedClassDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassClassBodyDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassMethodDeclarationLookahead node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassInterfaceDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassNestedInterfaceDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassUnmodifiedInterfaceDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassInterfaceMemberDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassFieldDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassVariableDeclarator node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassVariableDeclaratorId node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassVariableInitializer node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassArrayInitializer node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassMethodDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassMethodDeclarator node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassFormalParameters node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassFormalParameter node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassConstructorDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassExplicitConstructorInvocation node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassInitializer node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassType node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassPrimitiveType node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassResultType node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassName node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassNameList node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssignmentOperator node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassConditionalExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassConditionalOrExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassConditionalAndExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassInclusiveOrExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassExclusiveOrExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAndExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassEqualityExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassInstanceOfExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassRelationalExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassShiftExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAdditiveExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassMultiplicativeExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassUnaryExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassPreIncrementExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassPreDecrementExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassUnaryExpressionNotPlusMinus node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassPostfixExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassPostfixOperator node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassCastExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassPrimaryExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassPrimaryPrefix node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassPrimarySuffix node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassLiteral node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassBooleanLiteral node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassNullLiteral node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassArguments node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassArgumentList node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAllocationExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassArrayDimsAndInits node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassLabeledStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassBlock node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassBlockStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassLocalVariableDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassEmptyStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassStatementExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassSwitchStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassSwitchLabel node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassIfStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassWhileStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassDoStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassForStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassForInit node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassStatementExpressionList node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassForUpdate node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassBreakStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassContinueStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassReturnStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassThrowStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassSynchronizedStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassTryStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertStatement node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassMethodBodyBlock node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionClause node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassRequireClause node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassEnsureClause node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassBooleanAssertion node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassBooleanChangeAssertion node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassChangeList node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassFieldReference node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionLabel node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassClassInvariantClause node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassInvariantClause node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassInvariantAssertion node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassCheckClause node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassRescueClause node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassRescueCatch node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassVariantClause node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionForAllExistsExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionFiniteEnumerationCreation node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionRangeExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionConditionalExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionConditionalOrExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionConditionalAndExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionInclusiveOrExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionExclusiveOrExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionAndExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionEqualityExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionInstanceOfExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionRelationalExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionShiftExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionAdditiveExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionMultiplicativeExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionUnaryExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionUnaryExpressionNotPlusMinus node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassUnaryPlusMinusOperator node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassUnaryNotPlusMinusOperator node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionCastLookahead node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionCastExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionPrimaryExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionPrimaryPrefix node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionPrimarySuffix node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionArguments node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassAssertionArgumentList node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassRetryStatement node, Object data){return visit((SimpleNode) node, data);}
    
    public Object visit(JassTraceAssertion node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassTraceAssertionDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassTraceConstant node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassProcessDeclaration node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassProcessDeclarator node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassProcessExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassProcessIfElseExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassProcessPrefixExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassProcessChoiceExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassProcessParallelExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassProcessPrimaryExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassProcessCall node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassProcessJavaBlock node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassConditionalCommunication node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassBasicProcess node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassCommunicationExpressions node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassCommunicationExpression node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassCommunication node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassCommunicationWithWildcards node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassCommunicationParameterList node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassCommunicationParameter node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassLayedDownParameter node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassTypedParameter node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassInputParameter node, Object data){return visit((SimpleNode) node, data);}
    public Object visit(JassOutputParameter node, Object data){return visit((SimpleNode) node, data);}


    // --- This is additional functionality ---

	
    /**
     * Returns an Enumeration of tokens [cast: Token] that belong to this 
     * expansion unit.
     * @param node the node that represent the expansion unit
     * @return the requested tokens 
     * @see jass.parser.Token 
     */
    public Enumeration getNonSpecialTokens (Node node) {
	Vector v = new Vector();
	Token t = node.getFirstToken();
	v.addElement(t);
	while (t != node.getLastToken()) {
	    t = t.next;
	    v.addElement(t);
	}
	return v.elements();
    }

	
    /**
     * Returns a string that represents the sequence of tokens
     * belonging to this node. The string has no blanks, is is a
     * concatination of the token images.
     * @param node the node
     * @return the string 
     * @see jass.parser.Token 
     */
    public String getStringRepresentation (Node node)
    {
	StringBuffer stringBuffer = new StringBuffer();
	
	Token token = node.getFirstToken();
	stringBuffer.append(token.image);

	while (token != node.getLastToken()) {
			token = token.next;
			stringBuffer.append(token.image);
	}

	return stringBuffer.toString();
    }

    
    /**
     * Returns true, if the token.image belongs to the terminalId
     * @param 
     * @return the string 
     * @see jass.parser.Token 
     */
    public static boolean equal(Token token, int terminalId) {
	String terminal = "\"" + token.image + "\"";
	return JassParserConstants.tokenImage[terminalId].equals(terminal);
    }


    /* use : SimpleNode.childrenAccept
    public void visitChildren(JassParserVisitor visitor, Node node, 
			      Object data)
    {
	int numberOfChildren = node.jjtGetNumChildren();

	for(int iChild = 0; iChild < numberOfChildren; ++iChild) {
	    Node child = node.jjtGetChild(iChild);
	    child.jjtAccept(visitor, data);
	}
    }
    */
}
