/* Generated By:JJTree: Do not edit this line. JassAssertionLabel.java */

package jass.parser;

public class JassAssertionLabel extends SimpleNode {
  public JassAssertionLabel(int id) {
    super(id);
  }

  public JassAssertionLabel(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
