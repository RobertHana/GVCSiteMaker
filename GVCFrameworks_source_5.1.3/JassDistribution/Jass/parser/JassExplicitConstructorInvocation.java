/* Generated By:JJTree: Do not edit this line. JassExplicitConstructorInvocation.java */

package jass.parser;

public class JassExplicitConstructorInvocation extends SimpleNode {
  public JassExplicitConstructorInvocation(int id) {
    super(id);
  }

  public JassExplicitConstructorInvocation(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
