/* Generated By:JJTree: Do not edit this line. JassExclusiveOrExpression.java */

package jass.parser;

public class JassExclusiveOrExpression extends SimpleNode {
  public JassExclusiveOrExpression(int id) {
    super(id);
  }

  public JassExclusiveOrExpression(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
