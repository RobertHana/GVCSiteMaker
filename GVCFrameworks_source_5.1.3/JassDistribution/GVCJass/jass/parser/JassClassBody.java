/* Generated By:JJTree: Do not edit this line. JassClassBody.java */

package jass.parser;

public class JassClassBody extends SimpleNode {
  public JassClassBody(int id) {
    super(id);
  }

  public JassClassBody(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
