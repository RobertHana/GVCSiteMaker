/* Generated By:JJTree: Do not edit this line. JassAssertionForAllExistsExpression.java */

package jass.parser;

public class JassAssertionForAllExistsExpression extends SimpleNode {
  public JassAssertionForAllExistsExpression(int id) {
    super(id);
  }

  public JassAssertionForAllExistsExpression(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
