/* Generated By:JJTree: Do not edit this line. JassStatementExpression.java */

package jass.parser;

public class JassStatementExpression extends SimpleNode {
  public JassStatementExpression(int id) {
    super(id);
  }

  public JassStatementExpression(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
