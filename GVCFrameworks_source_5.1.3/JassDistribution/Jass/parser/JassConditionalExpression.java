/* Generated By:JJTree: Do not edit this line. JassConditionalExpression.java */

package jass.parser;

public class JassConditionalExpression extends SimpleNode {
  public JassConditionalExpression(int id) {
    super(id);
  }

  public JassConditionalExpression(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
