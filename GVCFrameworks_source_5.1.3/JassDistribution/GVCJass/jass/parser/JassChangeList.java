/* Generated By:JJTree: Do not edit this line. JassChangeList.java */

package jass.parser;

public class JassChangeList extends SimpleNode {
  public JassChangeList(int id) {
    super(id);
  }

  public JassChangeList(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
