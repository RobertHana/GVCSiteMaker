/* Generated By:JJTree: Do not edit this line. JassAssignmentOperator.java */

package jass.parser;

public class JassAssignmentOperator extends SimpleNode {
  public JassAssignmentOperator(int id) {
    super(id);
  }

  public JassAssignmentOperator(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
