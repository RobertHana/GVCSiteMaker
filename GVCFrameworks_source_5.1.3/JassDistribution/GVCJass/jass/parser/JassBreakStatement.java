/* Generated By:JJTree: Do not edit this line. JassBreakStatement.java */

package jass.parser;

public class JassBreakStatement extends SimpleNode {
  public JassBreakStatement(int id) {
    super(id);
  }

  public JassBreakStatement(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
