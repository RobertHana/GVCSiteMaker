/* Generated By:JJTree: Do not edit this line. JassInclusiveOrExpression.java */

package jass.parser;

public class JassInclusiveOrExpression extends SimpleNode {
  public JassInclusiveOrExpression(int id) {
    super(id);
  }

  public JassInclusiveOrExpression(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
