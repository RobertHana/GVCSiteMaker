/* Generated By:JJTree: Do not edit this line. JassAdditiveExpression.java */

package jass.parser;

public class JassAdditiveExpression extends SimpleNode {
  public JassAdditiveExpression(int id) {
    super(id);
  }

  public JassAdditiveExpression(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
