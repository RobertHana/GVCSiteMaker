/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.visitor;

import java.io.*;
import jass.parser.*;
import jass.reflect.*;
import jass.reflect.Class;
import jass.reflect.traceAssertion.*;
import java.util.Stack;
import jass.util.*;
import jass.GlobalFlags;
import java.util.Hashtable;
import jass.runtime.traceAssertion.CommunicationManager;

/**
 * The main class for code generation, which implements the visitor design
 * pattern. For all non-terminals of the Java language there is a method
 * to produce the output of this construct.
 */
public class OutputVisitor extends SimpleVisitor implements JassParserVisitor, 
							    JassParserTreeConstants, jass.util.doc.Tags {


    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    private Class	     topLevelClass;
    private String	     packName;
    private static final int NONE = 0;
    private static final int FOR = 1;
    private static final int WHILE = 2;
    private static final int DO = 3;

    /**
     * The PrintWriter that generates the code.
     */
    protected PrintWriter    out;

    /**
     * A simple class that records informations about indents.
     */
    public class Indent {

	/**
	 * The indent
	 */
	public String  indent;

	/**
	 * Flag if indent is contained in a block
	 */
	public boolean block;

	/**
	 * Constructor declaration
	 * @param ind
	 * @param bl
	 */
	public Indent(String ind, boolean bl) {
	    indent = ind;
	    block = bl;
	}

    }

    /**
     * Class stores control informations about the code generation.
     * For each inner class a new VInfo class is created.
     */
    public class VInfo {

	/**
	 * Counts the contructors.
	 */
	public int       constructorCounter = 0;

	/**
	 * Counts the methods.
	 */
	public int       methodCounter = 0;

	/**
	 * Counts the loops.
	 */
	public int       loopCounter = 0;

	/**
	 * Line number of the current method
	 */
	public int       methodLine = 0;

	/**
	 * Counts the check statements.
	 */
	public int       checkCounter = 0;

	/**
	 * Counts the rescue clauses.
	 */
	public int       rescueCounter = 0;

	/**
	 * Counts the forall expressions.
	 */
	public int       forallCounter = 0;

	/**
	 * The class this VInfo belongs to.
	 */
	public Class     unit;

	/**
	 * Name of the current package.
	 */
	public String    packageName;

	/**
	 * Name of the current method.
	 */
	public String    methodName;

	/**
	 * The current method.
	 */
	public Method    m;

	/**
	 * The name of the unit. Not nessesary but simple.
	 */
	public String    unitName;

	/**
	 * A stack to store the actual indent information.
	 */
	public Stack     indent_stack = new Stack();

	/**
	 * A Set to record the methods that are used in assertions.
	 */
	public Set       internals = new Set();

	/**
	 * Stores the names of this class that must be generated in an
	 * internal version.  
	 */
	public Hashtable nodes_of_internals = new Hashtable();

	/**
	 * Indent for the current method.
	 */
	public String    method_indent = "";

	/**
	 * Indent for the current block.
	 */
	public String    block_indent = "";

	/**
	 * Indent for the current class.
	 */
	public String    class_indent = "";

	/**
	 * A flag to indicate if the check loop method must be generated.
	 */
	public boolean   jass_test = false;

	/**
	 * Indent that should be added to all other indents.
	 */
	public String    indent_shift = "";

	/**
	 * The id of the last loop that was created.
	 */
	public int       last_loop;
    }

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    /**
     * Creates an OutputVisitor.
     * @param o the PrintWriter writing the code
     * @param _unit the toplevel class, that should be written
     */
    public OutputVisitor(PrintWriter o, Class _unit) {
	out = o;
	topLevelClass = _unit;
	packName = _unit.getPackageName();
    }

    /*
     * ....................................................................
     * . Private Methods                                                  .
     * ....................................................................
     */

    /**
     * Prints a SimpleNode. This method prints all tokens connected 
     * directly to the given node but calls {@link <a 
     * href="jass.parser.SimpleNode#jjtAccept(JassParserVisitor,Object)"> 
     * jjtAccept</a>} for children of the actual node, jumping 
     * over their tokens in the main thread.
     *
     * @param node the SimpleNode to be printed
     * @param vi 
     *
     * @return null, nothing else 
     */
    private Object print(SimpleNode node, Object vi) {
	// If the node includes a loop, execution is stopped
	if (node.getLastToken().next == node.getFirstToken()) {
	    return null;
	} 

	Token t1 = node.getFirstToken();
	Token t = new Token();

	t.next = t1;

	SimpleNode n;
	// pass through all child nodes
	for (int ord = 0; ord < node.jjtGetNumChildren(); ord++) {
	    n = (SimpleNode) node.jjtGetChild(ord);
	    // get tokens from the token-'stream' till you get the
	    // first token belonging to the actual child-node
	    // while doing so print all tokens (with formal modified
	    // in case of method or class declarations)
 	    while (true) {
		t = t.next;

		if (t == n.getFirstToken()) {
		    break;
		} 

		if ((node.hasId(JJTMETHODDECLARATION) ||
		     node.hasId(JJTCONSTRUCTORDECLARATION) ||
		     node.hasId(JJTCLASSDECLARATION)) 
			&& t == node.getFirstToken()) {
		    printWithFormalModified(node, t, vi);
		} else {
		    print(t, vi);
		} 
	    } 
	    // then call the child node
	    n.jjtAccept(this, vi);
	    // set the token pointer to the last token of the actual 
	    // child node for the next round  
	    t = n.getLastToken();
	} 
	// print all tokens after the last child node until the
	// last token of this node is reached
	while (t != node.getLastToken()) {
	    t = t.next;

	    print(t, vi);
	} 

	return null;
    } 

    /**
     * Prints node without recursing to sub nodes.  Just spits out the
     * tokens.  Used to print out anonymous and method scoped classes.
     *
     * @param node The node to print
     * @param vi ignored.
     * @return null.
     */
    private Object printNoRecurse(SimpleNode node, Object vi) {
	Token t = node.getFirstToken();
	while (t != node.getLastToken().next) {
	    print (t,vi);
	    t = t.next;
	}
	return null;
    } 

    /**
     * Prints a token. First prints the special tokens of the actual
     * token, if there are any (assertion comments converted to normal
     * java comments ( /<b></b>* ... *<b></b>/ delimeters instead of 
     * /# ... #/)) then the token itself is printed.
     *
     * @param t the token to print
     * @param vi
     */
    private void print(Token t, Object vi) {
	Token tt = t.specialToken;

	if (tt != null) {
	    while (tt.specialToken != null) {
		tt = tt.specialToken;
	    }

	    while (tt != null) {

		// convert a assertion comment in normal comment !
		if (tt.kind == JassParserConstants.ASSERTION_COMMENT) {
		    out.print("/*" 
			      + tt.image.substring(2, tt.image.length() - 2) 
			      + "*/");
		} else {
		    out.print(convertNewline(tt.image, (VInfo) vi));
		}

		tt = tt.next;
	    } 
	} 

	out.print(t.image);
    } 

    /**
     * Prints a token and its special tokens modifying formal comments.
     * First all special tokens of a given token are printed, then the
     * token itself. To modify formal comments included among the special
     * tokens {@link #convertFormal(Node, String, VInfo) convertFormal} 
     * is used.
     *
     * @param node the actual node
     * @param t the actual token
     * @param vi
     */
    private void printWithFormalModified(Node node, Token t, Object vi) {
	Token tt = t.specialToken;

	if (tt != null) {
	    while (tt.specialToken != null) {
		tt = tt.specialToken;
	    } 

	    while (tt != null) {
		if (tt.kind == JassParserConstants.FORMAL_COMMENT) {
		    out.print(convertFormal(node, tt.image, (VInfo) vi));
		} else {
		    out.print(convertNewline(tt.image, (VInfo) vi));
		} 

		tt = tt.next;
	    } 
	} 

	out.print(t.image);
    } 

    /**
     * Prints all comments of a given token. Any other special tokens
     * belonging to it are ignored.
     *
     * @param t the actual token
     * @param vi
     */
    private void printComments(Token t, Object vi) {
	// a single line comments contain a new line. 
	// This must be supressed for correct code structuring !
	Token tt = t.specialToken;

	if (tt != null) {
	    while (tt.specialToken != null) {
		tt = tt.specialToken;
	    }

	    while (tt != null) {
		if (tt.kind == JassParserConstants.FORMAL_COMMENT 
			|| tt.kind == JassParserConstants.SINGLE_LINE_COMMENT 
			|| tt.kind 
			   == JassParserConstants.MULTI_LINE_COMMENT) {
		    out.print(convertNewline(tt.image, (VInfo) vi));
		} 

		tt = tt.next;
	    } 
	} 
    } 

    /**
     * Prints all special tokens of a given token.
     *
     * @param t The initial token
     * @param vi
     */
    private void printSpecials(Token t, Object vi) {
	Token tt = t.specialToken;

	if (tt != null) {
	    while (tt.specialToken != null) {
		tt = tt.specialToken;
	    }

	    while (tt != null) {
		out.print(convertNewline(tt.image, (VInfo) vi));

		tt = tt.next;
	    } 
	} 
    } 

    /**
     * Determines the indent String of a token. Therefor, the special tokens
     * of that token are merged to a single String and the substring
     * after the last newline character if any is returned or else the 
     * whole string.
     *
     * @param t a token
     * @return this tokens special tokens last line
     */
    private String getIndentString(Token t) {

	// System.out.print("indent:");
	if (t.specialToken == null) {
	    return "";
	} 

	StringBuffer sb = new StringBuffer();
	Token	     tmp_t = t.specialToken;

	while (tmp_t.specialToken != null) {
	    tmp_t = tmp_t.specialToken;
	}

	while (tmp_t != null) {
	    sb.append(tmp_t.image);

	    tmp_t = tmp_t.next;
	} 

	String s = sb.toString();

	if (s.lastIndexOf('\n') >= 0) {
	    s = s.substring(s.lastIndexOf('\n') + 1, s.length());
	} 

	return s;
    } 

    /**
     * Converts newlines in a string depending on the VInfo.
     * In fact, the only conversion made is to insert an 
     * indent (the VInfos indent_shift) to the beginning of
     * each new line (except the first).
     *
     * @param s a string to be converted
     * @param vi the conversion informations
     *
     * @return the converted string
     */
    private String convertNewline(String s, VInfo vi) {
	if (vi == null) {
	    return s;
	} 

	if (vi.indent_shift.length() == 0) {
	    return s;
	} 

	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < s.length(); i++) {
	    sb.append(s.charAt(i));

	    if (s.charAt(i) == '\n') {
		sb.append(vi.indent_shift);
	    } 
	} 

	return sb.toString();
    } 

    /** flag to docify a method precondition */
    private static final int DOCIFY_PRE  = 0;
    /** flag to docify a method postcondition */
    private static final int DOCIFY_POST = 1;
    /** flag to docify a class invariant */
    private static final int DOCIFY_INV  = 2;

    //TODO: Use common variable for keywords in Doclet and here
    private static final String[] DOCIFY_JAVADOC =
    { "@" + JASSDOC_PRE, "@" + JASSDOC_POST, "@" + JASSDOC_INV };
    private static final String[] DOCIFY_HTML =
    { "Requires:", "Ensures:", "Invariant:" };
    
    /**
     * This method converts formal declarations to javadoc comments.
     * If <tt>node</tt> is a method declaration or class declaration
     * any existing pre-, postcondition or invariant will be embeded
     * by HTML tags and inserted between the beginning of the javadoc 
     * statement and the first javadoc tag appearing or the end of
     * <tt>s</tt> if there aren't any.
     *
     * @param node the actual node
     * @param s the string to be converted
     * @param vi
     *
     * @return the converted string
     */
    private String convertFormal(Node node, String s, VInfo vi) {
	String nl = GlobalFlags.NEWLINE;

	StringBuffer sb = new StringBuffer();
	int	     start_pos = s.indexOf(vi.method_indent+" */"); 
	//o temporary!

	if (start_pos < 0) {
	    start_pos = s.indexOf("*/");

	    /** check startpos >= 0; **/
	} 

	sb.append(s.substring(0, start_pos));

	// added constructors
	if (node.hasId(JJTMETHODDECLARATION) 
	    || node.hasId(JJTCONSTRUCTORDECLARATION)) {
	    Precondition pre = vi.m.getPrecondition();

	    if (pre != null) {
		//TODO: get indention style and apply
		docify(DOCIFY_PRE, pre.toHTML(), sb, vi);
	    } 

	    Postcondition post = vi.m.getPostcondition();
	    if (post != null) {
		//TODO: get indention style and apply
		docify(DOCIFY_POST, post.toHTML(), sb, vi);
	    } 
	} 


	if (node.hasId(JJTCLASSDECLARATION)) {
	    Invariant inv = vi.unit.getInvariant();

	    if (inv != null) {
		//TODO: get indention style and apply
		docify(DOCIFY_INV, inv.toHTML(), sb, vi);
	    } 
	} 

	sb.append(s.substring(start_pos, s.length()));

	return sb.toString();
    }

    /**
     * convert an assertion to the JD_MODE specific code for the
     * JavaDoc comment (formal comment).
     */
    private final static void docify(int what, String assertion, 
				     StringBuffer target, VInfo vi) {
	if (GlobalFlags.JD_MODE == GlobalFlags.JD_NOTHING) {
	    return;
	}
	String nl = GlobalFlags.NEWLINE;
	if (GlobalFlags.JD_MODE == GlobalFlags.JD_JAVADOC) {
	    target.append(vi.method_indent + " * ")
		.append(DOCIFY_JAVADOC[what])
		//.append(nl)
		.append(" ")
		.append(assertion).append(nl);
	} else { //GlobalFlags.JD_MODE == GlobalFlags.JD_HTML
	    target.append("<dl><dt><b>")
		.append(DOCIFY_HTML[what])
		.append("</b></dt><dd><code>")
		.append(assertion)
		.append("</code></dd></dl>")
		.append(nl);
	}
    }

    /**
     * Prints a JassMethodDeclaration changing its name to be 
     * preceeded by "jassInternal_". Prints all internal tokens 
     * before the second child of the JassMethodDeclaration 
     * (child(1) - the methods name) using {@link #internalPrint(Token) 
     * internalPrint(Token)} then proceeds with all token before the third
     * child, then prints all remaining children.
     *
     * @param node a jass method declaration
     * @param vi 
     */
    private void internalPrint(JassMethodDeclaration node, Object vi) {

	// Modifier.setModifier(((VInfo)vi).m.getModifier()); ??????
	// Modifier.setProtected();
	// out.print(((VInfo)vi).class_indent+((VInfo)vi).method_indent);
	// out.print(Modifier.toString(Modifier.getModifier()));
	Token t = node.getFirstToken();
	Node  declarator = node.jjtGetChild(1); //this is the MethodDeclarator

	while (t != declarator.getFirstToken()) {
	    internalPrint(t);

	    t = t.next;
	} 

	// modify method name
	out.print(" jassInternal_");
	out.print(t.image);

	t = t.next;
	/* node should have 3 or 4 children:
	 *  ResultType() MethodDeclarator() [ "throws" NameList() ]
         *  MethodBodyBlock()
	 * without method body makes no sense for internal methods
	 */
	/** check node.jjtGetNumChildren() == 3
	          || node.jjtGetNumChildren() == 4 
	**/
	Node body = node.jjtGetChild(node.jjtGetNumChildren() - 1);

	while (t != body.getFirstToken()) {
	    internalPrint(t);

	    t = t.next;
	} 
	internalPrint(body, (VInfo) vi);
	internalPrint(node.getLastToken()); 
    } 

    /**
     * Prints an internal node. Looping nodes or jass statements 
     * (require, ensure, invariant, variant, check) are ignored. 
     * All tokens not belonging to subnodes are printed within this
     * method and children are printed recursively using 
     * {@link #internalPrint(Node,VInfo) internalPrint(Node,VInfo)} or
     * {@link #internalPrint(Token) internalPrint(Token)}.
     *
     * @param node the node to be printed
     * @param vi
     */
    private void internalPrint(Node node, Object vi) {
	if (node.getLastToken().next == node.getFirstToken()) {
	    return;
	} 

	if (node.hasId(JJTREQUIRECLAUSE) || node.hasId(JJTENSURECLAUSE) 
		|| node.hasId(JJTINVARIANTCLAUSE) 
		|| node.hasId(JJTVARIANTCLAUSE) 
		|| node.hasId(JJTCHECKCLAUSE)) {
	    return;
	} 

	Token t1 = node.getFirstToken();
	Token t = new Token();

	t.next = t1;

	SimpleNode n;

	for (int ord = 0; ord < node.jjtGetNumChildren(); ord++) {
	    n = (SimpleNode) node.jjtGetChild(ord);

	    while (true) {
		t = t.next;

		if (t == n.getFirstToken()) {
		    break;
		} 

		internalPrint(t);
	    } 

	    internalPrint(n, (VInfo) vi);

	    t = n.getLastToken();
	} 

	while (t != node.getLastToken()) {
	    t = t.next;

	    internalPrint(t);
	} 
    } 

    /**
     * Prints a given token and all its special tokens ignoring
     * formal, single line or multi line comments.
     *
     * @param t the actual token
     */
    private void internalPrint(Token t) {
	Token tt = t.specialToken;

	if (tt != null) {
	    while (tt.specialToken != null) {
		tt = tt.specialToken;
	    }

	    while (tt != null) {
		if (tt.kind != JassParserConstants.FORMAL_COMMENT 
			&& tt.kind != JassParserConstants.SINGLE_LINE_COMMENT 
			&& tt.kind 
			   != JassParserConstants.MULTI_LINE_COMMENT) {
		    out.print(tt.image);
		} 

		tt = tt.next;
	    } 
	} 
      
	out.print(t);
    } 

    /**
     * Prints an invariant by generating code and enclosing it within
     * a method. 
     *
     * @param inv the invariant to print
     * @param vinfo the VInfo for this method
     */
    private void generateJassCheckInvariant(Invariant inv, Object vinfo) {
	VInfo vi = (VInfo) vinfo;

	out.println();
	out.print(vi.class_indent + "\t");
	out.print("private void jassCheckInvariant(String msg) {");

	inv.generateCode(out, vi.class_indent + "\t\t");
	out.println();
	out.println(vi.class_indent + "\t}");
    } 

    /**
     * Prints a check loop method. Depending on the GlobalFlags.MODE
     * the generated method throws a jass.runtime.AssertionException
     * on CONTRACT mode or just prints out a warning string.
     *
     * @param vinfo the VInfo for this method.
     */
    private void generateJassCheckLoop(Object vinfo) {
	VInfo     vi = (VInfo) vinfo;
	Invariant inv = vi.unit.getInvariant();

	out.println();
	out.print(vi.class_indent + "\t");

	if (GlobalFlags.MODE == GlobalFlags.CONTRACT) {
	    out.println("private final void "
			+"jassCheckLoop(boolean b, "
			+"jass.runtime.AssertionException e) {");
	    out.print(vi.class_indent + "\t\tif(!b) throw e;");
	} else {
	    out.println("private final void "
			+"jassCheckLoop(boolean b, String s) {");
	    out.print(vi.class_indent + "\t\tif(!b) System.out.println(s);");
	} 

	out.println();
	out.println(vi.class_indent + "\t}");
    } 

    /**
     * Generates internal calls for the methods that are used in assertions.
     * The methods ids are gotten from the VInfos internal field
     * (a Set containig the methods used in assertions) and then used
     * the get the corresponding JassMethodDeclaration from the 
     * nodes_of_internals field. Each method declaration is printed using the
     * {@link #internalPrint(Node,VInfo) internalPrint} method.
     *
     * @param vinfo the VInfo holding the informations
     */
    private void generateInternalCalls(Object vinfo) {
	VInfo    vi = (VInfo) vinfo;
	Object[] ints = vi.internals.elements();

	// out.println();
	for (int i = 0; i < ints.length; i++) {
	    JassMethodDeclaration node = 
		(JassMethodDeclaration) vi.nodes_of_internals.get(((Method) ints[i]).getIdString());

	    if (node != null) {
		internalPrint(node, vi);
		out.println();
	    } 
	} 
    } 

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */

    /**
     * Generates a jassCheckForAll / jassCheckExists method.
     * Preceeds through <tt>unit</tt>s ForAllExpressions printing a new method
     * ('check for all' or 'check exists depending' on the expressions 
     * <tt>isForAll()</tt> flag. 
     * 
     * @param unit A Jass class
     * @param out the print writer to use
     * @param indent the actual indent
     */
    public static void generateJassCheckForAll_Exists(Class unit, 
	    PrintWriter out, String indent) {
	Expression[] es = unit.getForAll_Exists();
	// unit isn't the normal java.lang.Class, but jass.reflect.Class

	for (int i = 0; i < es.length; i++) {
	    if (es[i] instanceof ForAllExpression) {
		ForAllExpression fae = (ForAllExpression) es[i];
		// print some method name
		out.println();
		out.print(indent + "\t");
		out.print("protected final boolean jassCheck" 
			  + (fae.isForAll() ? "ForAll" : "Exists") + "_" 
			  + fae.getNr() + "_" 
			  + unit.getName().replace('.', '_') + "(");
		// print parameters
		Object[] los = fae.getLocals().elements();
		boolean  first = true;

		for (int j = 0; j < los.length; j++) {
		    if (!first) {
			out.print(",");
		    } else {
			first = false;
		    } 

		    out.print(ClassPool.arrayToTypeName(((Entity) los[j]).getType().getName()) 
			      + " " + ((Entity) los[j]).getName());
		} 
		// begin the expression
		// it's just simple text output from here on  ;)
		out.println(") {");
		// well, now decide what type of expression to build
		if (fae.getArgs() instanceof AllocationExpression) {
		    out.println(indent + "\t\tObject " + fae.getIdentifier() 
				+ ";");
		    out.println(indent + "\t\tint helpLeft;");
		    out.println(indent + "\t\t" 
				+ fae.getArgs().getType().getName() 
				+ " jassEnum = new " 
				+ fae.getArgs().getType().getName() 
				+ fae.getArgs().toString() + ";");
		    out.println(indent 
				+ "\t\tint elemsLeft = jassEnum.elementsLeft();");
		    out.print(indent 
			      + "\t\tif (jassEnum.elementsLeft() < 0)");
		    out.println(ErrorHandler.generateTrigger(GlobalFlags.FORALL_DEC, 
							     fae.getContainer(), 
							     null, 
							     fae.getLine(), 
							     "Enumeration in " 
							     + (fae.isForAll() ? "forall" : "exists") 
							     + " expression has signaled illegal progress!"));
		    out.println(indent 
				+ "\t\twhile(jassEnum.hasMoreElements()) {");
		    out.println(indent + "\t\t\t" + fae.getIdentifier() 
				+ " = jassEnum.nextElement();");

		    if (fae.isForAll()) {
			out.println(indent + "\t\t\tif (!(" 
				    + fae.getChild().toString() 
				    + ")) return false;");
		    } else {
			out.println(indent + "\t\t\tif (" 
				    + fae.getChild().toString() 
				    + ") return true;");
		    } 

		    out.println(indent 
				+ "\t\t\thelpLeft = jassEnum.elementsLeft();");
		    out.println(indent 
				+ "\t\t\tif(helpLeft >= elemsLeft || helpLeft < 0)");
		    out.println(ErrorHandler.generateTrigger(GlobalFlags.FORALL_DEC, 
							     fae.getContainer(), 
							     null, 
							     fae.getLine(), 
							     "Enumeration in " 
							     + (fae.isForAll() ? "forall" : "exists") 
							     + " expression has signaled illegal progress!"));
		    out.println(indent + "\t\t\telemsLeft = helpLeft;");
		    out.println(indent + "\t\t}");

		    if (fae.isForAll()) {
			out.println(indent + "\t\treturn true;");
		    } else {
			out.println(indent + "\t\treturn false;");
		    } 

		    out.println(indent + "\t}");
		} 

		if (fae.getArgs() instanceof RangeExpression) {
		    out.println(indent + "\t\ttry {");

		    RangeExpression args = (RangeExpression) fae.getArgs();

		    out.println(indent + "\t\t\tint " + fae.getIdentifier() 
				+ ";");
		    out.println(indent + "\t\t\tfor(" + fae.getIdentifier() 
				+ " = " + args.getBottom() + ";" 
				+ fae.getIdentifier() + " <= " 
				+ args.getTop() + ";" + fae.getIdentifier() 
				+ "++) {");

		    if (fae.isForAll()) {
			out.println(indent + "\t\t\t\tif (!(" 
				    + fae.getChild().toString() 
				    + ")) return false;");
		    } else {
			out.println(indent + "\t\t\t\tif (" 
				    + fae.getChild().toString() 
				    + ") return true;");
		    } 

		    out.println(indent + "\t\t\t}");

		    if (fae.isForAll()) {
			out.println(indent + "\t\t\treturn true;");
		    } else {
			out.println(indent + "\t\t\treturn false;");
		    } 

		    out.println(indent + "\t\t}");
		    out.println(indent 
				+ "\t\tcatch (ArrayIndexOutOfBoundsException jassException) {");
		    out.println(indent + "\t\t\t" 
				+ ErrorHandler.generateTrigger(GlobalFlags.FORALL_BOUNDS, 
							       fae.getContainer(), 
							       null, 
							       fae.getLine(), 
							       "Arraybounds in forall/exists expression violated ! (\"+jassException.toString()+\")"));
		    out.println(indent + "\t\t}");
		    out.println(indent + "\t}");
		} 
	    } // if instance of for all expression 
	} 
    } 

    /**
     * Visits a JassCompilationUnit and prints its content.
     * Flushes the stream at end of printing. The return
     * value is the Object got from the {@link 
     * #print(SimpleNode,Object) print()} method.
     *
     * @param node node to be visited
     * @param o never used
     *
     * @return the result of printing.
     */
    public Object visit(JassCompilationUnit node, Object o) {
	Object d = print(node, null);

	// flush stream at end of visiting process ...
	out.flush();

	return d;
    } 

    /**
     * Visits a class declaration. It first generates a VInfo based
     * on the class's first tokens indent string and the other
     * class related information (class/package name, internals)
     * and then prints the class declaration using the {@link 
     * #print(SimpleNode,Object) print()} method.
     *
     * @param node the class to be visited
     * @param o never used
     *
     * @return the Object returned by print()
     */
    public Object visit(JassClassDeclaration node, Object o) {

	// System.out.println("Output: " + node);
	VInfo vi = new VInfo();

	vi.class_indent = getIndentString(node.getFirstToken());

	// for formal comment conversion we must get the unit now ...
	Token t = node.getFirstToken();

	while (!t.image.equals("class")) {
	    t = t.next;
	}

	vi.unit = ClassPool.getClass((packName != null ? packName + "." : "") 
				     + t.next.image);
	vi.unitName = vi.unit.getName();
	vi.internals = vi.unit.callsInAssertions();

	return print(node, vi);
    } 

    /**
     * Just prints the node using the {@link 
     * #print(SimpleNode,Object) print()} method.
     *
     * @param node a node to print
     * @param vi the VInfo
     *
     * @return the Object returned by print()
     */
    public Object visit(JassUnmodifiedClassDeclaration node, Object vi) {
      /*XXX*/
	return print(node, vi);
    } 

    /**
     * Prints a nested class declaration. Does mostly the same
     * as {@link #visit(JassClassDeclaration,Object)
     * visit(JassClassDeclaration,Object)} except not 
     * ignoring the second parameter.
     *
     * @param node the nested class declaration
     * @param vi_outer the VInfo of the enclosing class
     *
     * @return the Object returned by print()
     */
    public Object visit(JassNestedClassDeclaration node, Object vi_outer) {
	VInfo vi = new VInfo();

	vi.class_indent = getIndentString(node.getFirstToken());

	Token t = node.getFirstToken();

	while (!t.image.equals("class")) {
	    t = t.next;
	}

	vi.unit = ClassPool.getClass(((VInfo) vi_outer).unit.getName() + "$" 
				     + t.next.image);
	vi.unitName = vi.unit.getName();
	vi.internals = vi.unit.callsInAssertions();

	return print(node, vi);
    } 

    /**
     * Visits a method declarator. Sets the VInfos 
     * <tt>methodName</tt> field to this methods
     * name (the first token of the node) and
     * prints the node.
     *
     * @param node a method declarator
     * @param vi the VInfo
     *
     * @return the Object returned by print()
     */
    public Object visit(JassMethodDeclarator node, Object vi) {

	// MethodDeclarator ::= <IDENTIFIER> FormalParameters ( "[" "]" )*
	((VInfo) vi).methodName = node.getFirstToken().image;

	return print(node, vi);
    } 

    /**
     * Visits a method declaration. This method first sets the
     * fields of the VInfo to match this method (gathering 
     * information about the indent and internals etc) and then
     * prints it.
     * 
     * @param node a method declaration
     * @param vinfo the VInfo
     *
     * @return the Object returned by print()
     */
    public Object visit(JassMethodDeclaration node, Object vinfo) {
	// MethodDeclaration ::=
	// (
	// "public" | "protected" | "private" | "static" | "abstract" | "final"
	// | "native" | "synchronized" | "strictfp"
	// )* ResultType MethodDeclarator ( "throws" NameList )? ( "{" MethodBodyBlock "}" | ";" )
	VInfo vi = (VInfo) vinfo;

	vi.loopCounter = 0;
	vi.checkCounter = 0;
	vi.rescueCounter = 0;

	vi.methodCounter++;
	vi.m = ClassPool.getMethod(vi.unitName, vi.methodCounter);
	vi.method_indent = getIndentString(node.getFirstToken());
	vi.methodLine = node.getFirstToken().beginLine;
	if (vi.internals.contains(vi.m)) {
	    vi.nodes_of_internals.put(vi.m.getIdString(), node);
	}
	return print(node, vi);
    } 

    /**
     * Visits a constructor declaration. First creates a new
     * VInfo setting its fields to proper values and then
     * prints the node. Its nearly the same as the
     * {@link #visit(JassMethodDeclaration,Object)
     * visit(JassMethodDeclaration,Object)} method.
     *
     * @param node the constructor declaration
     * @param vinfo the VInfo
     *
     * @return the Object returned by print()
     */
    public Object visit(JassConstructorDeclaration node, Object vinfo) {
	VInfo vi = (VInfo) vinfo;

	vi.loopCounter = 0;
	vi.checkCounter = 0;
	vi.rescueCounter = 0;
	vi.constructorCounter++;
	vi.m = ClassPool.getConstructor(vi.unitName, vi.constructorCounter);
	vi.method_indent = getIndentString(node.getFirstToken());
	vi.methodLine = node.getFirstToken().beginLine;

	if (vi.internals.contains(vi.m)) {
	    vi.nodes_of_internals.put(vi.m.getIdString(), node);
	} 

	return print(node, vi);
    } 

    /**
     * Visits a class body. First sets the VInfos <tt>traceAssertionField</tt>
     * (if <tt>GENERATE_TRACE</tt> global flag set) and creates the
     * corresponding java files. Then prints the first token of the 
     * node and sets the nodes children to accept the visitor.
     * Then this method creates an invariant expression (if necessary)
     * and prints it. After that the internal calls and several 
     * jass test methods are generated. <br>
     * If the <tt>GENERATE_TRACE</tt> global flag is set a modified
     * constructor (including a trace stack) is created and for
     * each inherited method a new version is generated. This is done 
     * by changing the signature to match the trace assertion
     * and then calling the super classes method modifying the return
     * value as necessary. <br>
     * At last the class declaration is closed by printing the last
     * token.
     *
     * @param node the class body
     * @param vinfo the VInfo
     *
     * @return null, always
     */
    public Object visit(JassClassBody node, Object vinfo) {
	VInfo	       vi = (VInfo) vinfo;
	if (GlobalFlags.GENERATE_TRACE) {
	    TraceAssertion traceAssertion = vi.unit.getTraceAssertion();

	    if (traceAssertion != null) {
		traceAssertion.createJavaFiles();
	    } 
	}

	print(node.getFirstToken(), vi);
	node.childrenAccept(this, vi);

	Invariant inv = vi.unit.getInvariant();

	// jass method generation for boolean assertions
	if ((inv != null && GlobalFlags.GENERATE_INV) || vi.jass_test 
	    || (vi.unit.getForAll_Exists().length > 0 && GlobalFlags.GENERATE_FORALL) 
	    ||!vi.internals.empty()) {
	    out.println();
	    out.println();
	    out.println(vi.class_indent 
			+ "\t/* --- The following methods of class " 
			+ vi.unit.getName() 
			+ " are generated by JASS --- */");
	} 

	if (!vi.internals.empty()) {
	    generateInternalCalls(vi);
	} 

	if (inv != null && GlobalFlags.GENERATE_INV) {
	    generateJassCheckInvariant(inv, vi);
	} 

	if (vi.jass_test) {
	    generateJassCheckLoop(vi);
	} 

	if (GlobalFlags.GENERATE_FORALL) {
	    generateJassCheckForAll_Exists(vi.unit, this.out, 
					   vi.class_indent);
	} 

	if (GlobalFlags.GENERATE_TRACE) {
	    // overwrite standard constructor - which is set implicitly - to realize trace stack
	    boolean hasImplicitConstructor = 
		(vi.unit.getDeclaredConstructors().length == 0);
	    
	    if (hasImplicitConstructor && GlobalFlags.GENERATE_TRACE) { // ask twice for trace flag?!?
		DefaultConstructor defaultConstructor = 
		    new DefaultConstructor(vi.unit.getIdentifier());
		
		defaultConstructor.setContainer(vi.unit);
		defaultConstructor.reflect();
		out.println(vi.class_indent + "public " 
			    + defaultConstructor.getName() + "() {");
		out.println(vi.method_indent + "  " 
			    + CommunicationManager.javaCodeCommunication(vi.unit, defaultConstructor, new String[]{}, true) 
			    + ";");
		out.println(vi.method_indent + "  " 
			    + CommunicationManager.javaCodeCommunication(vi.unit, defaultConstructor, new String[]{}, false) 
			    + ";");
		out.println(vi.class_indent + "}");
		out.println();
	    } 
	    
	    // overwrite all inherited methods to realize trace stack
	    Method[] inheritedMethods = vi.unit.getInheritedMethods();
	    
	    for (int iMethod = 0; iMethod < inheritedMethods.length; ++iMethod) {
		Method method = inheritedMethods[iMethod];
		
		if (!method.isStatic() &&!method.isFinal() 
		    &&!method.isNative()    // && !method.isAbstract()
		    ) {
		    
		    // a method declaration contains of the following parts
		    // <modifiers> <return type> <methodname> <parameters> <throw-list>
		    // modifiers
		    out.println();
		    out.println();
		    out.print(vi.class_indent + "\t" 
			      + Modifier.toJava(method.getModifier()));
		    
		    // return type
		    String returnType = 
			ClassPool.arrayToTypeName(method.getReturnType().getName());
		    
		    out.print(" " + returnType);
		    
		    // methodname
		    out.print(" " + method.getName());
		    out.print(" (");
		    
		    // parameters
		    FormalParameter[] parameters = method.getFormalParameters();
		    String		  returnValue = "returnValue";
		    String[]	  parameterStrings = 
			new String[parameters.length];
		    
		    for (int iParameter = 0; iParameter < parameters.length; 
			 ++iParameter) {
			FormalParameter parameter = parameters[iParameter];
			String	    parametername = "par" + iParameter;
			
			out.print(parameter.toJava(parametername));
			
			parameterStrings[iParameter] = parametername;
			
			if (iParameter + 1 < parameters.length) {
			    out.print(", ");
			} 
		    } 
		    
		    out.print(")");

		    // throw-list
		    String[] exceptions = method.getExceptionTypes();

		    if (exceptions.length > 0) {
			out.print(" throws ");
			
			for (int iException = 0; iException < exceptions.length; 
			     ++iException) {
			    String exception = exceptions[iException];
			    
			    out.print(exception);
			    
			    if (iException + 1 < exceptions.length) {
				out.print(", ");
			    } 
			} 
		    } 

		    out.println(" {");
		    out.println(vi.method_indent + "  " 
				+ CommunicationManager.javaCodeCommunication(vi.unit, method, parameterStrings, true) 
				+ ";");
		    out.print(vi.class_indent + "\t\t");

		    if (!method.isVoid()) {
			out.print(returnType + " " + returnValue + " = ");
		    } 

		    out.print("super." + method.getName() + "(");

		    for (int iParameter = 0; iParameter < parameters.length; 
			 ++iParameter) {
			String parametername = parameterStrings[iParameter];

			out.print(parametername);

			if (iParameter + 1 < parameters.length) {
			    out.print(", ");
			} 
		    } 

		    out.println(");");
		    out.println(vi.method_indent + "  " 
				+ CommunicationManager.javaCodeCommunication(vi.unit, 
									     method, 
									     method.isVoid() ? new String[]{} : new String[] {
										 returnValue
									     }, false) + ";");

		    if (!method.isVoid()) {
			out.println(vi.class_indent + "\t\t" + "return " 
				    + returnValue + ";");
		    } 

		    out.print(vi.class_indent + "\t}");
		} 
	    } 
	}
	print(node.getLastToken(), vi);

	return null;
    }

    /**
     * Visits the body of a method block. First the JassResult, trace assertion
     * stuff, beginning of rescue block and precondition (if any) are printed 
     * If the method has return statements inside of inner loop statements or a 
     * postcondition or an invariant or a rescue clause, the corresponding
     * code is inserted: Invariant and try statements before the main 
     * code, postcondition and rescue clauses after that. 
     * Else this node is just printed. <br>
     * At last the postcondition and if the method is void 
     * the trace assertion are inserted.
     *
     * @param node the method body
     * @param vinfo the usual VInfo
     *
     * @return null if inner return statements, rescue clauses, invariants 
     *         or postconditions were encountered, the return value of print 
     *         otherwise
     *
     */
    public Object visit(JassMethodBodyBlock node, Object vinfo) {
	VInfo   vi = (VInfo) vinfo;
	Object  returnValue;
	boolean isRefinement = 
	    vi.m.getIdString().equals("jassGetSuperState()");

	if (GlobalFlags.GENERATE_TRACE) {
	    out.println();
	    out.println(vi.method_indent + "  " 
			+ CommunicationManager.javaCodeCommunication(vi.unit, 
								     vi.m, vi.m.getFormalParameters(), true) 
			+ ";");
	}
	// generate jassResult ... if the method is non void
	// constructors have return type void !
	if (vi.m.getReturnType() != ClassPool.Void &&!isRefinement) {
	    out.print(vi.method_indent + "\t");
	    out.print(ClassPool.arrayToTypeName(vi.m.getReturnType().getName()) 
		      + " jassResult;");
	} 

	out.println();
	vi.indent_stack.push(new Indent(vi.method_indent, true));

	Invariant     inv = vi.unit.getInvariant();
	Postcondition post = vi.m.getPostcondition();

	if (post != null && GlobalFlags.GENERATE_POST) {
	    post.determineChangedFields();
	} 

	if ((vi.m.hasReturnInLoop()) 
	    || (inv != null && GlobalFlags.GENERATE_INV) 
	    || (post != null && GlobalFlags.GENERATE_POST) 
	    || vi.m.hasRescueClause()) {
	    if (vi.m.hasRetry()) {
		out.println();
		out.println(vi.method_indent + "\tboolean jassRetry = true;");
		out.println(vi.method_indent 
			    + "\tJassRetry: while(jassRetry) {");
		out.print(vi.method_indent + "\t\tjassRetry = false;");
		vi.indent_stack.push(new Indent(vi.method_indent + "\t", 
						true));

		vi.method_indent = vi.method_indent + "\t";
		vi.indent_shift = vi.indent_shift + "\t";
	    } 

	    if (vi.m.hasRescueClause()) {
		out.println();
		out.print(vi.method_indent + "\ttry {");
		vi.indent_stack.push(new Indent(vi.method_indent + "\t", 
						true));

		vi.method_indent = vi.method_indent + "\t";
		vi.indent_shift = vi.indent_shift + "\t";
	    } 

	    if ((inv != null && GlobalFlags.GENERATE_INV) && !isRefinement 
		&& !vi.m.isStatic()
		&& !(vi.m instanceof Constructor)) {

		// print inv
		out.println();
		out.println(vi.method_indent + "\t/* invariant */");
		out.print(vi.method_indent + "\t");
		out.print("jassCheckInvariant(\"at begin of method " 
			  + vi.m.getIdString() + ".\");");
	    } 

	    // generate jassOld ... (not when static ???) if postcondition uses jassOld
	    // refines ! ?????????
	    if ((post != null && GlobalFlags.GENERATE_POST) 
		&& (post.usesJassOld() || post.refinedUsesJassOld() 
		    || (!post.isChangeAll() &&!post.dropsChangeList()))) {
		out.println();
		out.print(vi.method_indent + "\t");
		out.print(vi.unitName + " jassOld = (" + vi.unitName 
			  + ")this.clone();");
	    } 


	    // print rest, not the RescueClause if present
	    for (int i = 0; 
		 i < node.jjtGetNumChildren() - (vi.m.hasRescueClause() ? 1 : 0); 
		 i++) {
		node.jjtGetChild(i).jjtAccept(this, vi);
	    } 

	    if ((post != null && GlobalFlags.GENERATE_POST) 
		&& vi.m.getReturnType() == ClassPool.Void) {

		// out.println();
		post.generateCode(out, vi.method_indent + "\t");
	    } 


	    if ((inv != null && GlobalFlags.GENERATE_INV) 
		&& !vi.m.isStatic()
		&& vi.m.getReturnType() == ClassPool.Void 
		&& !vi.m.getIdString().equals("jassGetSuperState()")) {

		// print inv
		out.println();
		out.println(vi.method_indent + "\t/* invariant */");
		out.print(vi.method_indent + "\t");
		out.print("jassCheckInvariant(\"at end of method " 
			  + vi.m.getIdString() + ".\");");
	    } 

	    if (vi.m.hasRescueClause()) {
		out.println();
		out.print(vi.method_indent + "}");
		vi.indent_stack.pop();

		vi.method_indent = vi.method_indent.substring(0, 
							      vi.method_indent.length() - 1);
		vi.indent_shift = 
		    vi.indent_shift.substring(0, 
					      vi.indent_shift.length() - 1);

		node.jjtGetChild(node.jjtGetNumChildren() 
				 - 1).jjtAccept(this, vi);
	    } 

	    if (vi.m.hasRetry()) {
		out.println();
		out.print(vi.method_indent + "}");

		if (!vi.m.getReturnType().isVoid()) {
		    out.println();
		    out.print(vi.method_indent 
			      + "/* This is an invalid termination of a rescue block! */");
		    out.println();
		    out.print("throw new jass.runtime.AssertionException(\"Invalid termination of a rescue block!\");");
		} 

		vi.indent_stack.pop();

		vi.method_indent = vi.method_indent.substring(0, 
							      vi.method_indent.length() - 1);
		vi.indent_shift = 
		    vi.indent_shift.substring(0, 
					      vi.indent_shift.length() - 1);
	    } 

	    vi.indent_stack.pop();

	    returnValue = null;
	
	} else {  // if(vi.m.hasReturnInLoop)
	    Object o = print(node, vi);

	    vi.indent_stack.pop();

	    returnValue = o;
	} 

	// If the method has no return statements the end of method is
	// marked here.
	if (vi.m.getReturnType().isVoid() && GlobalFlags.GENERATE_TRACE) {
	    out.println();
	    out.println(vi.method_indent + "  " 
			+ CommunicationManager.javaCodeCommunication(vi.unit, 
								     vi.m, new String[]{}, false) 
			+ ";");
	} 

	return returnValue;
    } 

    /**
     * Visits a require clause and prints a precondition.
     *
     * @param node never used
     * @param vi the VInfo
     *
     * @return null, any times
     */
    public Object visit(JassRequireClause node, Object vi) {
	Precondition precond = ((VInfo) vi).m.getPrecondition();

	if (GlobalFlags.GENERATE_PRE) {
	    precond.generateCode(out, ((VInfo) vi).method_indent + "\t");
	} 

	return null;
    } 

    /**
     * Visits an check clause statement. If contained in a block, the check code 
     * is simply generated, else an enclosing block is generated too. 
     *
     * @param node the current node 
     * @param vinfo the VInfo
     *
     * @return null, always
     */
    public Object visit(JassCheckClause node, Object vinfo) {
	VInfo  vi = (VInfo) vinfo;
	Check  check = vi.m.getCheck(vi.checkCounter++);
	Indent ind = (Indent) vi.indent_stack.peek();

	if (ind.block || check.getAssertionExpressions().length <= 1) {
	    if (GlobalFlags.GENERATE_CHECK) {
		check.generateCode(out, ind.indent + "\t");
	    } 
	} else {
	    out.print(" {");
	    out.print(ind.indent + "\t");

	    if (GlobalFlags.GENERATE_CHECK) {
		check.generateCode(out, ind.indent + "\t");
	    } 

	    out.println();
	    out.print(ind.indent + "}");
	} 

	return null;
    } 

    /**
     * Visists a return statement. If a JassResult has to be created or there
     * are any postconditions or invariants, the corresponding code is printed before the 
     * return statement. If necessary, the printed code is surrounded by a bock.
     *
     * @param node the actual node
     * @param vinfo the usual VInfo
     *
     * @return null, every times
     */
    public Object visit(JassReturnStatement node, Object vinfo) {
	// ReturnStatement ::= "return" ( Expression )? ";"
	VInfo	      vi = (VInfo) vinfo;
	String	      indent = ((Indent) vi.indent_stack.peek()).indent;
	
	Invariant     inv = ClassPool.getClass(vi.unitName).getInvariant();
	Postcondition post = vi.m.getPostcondition();

	boolean       last_was_block = ((Indent) vi.indent_stack.peek()).block;
	boolean       hasExpression = (node.jjtGetNumChildren() > 0);
	boolean       isRefinement = vi.m.getIdString().equals("jassGetSuperState()");
	boolean       createJassResult = hasExpression &&!isRefinement;

	if (createJassResult) {

	    // are we in a block ?
	    if (!last_was_block) {
		out.print(" {");
	    } 

	    out.println();
	    out.print(indent + "\t");
	    out.print("jassResult = (");
	    print((SimpleNode) node.jjtGetChild(0), vi);
	    out.print(");");
	} 

	if (vi.last_loop != NONE 
		&& vi.m.getLoopInvariant(vi.loopCounter) != null 
		&& GlobalFlags.GENERATE_LOOP) {
	    LoopInvariant loop = vi.m.getLoopInvariant(vi.loopCounter);

	    loop.generateCodeWhileDo(out, indent + "\t");
	} 

	if (post != null && GlobalFlags.GENERATE_POST) {
	    post.generateCode(out, indent + "\t");
	} 

	if ((inv != null && GlobalFlags.GENERATE_INV) &&!isRefinement
	    && !vi.m.isStatic()) {

	    // print inv
	    out.println();
	    out.println(indent + "\t/* invariant */");
	    out.print(indent + "\t");
	    out.print("jassCheckInvariant(\"before return in method " 
		      + vi.m.getIdString() + ".\");");
	} 

	if (!createJassResult) {
	    return print(node, vi);
	} else {
	  if (GlobalFlags.GENERATE_TRACE) {
	    out.println();
	    out.println(vi.method_indent + "  " 
			+ CommunicationManager.javaCodeCommunication(vi.unit, 
			vi.m, new String[] {
		"jassResult"
	    }, false) + ";");
	    out.println();
	  }

	    // Special tokens (comments) before return must be printed.
	    // If return was a child in a block, there is alread a new line in the special tokens !
	    // do only print the comments !
	    printComments(node.getFirstToken(), vi);
	    out.println();
	    out.print(indent + "\t");
	    out.print("return jassResult;");

	    if (!last_was_block) {
		out.println();
		out.print(indent + "}");
	    } 
	} 

	return null;
    } 

    /**
     * Visits a for statement. If no loop variants or invariants are given or
     * the <tt>GENERATE_LOOP</tt> global flag is not set, the loop is 
     * printed as it is. <br>
     * Else information about expressions, local declarations etc is gathered
     * and the loop is printed by writing local declarations first (if any and
     * if variants exist), then the loop declaration (for ...) and then the
     * expression and checks for invariants and variants.
     *
     * @param node the for statement
     * @param vinfo the VInfo
     *
     * @return null, nothing else
     */
    public Object visit(JassForStatement node, Object vinfo) {
	VInfo vi = (VInfo) vinfo;

	vi.loopCounter++;

	// The id string for the exceptions
	String	      es = vi.unit.getName() + "." + vi.m.getIdString();
	Indent	      ind = (Indent) vi.indent_stack.peek();
	LoopVariant   vari = vi.m.getLoopVariant(vi.loopCounter);
	LoopInvariant loop = vi.m.getLoopInvariant(vi.loopCounter);

	if ((vari == null && loop == null) ||!GlobalFlags.GENERATE_LOOP) {
	    return print(node, vi);
	} else {
	    vi.jass_test = true;

	    // we retrieve some informations about the loop ...
	    boolean hasInit = false;
	    boolean hasUpdate = false;
	    boolean hasLocalDecl = false;
	    boolean hasExpression = false;
	    int     child = 0;

	    if (node.jjtGetChild(child).hasId(JJTFORINIT)) {
		hasInit = true;
		child++;
	    } 

	    if (child < node.jjtGetNumChildren() 
		&& node.jjtGetChild(child).hasId(JJTEXPRESSION)) {
		hasExpression = true;
		child++;
	    } 

	    if (child < node.jjtGetNumChildren() 
		&& node.jjtGetChild(child).hasId(JJTFORUPDATE)) {
		hasUpdate = true;
		child++;
	    } 

	    if (hasInit 
		&& node.jjtGetChild(0).jjtGetChild(0).hasId(JJTLOCALVARIABLEDECLARATION)) {
		hasLocalDecl = true;

		// must we create a new local block ?
	    } 

	    if (hasLocalDecl || vari != null) {
		if (ind.block) {
		    out.println();
		} 

		out.println((ind.block ? (ind.indent + "\t{") : (" {")));

		/* new */
		vi.indent_shift = vi.indent_shift + "\t";
		ind = new Indent(ind.indent + /* new */ "\t", true);

		vi.indent_stack.push(ind);

		if (vari != null) {
		    out.println(ind.indent + "\tint jassVariant" 
				+ (vi.loopCounter > 1 ? new Integer(vi.loopCounter).toString() : "") 
				+ ";");
		} 

		if (hasLocalDecl) {
		    out.print(ind.indent + "\t");
		    print((SimpleNode) node.jjtGetChild(0).jjtGetChild(0), 
			  vi);
		    out.println(";");
		} 
	    } 

	    // print the first tokens ("for","(") ...
	    out.print(ind.indent);

	    // a block ? make a greater indent ...
	    if (hasLocalDecl || vari != null) {
		out.print("\t");
		printComments(node.getFirstToken(), vi);
		out.print("for");
	    } else {
		print(node.getFirstToken(), vi);
	    }

	    print(node.getFirstToken().next, vi);

	    if (hasInit &&!hasLocalDecl) {
		print((SimpleNode) node.jjtGetChild(0), vi);

		// first test loop invariant
	    } 

	    if (loop != null) {
		loop.generateCodeFor(out, ind.indent + "\t", 
				     hasLocalDecl ||!hasInit);
	    } 

	    // then init the variant
	    if (vari != null) {
		if (loop != null || (hasInit &&!hasLocalDecl)) {
		    out.print(",");
		} 

		out.print("jassVariant" 
			  + (vi.loopCounter > 1 ? new Integer(vi.loopCounter).toString() : "") 
			  + "=" 
			  + vari.getAssertionExpressions()[0].getExpression());

		// check it for greater zero
		vari.generateCodeForZero(out, "", vi.loopCounter);
		out.println();
		out.print(ind.indent + "\t");
	    } 

	    // print the ";"
	    // this is tricky, cause we must print the specials tokens (e.g. comments) too !
	    // child contains the number of the actual child
	    child = 0;

	    if (hasInit) {
		print(node.jjtGetChild(child).getLastToken().next, vi);

		child++;

		if (hasExpression) {
		    print((SimpleNode) node.jjtGetChild(child), vi);

		    // print ";"
		    print(node.jjtGetChild(child).getLastToken().next, vi);

		    child++;
		} else {
		    print(node.jjtGetChild(child).getLastToken().next.next, 
			  vi);
		} 
	    } else {
		print(node.getFirstToken().next.next, vi);

		if (hasExpression) {
		    print((SimpleNode) node.jjtGetChild(child), vi);
		    print(node.jjtGetChild(child).getLastToken().next, vi);

		    child++;
		} else {
		    print(node.getFirstToken().next.next.next, vi);
		} 
	    } 

	    // make a new line, its too long ...
	    out.println();
	    out.print(ind.indent + "\t");

	    // process the update
	    if (hasUpdate) {
		print((SimpleNode) node.jjtGetChild(child), vi);

		child++;
	    } 

	    if (loop != null) {
		loop.generateCodeFor(out, ind.indent + "\t", !hasUpdate);
	    } 

	    if (vari != null) {
		if (loop != null 
		    || (hasUpdate /* hasInit && !hasLocalDecl */)) {
		    out.print(",");
		} 

		vari.generateCodeForDec(out, "", vi.loopCounter);
		out.print(",jassVariant" 
			  + (vi.loopCounter > 1 ? new Integer(vi.loopCounter).toString() : "") 
			  + "=" 
			  + vari.getAssertionExpressions()[0].getExpression());
		out.println();
		out.print(ind.indent + "\t");

		// check it for greater zero
		vari.generateCodeForZero(out, "", vi.loopCounter);
		out.println();
		out.print(ind.indent + "\t");
	    } 

	    // print the ")" ...
	    if (hasUpdate) {
		print(node.jjtGetChild(node.jjtGetNumChildren() 
				       - 2).getLastToken().next, vi);
	    } else {
		if (hasExpression) {
		    print(node.jjtGetChild(node.jjtGetNumChildren() 
					   - 2).getLastToken().next.next, vi);
		} else {
		    if (hasInit) {
			print(node.jjtGetChild(node.jjtGetNumChildren() 
					       - 2).getLastToken().next.next.next, vi);
		    } else {
			print(node.getFirstToken().next.next.next.next, vi);
		    } 
		} 
	    } 

	    vi.last_loop = FOR;

	    node.jjtGetChild(node.jjtGetNumChildren() - 1).jjtAccept(this, 
								     vi);

	    vi.last_loop = NONE;

	    if (hasLocalDecl || vari != null) {
		vi.indent_stack.pop();
		out.println();

		// out.print(ind.indent+(((Indent)vi.indent_stack.peek()).block?"\t":""));
		out.print(ind.indent + "}");

		/* new */
		vi.indent_shift = 
		    vi.indent_shift.substring(0, 
					      vi.indent_shift.length() - 1);
	    } 
	} 

	return null;
    } 

    /**
     * Visits a while loop. If there are any variants and the 
     * <tt>GENERATE_LOOP</tt> global flag is set a variant check 
     * is generated before printing. Then this node is written.
     *
     * @param node the while statement
     * @param vinfo the VInfo
     *
     * @return the Object returned by print()
     */
    public Object visit(JassWhileStatement node, Object vinfo) {
	VInfo vi = (VInfo) vinfo;

	vi.loopCounter++;

	Indent      ind = (Indent) vi.indent_stack.peek();
	String      indent = getIndentString(node.getFirstToken());
	LoopVariant vari = vi.m.getLoopVariant(vi.loopCounter);

	if (vari != null && GlobalFlags.GENERATE_LOOP) {

	    // is while contained in a block ?
	    if (!ind.block) {
		out.print(" {");
		vi.indent_stack.push(new Indent(ind.indent + "\t", true));
	    } 

	    out.println();
	    out.print(ind.indent + "\tint jassVariant" 
		      + (vi.loopCounter > 1 ? new Integer(vi.loopCounter).toString() : "") 
		      + ";");
	} 

	vi.indent_stack.push(new Indent(indent, false));

	Object o;

	// the actual printing
	vi.last_loop = WHILE;
	o = print(node, vi);
	vi.last_loop = NONE;

	vi.indent_stack.pop();

	if (!ind.block) {
	    out.println();
	    out.print(ind.indent + "}");
	    vi.indent_stack.pop();
	} 

	return o;
    } 

    /**
     * Prints a do loop. If the loop has a variant and the
     * <tt>GENERATE_LOOP</tt> global flag is set a variant
     * check is generated before the loop. Then the loop is
     * printed. Last if there is an invariant this code is
     * generated too.
     *
     * @param node the do statement
     * @param vinfo the VInfo
     *
     * @return null, always
     */
    public Object visit(JassDoStatement node, Object vinfo) {
	VInfo vi = (VInfo) vinfo;

	vi.loopCounter++;

	Indent	      ind = (Indent) vi.indent_stack.peek();
	String	      indent = getIndentString(node.getFirstToken());
	LoopVariant   vari = vi.m.getLoopVariant(vi.loopCounter);
	LoopInvariant loop = vi.m.getLoopInvariant(vi.loopCounter);

	if (vari != null && GlobalFlags.GENERATE_LOOP) {

	    // is do contained in a block ?
	    if (!ind.block) {
		out.print(" {");
		vi.indent_stack.push(new Indent(ind.indent + "\t", true));
	    } 

	    out.println();
	    out.print(ind.indent + "\tint jassVariant" 
		      + (vi.loopCounter > 1 ? new Integer(vi.loopCounter).toString() : "") 
		      + ";");
	} 

	vi.indent_stack.push(new Indent(indent, false));

	Object o;

	vi.last_loop = DO;
	o = print(node, vi);
	vi.last_loop = NONE;

	if (loop != null && GlobalFlags.GENERATE_LOOP) {
	    loop.generateCodeWhileDo(out, indent);
	} 

	vi.indent_stack.pop();

	if (!ind.block) {
	    out.println();
	    out.print(ind.indent + "}");
	    vi.indent_stack.pop();
	} 

	return null;
    } 

    /**
     * Visits an if statement. This statement is simply printed (with increasing the indent
     * if necessary).
     *
     * @param node the if statement
     * @param vi the usual VInfo
     *
     * @return the Object returned by print
     */
    public Object visit(JassIfStatement node, Object vi) {
	((VInfo) vi).indent_stack.push(new Indent(getIndentString(node.getFirstToken()), 
						  false));

	Object o = print(node, vi);

	((VInfo) vi).indent_stack.pop();

	return o;
    } 

    /**
     * Visits a block and prints it increasing the indent.  
     *
     * @param node the actual block
     * @param vi the VInfo
     *
     * @return null, always
     */
    public Object visit(JassBlock node, Object vi) {
	Indent ind = (Indent) ((VInfo) vi).indent_stack.peek();

	((VInfo) vi).indent_stack.push(new Indent(ind.indent + "\t", true));

	Object o = print(node, /* (Indent)indent_stack.peek() */ vi);

	((VInfo) vi).indent_stack.pop();

	return null;
    } 

    /**
     * Visits a switch label and prints it increasing the indent.
     *
     * @param node the switch label
     * @param vi the VInfo
     *
     * @return null, every time
     */
    public Object visit(JassSwitchLabel node, Object vi) {
	Indent ind = (Indent) ((VInfo) vi).indent_stack.peek();

	// ???? double indent if "case" or "default" ? Sometimes this looks better.
	((VInfo) vi).indent_stack.push(new Indent(ind.indent + "\t", true));

	Object o = print(node, vi);

	((VInfo) vi).indent_stack.pop();

	return null;
    } 

    /**
     * Visits a continue statement. If in a while or do loop a monotic check
     * for the loop variant is created before printing (in a new block). 
     * Else this node is simply written down. 
     *
     * @param node the continue statement
     * @param vinfo the VInfo
     *
     * @return null and nothing else
     */
    public Object visit(JassContinueStatement node, Object vinfo) {
	VInfo vi = (VInfo) vinfo;

	// if a continue appears in a while- or do-loop with a variant
	// there must be a monotonic test ...
	if ((vi.last_loop == WHILE || vi.last_loop == DO) 
		&& GlobalFlags.GENERATE_LOOP) {
	    LoopVariant vari = vi.m.getLoopVariant(vi.loopCounter);

	    if (vari != null) {
		String  indent = ((Indent) vi.indent_stack.peek()).indent;
		boolean was_block = ((Indent) vi.indent_stack.peek()).block;

		if (!was_block) {
		    vi.indent_stack.push(new Indent(indent + "\t", true));

		    indent = indent + "\t";

		    out.print(" {");
		} 

		out.println();
		vari.generateCodeWhileDoDec(out, indent, vi.loopCounter);
		print(node, vi);

		if (!was_block) {
		    out.println();
		    vi.indent_stack.pop();

		    indent = ((Indent) vi.indent_stack.peek()).indent;

		    out.print(indent + "}");
		} 
	    } 
	} else {
	    print(node, vinfo);
	}

	return null;
    } 

    /**
     * Visits a Jass retry statement. First prints its special tokens
     * and then "jassRetry = true; continue JassRetry;".
     *
     * @param node the retry statement
     * @param vi the VInfo
     *
     * @return null
     */
    public Object visit(JassRetryStatement node, Object vi) {
	printSpecials(node.getFirstToken(), vi);
	out.print("jassRetry = true; continue JassRetry;");

	return null;
    } 

    /**
     * Visits a Jass assertion clause. In case of a for loop 
     * all code is genrated in the loop head. No changes 
     * are needed in the body. <br>
     * If this is a while or do loop the loop variant and invariant
     * (if any) are created. Then the children are printed. 
     * The loop is enclosed by block if it hasn't been before.
     * In case of while or do loop with any invariant or variant,
     * null is returned by this method, else the value returned
     * by {@link jass.parser.SimpleNode#childrenAccept 
     * SimpleNode.childrenAccept}.
     *
     * @param node this assertion clause
     * @param vinfo the VInfo
     *
     * @return the value returned SimpleNode.childrenAccept or null
     */
    public Object visit(JassAssertionClause node, Object vinfo) {
	VInfo vi = (VInfo) vinfo;

	// For a for loop all code is genrated in the loop head. No changes are needed in the body ...
	if (vi.last_loop == FOR) {
	    return node.childrenAccept(this, vi);
	} else {

	    // this is a while or do loop ...
	    // insert loop invariant and variant ...
	    // get the loop body statement (the last child) (is a normal statement or a block)
	    LoopInvariant loop = vi.m.getLoopInvariant(vi.loopCounter);
	    LoopVariant   vari = vi.m.getLoopVariant(vi.loopCounter);
	    String	  es = vi.unit.getName() + "." + vi.m.getIdString();

	    // something to do ?
	    if ((vari != null || loop != null) && GlobalFlags.GENERATE_LOOP) {
		String indent = ((Indent) vi.indent_stack.peek()).indent;

		// last child is a statement, get the child of this (the real body)
		Node   body = node.jjtGetChild(node.jjtGetNumChildren() 
					       - 1).jjtGetChild(0);

		// is body a block ?
		if (((SimpleNode) body).hasId(JassParserTreeConstants.JJTBLOCK)) {

		    // insert a block on the indent_stack !
		    vi.indent_stack.push(new Indent(indent + "\t", true));

		    indent = indent + "\t";

		    print(body.getFirstToken(), vi);

		    // print invariant and variant (if present)
		    if (loop != null) {
			loop.generateCodeWhileDo(out, indent);
		    } 

		    if (vari != null) {
			out.println();
			out.println(indent + "jassVariant" 
				    + (vi.loopCounter > 1 ? new Integer(vi.loopCounter).toString() : "") 
				    + "=" 
				    + vari.getAssertionExpressions()[0].getExpression() 
				    + ";");
			vari.generateCodeWhileDoZero(out, indent, 
						     vi.loopCounter);
		    } 

		    // print the children of the block ...
		    // This prevents the duplicate "{" !
		    for (int i = 0; i < body.jjtGetNumChildren(); i++) {
			int help_last_loop = vi.last_loop;

			body.jjtGetChild(i).jjtAccept(this, vi);

			vi.last_loop = help_last_loop;
		    } 

		    if (vari != null) {

			// check decrement
			out.println();
			vari.generateCodeWhileDoDec(out, indent, 
						    vi.loopCounter);
		    } 

		    print(body.getLastToken(), vi);
		    vi.indent_stack.pop();

		    indent = ((Indent) vi.indent_stack.peek()).indent;
		} else {

		    // make a block
		    vi.indent_stack.push(new Indent(indent + "\t", true));

		    indent = indent + "\t";

		    out.print(" {");

		    if (loop != null) {
			loop.generateCodeWhileDo(out, indent);
		    } 

		    if (vari != null) {
			out.println();
			out.println(indent + "jassVariant" 
				    + (vi.loopCounter > 1 ? new Integer(vi.loopCounter).toString() : "") 
				    + "=" 
				    + vari.getAssertionExpressions()[0].getExpression() 
				    + ";");
			vari.generateCodeWhileDoZero(out, indent, 
						     vi.loopCounter);
		    } 

		    // accept the statement (not the body -> statement expression would be printed without ";" !)
		    int help_last_loop = vi.last_loop;

		    node.jjtGetChild(node.jjtGetNumChildren() 
				     - 1).jjtAccept(this, vi);

		    vi.last_loop = help_last_loop;

		    if (vari != null) {

			// check decrement
			out.println();
			vari.generateCodeWhileDoDec(out, indent, 
						    vi.loopCounter);
		    } 

		    out.println();
		    vi.indent_stack.pop();

		    // get back old indent
		    indent = ((Indent) vi.indent_stack.peek()).indent;

		    out.print(indent + "}");
		} 

		// loop invariant after loop body
		if (vi.last_loop == WHILE && loop != null) {
		    loop.generateCodeWhileDo(out, indent);
		} 

		return null;
	    } else {
		return node.childrenAccept(this, vi);
	    }
	} 
    } 

    /* Most nonterminals are written without modifications ... */

    /**
     * Visits any simple node and prints it.
     *
     * @param node the node
     * @param vi the VInfo
     *
     * @return the value returned by print
     */
    public Object visit(SimpleNode node, Object vi) {
	return print(node, vi);
    } 

    /**
     * Visits a Jass interface declaration. First creates a new
     * VInfo using this nodes first tokens indent string, then
     * searchest for the first token matching "interface", setting
     * the VInfos <tt>unit</tt>, <tt>unitName</tt> and
     * <tt>internals</tt> fields by using the next tokens (following 
     * the "interface") content.
     *
     * @param node the interface declaration
     * @param vi the VInfo (never used)
     *
     * @return the value returned by print
     */
    public Object visit(JassInterfaceDeclaration node, Object vi_outer) {
	// System.out.println("Output: " + node);
	VInfo vi = new VInfo();

	vi.class_indent = getIndentString(node.getFirstToken());

	// Now skip over the modifiers to get the name of the interface
	Token t = node.getFirstToken();
	while (!t.image.equals("interface")) {
	    t = t.next;
	}

	vi.unit = ClassPool.getClass((packName != null ? packName + "." : "") 
				     + t.next.image);
	vi.unitName = vi.unit.getName();
	vi.internals = vi.unit.callsInAssertions();

	return print(node, vi);
    } 

    /**
     * Visits a nested interface declaration. First creates a new
     * VInfo using this nodes first tokens indent string, then
     * searchest for the first token matching "interface", setting
     * the VInfos <tt>unit</tt>, <tt>unitName</tt> and
     * <tt>internals</tt> fields by using the next tokens (following 
     * the "interface") content.
     *
     * @param node the nested interface
     * @param vi the outer VInfo 
     *
     * @return the value returned by print
     */
    public Object visit(JassNestedInterfaceDeclaration node, Object vi_outer) {
	VInfo vi = new VInfo();

	vi.class_indent = getIndentString(node.getFirstToken());

	Token t = node.getFirstToken();

	while (!t.image.equals("interface")) {
	    t = t.next;
	}

	vi.unit = ClassPool.getClass(((VInfo) vi_outer).unit.getName() + "$" 
				     + t.next.image);
	vi.unitName = vi.unit.getName();
	vi.internals = vi.unit.callsInAssertions();

	return print(node, vi);
    } 

    /**
     * Handle a rescue clause.  We do it by accepting the RescueCatch
     * children, without printing the surrounding tokens.
     *
     * @param node the rescue clause
     * @param vi ignored
     *
     * @return null
     */
    public Object visit(JassRescueClause node, Object vi) {
       return node.childrenAccept(this, vi);
    } 

    /**
     * Visits a Jass block statement. If this nodes first child is 
     * an unmodified class declaration, it is printed unrecursively
     * else as normal.
     *
     * @param node this block statement
     * @param vi the VInfo
     *
     * @return the object returned by print or printNoRecurse
     */
    public Object visit(JassBlockStatement node, Object vi) {
	if (node.jjtGetChild(0).hasId(JJTUNMODIFIEDCLASSDECLARATION) 
	    || node.jjtGetChild(0).hasId(JJTUNMODIFIEDINTERFACEDECLARATION))
	    return printNoRecurse(node, vi);
	return print(node, vi);
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     *
     * @return null, always
     */
    public Object visit(JassChangeList node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     *
     * @return null, always
     */
    public Object visit(JassFieldReference node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassInvariantClause node, Object vi) {	
	return null;
    } 

    /**
     * Just returns <tt>vi</tt>.
     * @param node
     * @param vi the VInfo
     *
     * @return the VInfo
     */
    public Object visit(JassAssertionRangeExpression node, Object vi) {
	return vi;
    } 

    /**
     * Just returns <tt>vi</tt>.
     * @param node
     * @param vi the VInfo
     *
     * @return the VInfo
     */
    public Object visit(JassAssertionFiniteEnumerationCreation node, 
			Object vi) {
	return vi;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassEnsureClause node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassVariantClause node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassTraceConstant node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassProcessDeclarator node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassProcessIfElseExpression node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassProcessCall node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassConditionalCommunication node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassBasicProcess node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassCommunicationExpressions node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassCommunicationExpression node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassCommunication node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassCommunicationWithWildcards node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassCommunicationParameterList node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassCommunicationParameter node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassLayedDownParameter node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassTypedParameter node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassInputParameter node, Object vi) {
	return null;
    } 

    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassOutputParameter node, Object vi) {
	return null;
    } 
    /**
     * Just returns null. 
     *
     * @param node never used
     * @param vi never used
     * 
     * @return null, always
     */
    public Object visit(JassClassInvariantClause node, Object vi) {
	return null;
    } 

}


/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

