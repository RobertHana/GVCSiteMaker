/* Generated By:JJTree: Do not edit this line. JassForInit.java */

package jass.parser;

public class JassForInit extends SimpleNode {
  public JassForInit(int id) {
    super(id);
  }

  public JassForInit(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
