/* Generated By:JJTree: Do not edit this line. JassLiteral.java */

package jass.parser;

public class JassLiteral extends SimpleNode {
  public JassLiteral(int id) {
    super(id);
  }

  public JassLiteral(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
