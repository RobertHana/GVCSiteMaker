/* Generated By:JJTree: Do not edit this line. JassSynchronizedStatement.java */

package jass.parser;

public class JassSynchronizedStatement extends SimpleNode {
  public JassSynchronizedStatement(int id) {
    super(id);
  }

  public JassSynchronizedStatement(JassParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JassParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
