/* Generated By:JJTree: Do not edit this line. JassProcessDeclaration.java */

package jass.parser;

public class JassProcessDeclaration extends SimpleNode {
  public JassProcessDeclaration(int id) {
    super(id);
  }

  public JassProcessDeclaration(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
