/* Generated By:JJTree: Do not edit this line. JassAssertionInclusiveOrExpression.java */

package jass.parser;

public class JassAssertionInclusiveOrExpression extends SimpleNode {
  public JassAssertionInclusiveOrExpression(int id) {
    super(id);
  }

  public JassAssertionInclusiveOrExpression(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
