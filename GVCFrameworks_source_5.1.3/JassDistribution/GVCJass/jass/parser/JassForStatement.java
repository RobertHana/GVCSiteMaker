/* Generated By:JJTree: Do not edit this line. JassForStatement.java */

package jass.parser;

public class JassForStatement extends SimpleNode {
  public JassForStatement(int id) {
    super(id);
  }

  public JassForStatement(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
