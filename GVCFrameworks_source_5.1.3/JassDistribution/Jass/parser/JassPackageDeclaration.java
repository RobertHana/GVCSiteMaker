/* Generated By:JJTree: Do not edit this line. JassPackageDeclaration.java */

package jass.parser;

public class JassPackageDeclaration extends SimpleNode {
  public JassPackageDeclaration(int id) {
    super(id);
  }

  public JassPackageDeclaration(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
