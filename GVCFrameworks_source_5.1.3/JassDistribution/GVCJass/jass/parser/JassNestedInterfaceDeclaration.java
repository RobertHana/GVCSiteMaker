/* Generated By:JJTree: Do not edit this line. JassNestedInterfaceDeclaration.java */

package jass.parser;

public class JassNestedInterfaceDeclaration extends SimpleNode {
  public JassNestedInterfaceDeclaration(int id) {
    super(id);
  }

  public JassNestedInterfaceDeclaration(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
