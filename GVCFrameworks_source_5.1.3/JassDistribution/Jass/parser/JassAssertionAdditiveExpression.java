/* Generated By:JJTree: Do not edit this line. JassAssertionAdditiveExpression.java */

package jass.parser;

public class JassAssertionAdditiveExpression extends SimpleNode {
  public JassAssertionAdditiveExpression(int id) {
    super(id);
  }

  public JassAssertionAdditiveExpression(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
