/* Generated By:JJTree: Do not edit this line. JassRescueClause.java */

package jass.parser;

public class JassRescueClause extends SimpleNode {
  public JassRescueClause(int id) {
    super(id);
  }

  public JassRescueClause(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
