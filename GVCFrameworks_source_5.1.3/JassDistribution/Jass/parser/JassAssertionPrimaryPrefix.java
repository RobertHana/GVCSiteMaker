/* Generated By:JJTree: Do not edit this line. JassAssertionPrimaryPrefix.java */

package jass.parser;

public class JassAssertionPrimaryPrefix extends SimpleNode {
  public JassAssertionPrimaryPrefix(int id) {
    super(id);
  }

  public JassAssertionPrimaryPrefix(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
