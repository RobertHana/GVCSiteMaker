/* Generated By:JJTree: Do not edit this line. JassVariableInitializer.java */

package jass.parser;

public class JassVariableInitializer extends SimpleNode {
  public JassVariableInitializer(int id) {
    super(id);
  }

  public JassVariableInitializer(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
