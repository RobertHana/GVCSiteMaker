/* Generated By:JJTree: Do not edit this line. JassPostfixExpression.java */

package jass.parser;

public class JassPostfixExpression extends SimpleNode {
  public JassPostfixExpression(int id) {
    super(id);
  }

  public JassPostfixExpression(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
