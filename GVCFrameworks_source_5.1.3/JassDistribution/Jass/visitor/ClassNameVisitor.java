package jass.visitor;

import jass.parser.*;
import jass.GlobalFlags;

import java.util.*;


/**
 * The main class for code generation which implements the visitor
 * design pattern. For all nonterminals of the Java * language there
 * is a method, to produce the output of this contruct.
 */
public class ClassNameVisitor extends SimpleVisitor 
    implements JassParserVisitor,JassParserTreeConstants {
	

    protected Vector definedClassNames = new Vector();
    protected String currentPackagename;
    protected Stack currentClassname = new Stack();

    /**
     * Creates an ClassNameVisitor.
     */
    public ClassNameVisitor() {}

    protected Object collectClassname(Node node, Object o)
    {
	String classIdentifier = node.getFirstToken().next.image;
	
	String classname = classIdentifier;
	if (!currentClassname.empty()) {
	    classname = (String)currentClassname.peek() + "$" + classname;
	}
		
	currentClassname.push(classname);
	
	String fullClassname = currentPackagename == null ? classname
	    : currentPackagename + "." + classname;
		
	definedClassNames.addElement(fullClassname);
//System.out.println(fullClassname + " added by classname visitor");

	Object r = descend(node,o);
	currentClassname.pop();
	return r;
    }  


    public Object visit(JassCompilationUnit node, Object o) {
	return descend(node,o);
    }

    public Object visit(JassPackageDeclaration node, Object o) {
	// PackageDeclaration ::= "package" Name ";" 
	currentPackagename = (String) node.getFirstChild().jjtAccept(this, o);
	return currentPackagename;
    }

    public Object visit(JassName node, Object o) {
	// Name ::= <IDENTIFIER> ( "." <IDENTIFIER> )* 

	String name = "";

	int child = 0;
	Token token = node.getFirstToken();
	Token lastToken = node.getLastToken();
	
	while(true) {		
	    name += token.image;

	    if(token == lastToken) {
		break;
	    }
			
	    token = token.next;
	}

	return name;
    }
		
    public Object visit(JassUnmodifiedClassDeclaration node, Object o) {
	return collectClassname(node, o);
    }

    public Object visit(JassUnmodifiedInterfaceDeclaration node, Object o) {
	return collectClassname(node, o);
    }

    public Object visit(SimpleNode node, Object o) {
	return descend(node,o);
    }
	
	
    public Object descend(Node node, Object o) {
	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    node.jjtGetChild(i).jjtAccept(this,o);
	}
	return o;
    }
	
    public boolean containsClassName(String name) {
	for (int i = 0; i < definedClassNames.size(); i++) {
	    //System.out.println(">>>>"+(String)definedClassNames.elementAt(i));
	    if (((String)definedClassNames.elementAt(i)).equals(name)) {
		return true;
	    }
	}
	return false;
    }
  
}

/*end*/
