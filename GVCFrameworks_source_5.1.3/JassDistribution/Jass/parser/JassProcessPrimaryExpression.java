/* Generated By:JJTree: Do not edit this line. JassProcessPrimaryExpression.java */

package jass.parser;

public class JassProcessPrimaryExpression extends SimpleNode {
  public JassProcessPrimaryExpression(int id) {
    super(id);
  }

  public JassProcessPrimaryExpression(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
