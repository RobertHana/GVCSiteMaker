/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.visitor;

import java.io.*;
import java.util.*;
import jass.GlobalFlags;
import jass.parser.*;
import jass.reflect.Class;    // to avoid conflicts with java.lang.Class
import jass.reflect.Method;    // to avoid conflicts with java.lang.reflect.Method
import jass.reflect.Modifier;    // to avoid conflicts with java.lang.reflect.Modifier
import jass.reflect.*;
import jass.reflect.traceAssertion.Process;    // to avoid conflicts with java.lang.Process
import jass.reflect.traceAssertion.*;
import jass.runtime.traceAssertion.MappedClass;
import jass.runtime.traceAssertion.TraceAssertionException;
import jass.runtime.util.*;
import jass.util.Set;

/**
 * Known Bugs: No brackets after method declarators are parsed.
 * @author Detelef Bartetzko
 * @author Michael Plath
 */
public class ReflectVisitor extends SimpleVisitor 
    implements JassParserTreeConstants {

    /*
     * If more than three question marks apear in a comment (like:
     * ?????) there is a place to modify or optimize the code !  
     */

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */
    /* Global informations, for all (inner)classes */
    protected String packName = null;
    protected Vector imports = new Vector();
    protected Class  topLevelClass = null;

    /**
     * This class represent a control object which carries all needed
     * information about the parsed class.  Most methods got this
     * class as parameter and add informations or make decisions for
     * further processing.  For the top level class and each inner
     * class an object of this class is generated.  
     */
    public class VInfo {
	public jass.reflect.Class  clazz = 
	    null;    /* the class this info belongs to */
	public jass.reflect.Method method = 
	    null;    /* the current method (or constructor) */
	public String		   unitName = null;
	public Vector		   fields = new Vector();
	public Vector		   methods = new Vector();
	public Vector		   constructors = new Vector();
	public Stack		   locals = new Stack();
	public Vector		   forall_exists = new Vector();
	public Vector		   rescues = new Vector();
	public Vector		   retries = new Vector();
	public int		   loopCounter = 0;
	public int		   checkCounter = 0;
	public int		   forallCounter = 0;
	public boolean		   inMethod = false;
	public Class		   outerClass = null;
	public boolean		   inLoop = false;

	// Information for Trace-Assertions
	TraceAssertion		   traceAssertion = null;
	TraceAssertionExpression   traceAssertionExpression = null;
	Process			   process = null;
	public jass.reflect.Class  traceAssertionClass;
    }

    /**
     * Class declaration
     *
     *
     * @author
     * @version %I%, %G%
     */
    public class ClassFake {
	Class mappedClass;
	VInfo vinfo;

	/**
	 * Constructor declaration
	 *
	 *
	 * @param outerVInfo
	 *
	 * @see
	 */
	public ClassFake(VInfo outerVInfo) {
	    vinfo = new VInfo();
	    vinfo.traceAssertionClass = outerVInfo.traceAssertionClass;
	    vinfo.traceAssertionExpression = 
		outerVInfo.traceAssertionExpression;
	}

	/**
	 * Method declaration
	 *
	 *
	 * @param process
	 *
	 * @return
	 *
	 * @see
	 */
	public Class begin(Process process) {

	    // A process of a trace assertion is mapped to a java class
	    mappedClass = process.getMappedClass();
	    vinfo.clazz = mappedClass;
	    vinfo.process = process;
	    vinfo.unitName = mappedClass.getName();

	    return mappedClass;
	} 

	/**
	 * Method declaration
	 *
	 *
	 * @param traceAssertion
	 *
	 * @return
	 *
	 * @see
	 */
	public Class begin(TraceAssertionExpression traceAssertion) {

	    // A process of a trace assertion is mapped to a java class
	    mappedClass = traceAssertion.getMappedClass();
	    vinfo.clazz = mappedClass;
	    vinfo.unitName = mappedClass.getName();

	    // System.out.println("Begin fake:" + mappedClass);
	    return mappedClass;
	} 

	/**
	 * Method declaration
	 *
	 *
	 * @see
	 */
	public void end() {

	    // set reflected infos ...
	    mappedClass.setImportedTypes(imports);
	    mappedClass.setInterface(false);
	    mappedClass.setModifier(Modifier.PUBLIC);
	    mappedClass.setDeclaredMethods(vinfo.methods);
	    mappedClass.setDeclaredConstructors(vinfo.constructors);
	    mappedClass.setForAll_Exists(vinfo.forall_exists);

	    // reflect info about superclass
	    String superclassname = 
		NameAnalysis.expandTypeName(MappedClass.CLASSNAME, 
					    mappedClass);
	    Class  superclass = ClassPool.getClass(superclassname);

	    mappedClass.setSuperclass(superclass);

	    // reflect info about interfaces implemented : no interfaces
	    // System.out.println("Ended fake:" + mappedClass);
	} 

	/**
	 * Method declaration
	 *
	 *
	 * @return
	 *
	 * @see
	 */
	public VInfo getVinfo() {
	    return vinfo;
	} 

    }

    /**
     * Class declaration
     *
     *
     * @author
     * @version %I%, %G%
     */
    public class MethodFake {
	VInfo   vinfo;
	int     loopCounter;
	int     checkCounter;
	Method  method;
	boolean inMethod;

	/**
	 * Constructor declaration
	 *
	 *
	 * @param vinfo
	 *
	 * @see
	 */
	public MethodFake(VInfo vinfo) {
	    this.vinfo = vinfo;
	}

	/**
	 * Method declaration
	 *
	 *
	 * @param fakedMethod
	 *
	 * @see
	 */
	public void begin(Method fakedMethod) {
	    loopCounter = vinfo.loopCounter;
	    checkCounter = vinfo.checkCounter;
	    method = vinfo.method;
	    inMethod = vinfo.inMethod;
	    vinfo.loopCounter = 0;
	    vinfo.checkCounter = 0;
	    vinfo.method = fakedMethod;
	    vinfo.inMethod = true;
	} 

	/**
	 * Method declaration
	 *
	 *
	 * @see
	 */
	public void end() {
	    vinfo.loopCounter = loopCounter;
	    vinfo.checkCounter = checkCounter;
	    vinfo.method = method;
	    vinfo.inMethod = inMethod;
	} 

    }

    /*
     * ....................................................................
     * . Constructors                                                     .
     * ....................................................................
     */

    // none

    /*
     * ....................................................................
     * . Private Methods                                                  .
     * ....................................................................
     */

    /**
     * Checks for reserved identifiers. Jass uses name patterns for
     * code generation. These patterns are reserved identifiers which
     * could not be used by the user program's code. This method
     * checks for the reserved identifiers and throws an exception
     * when a name conflict occurs.
     *
     * @param token
     * @param vi 
     *
     * @throws ReflectionError if a reserved identifier was used.
     *
     * @see */
    private void checkForNameclash(Token token, VInfo vi) {
	String id = token.image;
	int    line = token.beginLine;

	if (id.equals("jassResult") || id.equals("jassOld") 
		|| id.equals("jassParameters") || id.equals("jassRetry") 
		|| id.equals("jassException") || id.equals("jassCheck")) {
	    throw new ReflectionError
		(vi.unitName + ":" + line + " <Name " 
		 + id 
		 + " can not be declared. This name is generated.>");
	} 

	if (id.startsWith("jassVariant")) {
	    throw new ReflectionError
		(vi.unitName + ":" + line 
		 + " <Prefix jassVariant is used for code generation. " 
		 + "It can not be used as a prefix for variables.>");
	} 
    } 

    /**
     * Calculate the array type of the field. If the name includes 
     * brackets, the field is an array and we have to calculate the
     * dimension. 
     *
     * @param name the name including the brackets
     * @param type
     *
     * @return the array type (if the name includes brackets), or the
     *         given type (otherwise).
     */
    private Class getClass(String name, Class type) {
	if (name.indexOf("[") >= 0) {
	    int dims = (name.lastIndexOf("]") - name.indexOf("[") + 1) / 2;

	    int    hd = type.getArrayDims();
	    String hn;

	    if (hd > 0) {
		hn = type.getComponentType().getName();
	    } else {
		hn = type.getName();
	    }

	    return ClassPool.getClass(ClassPool.componentToArray(hn, 
                                                                 dims + hd));
	} else {
	    return type;
	} 
    } 


    /**
     * Method declaration
     *
     *
     * @param node
     * @param op_type
     * @param vi
     *
     * @return
     */
    private BinaryExpression handleBinaryExpression(Node node, int op_type, 
	    Object vi) {
	Expression[]     vexpr = new Expression[node.jjtGetNumChildren()];
	String[]	 vimage = new String[node.jjtGetNumChildren() - 1];
	BinaryExpression be = new BinaryExpression();

	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    vexpr[i] = (Expression) node.jjtGetChild(i).jjtAccept(this, vi);

	    if (i != 0) {
		vimage[i - 1] = 
		    node.jjtGetChild(i - 1).getLastToken().next.image;
	    } 
	} 

	be.setLine(node.getFirstToken().beginLine);
	be.setContainer(((VInfo) vi).clazz);
	be.setChildren(vexpr);
	be.setImages(vimage);
	be.setOperatorType(op_type);

	return be;
    } 

    /**
     * Build an expression. This method is very complex and has many
     * side effects.  Here the expression prefix and suffix will be
     * concatenated to an expression.  This is done by (re)classifiing
     * the found expression. Example: We have parsed a NameExpression
     * (as prefix or concatenation of SimpleName suffixes) and got an
     * ArgumentExpression as new suffix. Than we have to reclassify
     * the last SimpleNameExpression (in the NameExpression) as a
     * methodname and create a MethodCallExpression.  The expression
     * "this.field1.m(n)" is parsed like that:
     * 
     * <pre>
     * The prefix:
     * 
     * NameExpression
     * - SimpleNameExpression (this);
     * - SimpleNameExpression (field1);
     * - SimpleNameExpression (m);
     * 
     * Suffix:
     * 
     * ArgumentExpression (n)
     * 
     * Will be classified as:
     * 
     * NameExpression
     * - SimpleNameExpression (this);
     * - SimpleNameExpression (field1);
     * - MethodCallExpression (m)
     * - SimpleNameExpression (n)
     * </pre>
     *
     * @param node
     * @param vinfo
     *
     * @return
     *
     * @see
     */
    private Expression handlePrimaryExpression(Node node, Object vinfo) {
	VInfo      vi = (VInfo) vinfo;
	Expression expr = (Expression) node.jjtGetChild(0).jjtAccept(this, 
		vinfo);
	Expression suffix;

	for (int i = 1; i < node.jjtGetNumChildren(); i++) {
	    suffix = (Expression) node.jjtGetChild(i).jjtAccept(this, vinfo);

	    // Check the three possibilities of PrimarySuffix:
	    // 1.) "." <IDENTIFIER> [returned as SimpleNameExpression]
	    // get a longer name like field1.field2.field3 ...
	    // 2.) Arguments [returned as ArgumentExpression]
	    // the previous name was a method name
	    // 3.) "[" Expression "]" [returned as Expression]
	    // the previous name was an array name
	    if (suffix instanceof ArrayDimReferenceExpression) {

		// Third case: array access (suffix is a Expression) !
		if (expr instanceof jass.reflect.SimpleNameExpression) {

		    // last SimpleName was an array
		    expr = 
			new ArrayAccessExpression((SimpleNameExpression) expr, 
						  suffix);

		    expr.setContainer(vi.clazz);
		    expr.setLine(node.getFirstToken().beginLine);
		} else {
		    if (expr instanceof NameExpression) {
			((NameExpression) expr).insertArrayAccess(suffix);
		    } else {
			if (expr instanceof ArrayAccessExpression) {
			    ((ArrayAccessExpression) expr).addChild(suffix);
			} else {
			    if (expr instanceof MethodCallExpression 
				    || expr instanceof NestedExpression) {
				expr = 
				    new ArrayAccessExpression((Expression) expr, 
							      suffix);

				expr.setContainer(vi.clazz);
				expr.setLine(node.getFirstToken().beginLine);
			    } 
			} 
		    } 
		} 
	    } else {

		// First case:
		// SimpleName ?
		if (suffix instanceof SimpleNameExpression) {
		    if (expr instanceof SimpleNameExpression 
			    || expr instanceof MethodCallExpression 
			    || expr instanceof ArrayAccessExpression 
			    || expr instanceof NestedExpression) {

			// create a NameExpression
			NameExpression ne = new NameExpression();

			ne.setContainer(vi.clazz);
			ne.setLine(node.getFirstToken().beginLine);
			ne.addChild(expr);
			ne.addChild(suffix);

			expr = ne;
		    } else if (expr instanceof NameExpression) {

			// expand the NameExpression
			((NameExpression) expr).addChild(suffix);
		    } 
		} 

		// Second case:
		// MethodCall ?
		if (suffix instanceof ArgumentExpression) {

		    // last SimpleName was the name of a method !
		    if (expr instanceof SimpleNameExpression) {
			expr = 
			    new MethodCallExpression(((SimpleNameExpression) expr).getName(), 
						     (ArgumentExpression) suffix);

			expr.setContainer(vi.clazz);
			expr.setLine(node.getFirstToken().beginLine);
			vi.clazz.addPrimaryNode((SimpleNode) node, 
						(MethodCallExpression) expr);
		    } else if (expr instanceof NameExpression) {
			((NameExpression) expr).insertMethodCall((ArgumentExpression) suffix);
			vi.clazz.addPrimaryNode((SimpleNode) node, 
						(MethodCallExpression) expr.getLastChild());
		    } else if (expr instanceof ArrayAccessExpression) {
			NameExpression ne = new NameExpression();

			ne.setContainer(vi.clazz);
			ne.setLine(node.getFirstToken().beginLine);
			ne.addChild(expr);
			ne.addChild(suffix);

			expr = ne;
		    } 

		    // This case can not happen.
		    else if (expr instanceof MethodCallExpression) {
			System.err.println("Fatal error: ReflectVisitor [handlePrimaryExpression] : " 
					   + "ArgumentExpression follows ArgumentExpression !");
			System.exit(1);
		    } 
		} 
	    } 
	} 

	return expr;
    } 

    /**
     * Method declaration
     *
     *
     * @param communicationExpression
     * @param name
     * @param lastToken
     * @param vinfo
     *
     * @see
     */
    private static void setCommunicationType(CommunicationExpression communicationExpression, 
					     String name, Token lastToken, 
					     VInfo vinfo) {
	boolean hasBeginOrEnd = (lastToken.kind == DOT);

	if (!hasBeginOrEnd) {
	    communicationExpression.setCommunicationType(Communication.TYPE_BOTH);
	} else {
	    Token  tokenIdentifier = lastToken.next;
	    String identifier = tokenIdentifier.image;

	    if (identifier.equals("begin") || identifier.equals("b")) {
		communicationExpression.setCommunicationType(Communication.TYPE_BEGIN);
	    } else if (identifier.equals("end") || identifier.equals("e")) {
		communicationExpression.setCommunicationType(Communication.TYPE_END);
	    } else {
		throw new ReflectionError("Expected ' ', '.b', '.begin', 'e', '.end' at end of method" 
					  + name 
					  + TraceAssertionException.errorString(vinfo.traceAssertionClass.getName(), 
					  tokenIdentifier.beginLine));
	    } 
	} 
    } 

    /*
     * ....................................................................
     * . Public Methods                                                   .
     * ....................................................................
     */

    /**
     * reflect JassCompilationUnit
     *
     * @param node
     * @param vi
     *
     * @return always returns null
     *
     * @see jass.visitor.SimpleVisitor
     */
    public Object visit(JassCompilationUnit node, 
			Object o /* <- not of interest */) {

	/* CompilationUnit ::= ( PackageDeclaration )? ( ImportDeclaration )* 
	   ( TypeDeclaration )* <EOF> */
	NameAnalysis.clearCache();

	// visit all children ...
	node.childrenAccept(this, null);

	// ClassPool.dump(new PrintWriter(System.out,true),"");
	return topLevelClass;
    } 

    /**
     * reflect PackageDeclaration
     *
     * @param node
     * @param vi
     *
     * @return always returns null
     *
     * @see jass.visitor.SimpleVisitor
     */
    public Object visit(JassPackageDeclaration node, 
			Object o /* <- not of interest */) {

	/* PackageDeclaration ::= "package" Name ";" */
	packName = (String) node.jjtGetChild(0).jjtAccept(this, null);

	return null;
    } 

    /**
     * reflect ImportDeclaration
     *
     * @param node the syntax tree node
     * @param o not of interest
     *
     * @return always returns null
     *
     * @see jass.visitor.SimpleVisitor
     */
    public Object visit(JassImportDeclaration node, 
			Object o /* <- not of interest */) {

	/* ImportDeclaration ::= "import" Name ( "." "*" )? ";" */

	// get name of imported package ...
	StringBuffer s = 
	    new StringBuffer((String) node.jjtGetChild(0).jjtAccept(this, 
		null));

	// handle import-on-demand ...
	Token	     t = node.getFirstToken();
	boolean      star = false;

	while (t != node.getLastToken()) {

	    // is token before ";" a star ?
	    if (t.next == node.getLastToken() && t.image == "*") {
		star = true;
	    } 

	    t = t.next;
	} 

	if (star) {

	    // insert a import-on-demand-name (package)
	    s.append(".*");
	} 

	imports.addElement(s.toString());

	return null;
    } 

    /**
     * reflect TypeDeclaration
     *
     * @param node the syntax tree node
     * @param o not of interest
     *
     * @return always returns null
     *
     * @see jass.visitor.SimpleVisitor
     */
    public Object visit(JassTypeDeclaration node, 
			Object o /* <- not of interest */) {

	/* TypeDeclaration ::= ( ClassDeclaration | InterfaceDeclaration | ";" ) */

	// visit all children ...
	node.childrenAccept(this, null);

	return null;
    } 

    /*
     * ******************
     * CLASS nonterminals
     * ******************
     */

    /**
     * Reflect the declaration of a class, and store the reflection 
     * result to the ClassPool. This is done by reflecting the unmodified
     * class and adding the modifiers.
     *
     * @param node  the class declaration
     * @param o     is ignored
     *
     * @return null
     */
    public Object visit(JassClassDeclaration node, Object o) {

	/* ClassDeclaration ::= 
	   ( "abstract" | "final" | "public" )* UnmodifiedClassDeclaration */

	// first visit the UnmodifiedClassDeclaration to get an class object 
	// from the ClassPool !
	VInfo vi = new VInfo();

	node.childrenAccept(this, vi);

	Token t = node.getFirstToken();
	// search "class" token, set modifiers
        jass.reflect.Modifier.setModifier(jass.reflect.Modifier.PACKAGE);
	while (t.kind != CLASS) {
	    jass.reflect.Modifier.setFromString(t.image);
	    t = t.next;
	} 

	((VInfo) vi).clazz.setModifier(jass.reflect.Modifier.getModifier());

	return null;
    } 

    /**
     * Reflect the declaration of a class (ignoring any modifier), and 
     * store the reflection result to the vinfo's clazz field and to the
     * ClassPool. The vinfo object must be of type VInfo.
     *
     * @param node   the class declaration node
     * @param vinfo  the reflection info (of class VInfo)
     *
     * @return null
     */
    public Object visit(JassUnmodifiedClassDeclaration node, Object vinfo) {

	/* 
	   UnmodifiedClassDeclaration ::=
	     "class" <IDENTIFIER> ( "extends" Name )? 
	     ( "implements" NameList )? ClassBody 
	*/
	VInfo	     vi = (VInfo) vinfo;

	// set name
	String       unit = node.getFirstToken().next.image;
	StringBuffer sb = new StringBuffer();

	// expand name to full-qualified classname
	if (vi.outerClass != null) {
	    sb.append(vi.outerClass.getName() + "$" + unit);
	} else {
	    if (packName != null) {
		sb.append(packName + ".");
	    } 

	    sb.append(unit);
	} 

	vi.unitName = sb.toString();

	// Get the class object from the ClassPool (class is in state 
	// unreflected)
	vi.clazz = ClassPool.getClass(vi.unitName);

	// class will be reflected now ...
	vi.clazz.setUnreflected(false);

	if (topLevelClass == null) {
	    topLevelClass = vi.clazz;

	    // set reflected infos ...
	} 

	vi.clazz.setImportedTypes(imports);

	// vi.clazz.setPackageName(packName);
	vi.clazz.setInterface(false);

	// line for error generation
	int child_line = 0;

	// is the next child a name or a namelist ?
	int childnum = 0;

	// reflect info about superclass
	if (node.jjtGetChild(childnum).hasId(JJTNAME)) {
	    child_line = node.jjtGetChild(childnum).getFirstToken().beginLine;

	    // child is name: set superclass (and increase childnum !)
	    String s = (String) node.jjtGetChild(childnum++).jjtAccept(this, 
						 vi);

	    // get the full-qualified name of superclass
	    String type_name = NameAnalysis.expandTypeName(s, vi.clazz);

	    if (type_name == null) {
		throw new ReflectionError(vi.unitName + ":" + child_line 
					  + " <Can not expand " + s 
					  + " to full qualified type name.>");
	    } 

	    Class superclass = ClassPool.getClass(type_name);

	    if (superclass == null) {
		throw new ReflectionError(vi.unitName + ":" + child_line 
					  + " <" + s 
					  + " is not a proper type.>");
	    } 

	    // add superclass
	    vi.clazz.setSuperclass(superclass);
	} else {

	    // no superclass ? => superclass is java.lang.Object !
	    vi.clazz.setSuperclass(ClassPool.getClass("java.lang.Object"));
	} 

	// reflect info about interfaces implemented
	if (node.jjtGetChild(childnum).hasId(JJTNAMELIST)) {
	    child_line = node.jjtGetChild(childnum).getFirstToken().beginLine;

	    // next child is namelist: set interfaces
	    // visiting a JassNameList returns a vector of the strings in the list !
	    Vector v = (Vector) node.jjtGetChild(childnum++).jjtAccept(this, 
						 vi);

	    // expand the names
	    for (int i = 0; i < v.size(); i++) {
		String s = (String) v.elementAt(i);
		String type_name = NameAnalysis.expandTypeName(s, vi.clazz);

		if (type_name == null) {
		    throw new ReflectionError(vi.unitName + ":" + child_line 
					      + " <Can not expand " + s 
					      + " to full qualified type name.>");
		} 

		Class h = ClassPool.getClass(type_name);

		if (h == null) {
		    throw new ReflectionError(vi.unitName + ":" + child_line 
					      + " <" + s 
					      + " is not a proper type.>");
		} 

		v.setElementAt(h, i);
	    } 

	    vi.clazz.setInterfaces(v);
	} 

	// traverse the class body ... (the last child)
	node.jjtGetChild(node.jjtGetNumChildren() - 1).jjtAccept(this, vi);
	vi.clazz.setDeclaredFields(vi.fields);
	vi.clazz.setDeclaredMethods(vi.methods);
	vi.clazz.setDeclaredConstructors(vi.constructors);
	vi.clazz.setForAll_Exists(vi.forall_exists);

	return null;
    } 

    /**
     * Visit a nested class by putting the class information of the
     * reflection info object (vi_outer) to the outer class information
     * of a new VInfo object.
     *
     * @param node      The nested class declaration.
     * @param vi_outer  the reflection info object (of class VInfo) of
     *                  the outer class (we are interested in the
     *                  clazz field of this object)
     * @return null
     */
    public Object visit(JassNestedClassDeclaration node, Object vi_outer) {

	/* NestedClassDeclaration ::=
	   ( "static" | "abstract" | "final" | "public" | "protected" | 
	     "private" )* UnmodifiedClassDeclaration 
	*/

	// first visit the UnmodifiedClassDeclaration to get an class object from the ClassPool !
	VInfo vi = new VInfo();

	vi.outerClass = ((VInfo) vi_outer).clazz;

	node.childrenAccept(this, vi);

	Token t = node.getFirstToken();

	// search "class" token, set modifiers
	while (t.kind != CLASS) {
	    jass.reflect.Modifier.setFromString(t.image);

	    t = t.next;
	} 

	((VInfo) vi).clazz.setModifier(jass.reflect.Modifier.getModifier());

	return null;
    } 

    /**
     * A ClassBody is reflected by simply reflecting a children.
     *
     * @param node  the syntax tree node (ClassBody)
     * @param vi    the reflection info object (of class VInfo)
     *
     * @return null
     */
    public Object visit(JassClassBody node, Object vi) {

	// ClassBody ::= "{" ( ClassBodyDeclaration )* ( ClassInvariantClause )? "}"

	/* TraceAssertion not reflected */

	// visit all definitions in the class body
	node.childrenAccept(this, vi);

	return null;
    } 

    /**
     * Reflect a ClassInvariantClause node of the syntax tree, by adding
     * the children (the assertions) to the class, either as invariant
     * or as trace assertions.
     *
     * @param node  the syntax tree node (ClassInvariantClause)
     * @param vi    the reflection info object (of class VInfo - we are
     *              interested in the clazz field of this object)
     *
     * @return null
     */
    public Object visit(JassClassInvariantClause node, Object vi) {

	// ClassInvariantClause ::= <INVARIANT> ( InvariantAssertion ";" )+ <ASS_END>
	VInfo	       vinfo = (VInfo) vi;
	int	       numberOfAssertionExpressions = 
	    node.jjtGetNumChildren();
	TraceAssertion traceAssertion = new TraceAssertion(packName);

	traceAssertion.setContainer(vinfo.clazz);

	vinfo.traceAssertion = traceAssertion;

	Vector booleanAssertionExpressions = new Vector();
	Vector traceAssertionExpressions = new Vector();

	for (int i = 0; i < numberOfAssertionExpressions; i++) {
	    AssertionExpression assertionExpression = 
		(AssertionExpression) node.jjtGetChild(i).jjtAccept(this, vi);

	    if (assertionExpression instanceof BooleanAssertionExpression) {
		booleanAssertionExpressions.add(assertionExpression);
	    } else if (assertionExpression 
		       instanceof TraceAssertionExpression) {
		traceAssertionExpressions.add(assertionExpression);
	    } 
	} 

	if (!booleanAssertionExpressions.isEmpty()) {
	    Invariant inv = new Invariant();

	    inv.setAssertionExpressions(booleanAssertionExpressions);
	    vinfo.clazz.setInvariant(inv);
	} 

	if (!traceAssertionExpressions.isEmpty()) {
	    traceAssertion.setAssertionExpressions(traceAssertionExpressions);
	    vinfo.clazz.setTraceAssertion(traceAssertion);
	} 

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     */
    public Object visit(JassInvariantAssertion node, Object vi) {

	// InvariantAssertion ::= ( TraceAssertion | BooleanAssertion )
	AssertionExpression assertionExpression;

	assertionExpression = 
	    (AssertionExpression) node.getFirstChild().jjtAccept(this, vi);

	return assertionExpression;
    } 

    /**
     * reflect ClassBodyDeclaration
     *
     * @param node
     * @param vi
     *
     * @return always returns null
     *
     * @see jass.visitor.SimpleVisitor
     */
    public Object visit(JassClassBodyDeclaration node, Object vi) {

	// ClassBodyDeclaration ::=
	// (
	// Initializer | NestedClassDeclaration | NestedInterfaceDeclaration
	// | ConstructorDeclaration | MethodDeclaration | FieldDeclaration
	// )
	// visit the definitions (initializer are not reflected ?????)
	node.childrenAccept(this, vi);

	return null;
    } 

    // **********************************
    // methods for INTERFACE nonterminals
    // **********************************

    /**
     * Method declaration
     *
     *
     * @param node
     * @param o
     *
     * @return
     *
     * @see
     */
    public Object visit(JassInterfaceDeclaration node, Object o) {
	/* InterfaceDeclaration ::= ( "abstract" | "public" )* UnmodifiedInterfaceDeclaration */

	// first visit the UnmodifiedInterfaceDeclaration to get an class (interface) object from the ClassPool !
	VInfo vi = new VInfo();

	node.childrenAccept(this, vi);

	// search "interface" token, set modifiers
	Token t = node.getFirstToken();

	while (t.image != "interface") {
	    jass.reflect.Modifier.setFromString(t.image);

	    t = t.next;
	} 

	((VInfo) vi).clazz.setModifier(jass.reflect.Modifier.getModifier());

	return null;
    } 

    /**
     * reflect UnmodifiedInterfaceDeclaration
     *
     * @param node
     * @param vinfo
     *
     * @return
     *
     * @see
     */
    public Object visit(JassUnmodifiedInterfaceDeclaration node, 
			Object vinfo) {
	/*
	   UnmodifiedInterfaceDeclaration ::=
	      "interface" <IDENTIFIER> ( "extends" NameList )? 
	      "{" (InterfaceMemberDeclaration)* "}"
	*/
	VInfo	     vi = (VInfo) vinfo;

	// set name
	String       unit = node.getFirstToken().next.image;
	StringBuffer sb = new StringBuffer();

	if (vi.outerClass != null) {
	    sb.append(vi.outerClass.getName() + "$" + unit);
	} else {
	    if (packName != null) {
		sb.append(packName + ".");
	    } 

	    sb.append(unit);
	} 

	vi.unitName = sb.toString();

	// Get the class object from the ClassPool (class is in state unreflected)
	vi.clazz = ClassPool.getClass(vi.unitName);

	// class will be reflected now ...
	vi.clazz.setUnreflected(false);

	if (topLevelClass == null) {
	    topLevelClass = vi.clazz;

	    // set reflected infos
	} 

	vi.clazz.setImportedTypes(imports);

	// vi.clazz.setPackageName(packName);
	// is interface !
	vi.clazz.setInterface(true);

	// is the next child a name or a namelist ?
	int child = 0;

	// reflect extended interfaces
	if (node.jjtGetNumChildren() > child 
		&& node.jjtGetChild(child).hasId(JJTNAMELIST)) {
	    int    child_line = 
		node.jjtGetChild(child).getFirstToken().beginLine;

	    // child is namelist: get the extended interfaces
	    // increase child
	    Vector v = (Vector) node.jjtGetChild(child++).jjtAccept(this, vi);

	    // expand the names
	    for (int i = 0; i < v.size(); i++) {
		String s = (String) v.elementAt(i);
		String type_name = NameAnalysis.expandTypeName(s, vi.clazz);

		if (type_name == null) {
		    throw new ReflectionError(vi.unitName + ":" + child_line 
					      + " <Can not expand " + s 
					      + " to full qualified type name.>");
		} 

		Class h = ClassPool.getClass(type_name);

		if (h == null) {
		    throw new ReflectionError(vi.unitName + ":" + child_line 
					      + " <" + s 
					      + " is not a proper type.>");
		} 

		v.setElementAt(h, i);
	    } 

	    vi.clazz.setInterfaces(v);
	} 

	// traverse the InterfaceMemberDeclaration(s) ... (the last child)
	for (int i = child; i < node.jjtGetNumChildren(); i++) {
	    node.jjtGetChild(i).jjtAccept(this, vi);
	}

	vi.clazz.setDeclaredFields(vi.fields);
	vi.clazz.setDeclaredMethods(vi.methods);
	vi.clazz.setDeclaredConstructors(vi.constructors);
	vi.clazz.setForAll_Exists(vi.forall_exists);
	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi_outer
     *
     * @return
     *
     * @see
     */
    public Object visit(JassNestedInterfaceDeclaration node, 
			Object vi_outer) {

	/*
	 * NestedInterfaceDeclaration ::=
	 * ( "static" | "abstract" | "final" | "public" | "protected" | "private" )* UnmodifiedInterfaceDeclaration
	 */
	VInfo vi = new VInfo();

	vi.outerClass = ((VInfo) vi_outer).clazz;

	node.childrenAccept(this, vi);

	Token t = node.getFirstToken();

	// search "class" token, set modifiers
	while (t.image != "interface") {
	    jass.reflect.Modifier.setFromString(t.image);

	    t = t.next;
	} 

	((VInfo) vi).clazz.setModifier(jass.reflect.Modifier.getModifier());

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassInterfaceMemberDeclaration node, Object vi) {
	return node.childrenAccept(this, vi);
    } 

    // ******************************
    // methods for FIELD nonterminals
    // ******************************

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassFieldDeclaration node, Object vi) {

	// FieldDeclaration ::=
	// ( "public" | "protected" | "private" | "static" | "final" | "transient" | "volatile" )*
	// Type VariableDeclarator ( "," VariableDeclarator )* ";"
	// get the token of the first child (Type)
	jass.reflect.Modifier.clear();

	Token next_unit_token = node.jjtGetChild(0).getFirstToken();
	Token t = node.getFirstToken();

	// set modifier
	while (t != next_unit_token) {
	    jass.reflect.Modifier.setFromString(t.image);

	    t = t.next;
	} 

	// no modifikations ? => set package
	if (jass.reflect.Modifier.getModifier() 
		== jass.reflect.Modifier.NOTHING) {
	    jass.reflect.Modifier.setPackage();
	} 

	int   modifiers = jass.reflect.Modifier.getModifier();

	// visiting JassType returns a classobject of the type
	Class type = (Class) node.jjtGetChild(0).jjtAccept(this, vi);

	// visit the VariableDeclarators ...
	for (int i = 1; i < node.jjtGetNumChildren(); i++) {
	    VariableDeclarator variableDeclarator = 
		(VariableDeclarator) node.jjtGetChild(i).jjtAccept(this, vi);
	    String	       name = variableDeclarator.getId();

	    // some work to do: array brackets after identifier name !
            if (name.indexOf("[") >= 0) {
                type = getClass(name, type);
                name = name.substring(0, name.indexOf("["));
                variableDeclarator = 
                    new VariableDeclarator(name, 
                           variableDeclarator.getInitializerString());
            }
	    jass.reflect.Field f = new jass.reflect.Field(variableDeclarator);

	    f.setType(type);
	    f.setModifier(modifiers);
	    ((VInfo) vi).fields.addElement(f);
	} 

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     */
    public Object visit(JassType node, Object vi) {

	/* Type ::= ( PrimitiveType | Name ) ( "[" "]" )* */
	Token      token;
	String     type_name;
	Class      type;

	// retrieve child node (PrimitiveType or Name)
	SimpleNode subNode = (SimpleNode) node.jjtGetChild(0);

	token = subNode.getLastToken();

	if (subNode.hasId(JJTPRIMITIVETYPE)) {

	    // child is of primitive type
	    type_name = subNode.getFirstToken().image;
	} else {

	    // child is of reference type
	    String s = (String) node.jjtGetChild(0).jjtAccept(this, vi);

	    type_name = NameAnalysis.expandTypeName(s, ((VInfo) vi).clazz);

	    if (type_name == null) {
		throw new ReflectionError(((VInfo) vi).unitName + ":" 
					  + subNode.getFirstToken().beginLine 
					  + " <Can not expand " + s 
					  + " to full qualified type name.>");
	    } 
	} 

	int arrayDims = 0;

	// array dims used ?
	while (token != node.getLastToken()) {
	    token = token.next;
	    arrayDims++;
	} 

	arrayDims = arrayDims / 2;
	type_name = ClassPool.componentToArray(type_name, arrayDims);
	type = ClassPool.getClass(type_name);

	return type;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassVariableDeclarator node, Object vi) {

	// VariableDeclarator ::= VariableDeclaratorId ( "=" VariableInitializer )?
	String  declaratorId = (String) node.jjtGetChild(0).jjtAccept(this, 
		vi);
	String  initializer = null;
	boolean hasInitializer = node.jjtGetNumChildren() > 1;

	if (hasInitializer) {
	    Node nodeInitializer = node.jjtGetChild(1);

	    initializer = ToJava.node(nodeInitializer);

	    // visit initializer for further reflections
	    nodeInitializer.jjtAccept(this, vi);
	} 

	return new VariableDeclarator(declaratorId, initializer);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassVariableInitializer node, Object vi) {

	/* VariableInitializer ::= ( ArrayInitializer | Expression ) */
	Object o = node.jjtGetChild(0).jjtAccept(this, vi);

	// expressions will be added to current method or class for dataflow analysis
	if (node.jjtGetChild(0).hasId(JJTEXPRESSION)) {
	    if (((VInfo) vi).inMethod) {
		((VInfo) vi).method.addExpression((Expression) o);
	    } else {
		((VInfo) vi).clazz.addExpression((Expression) o);
	    }
	} 

	return o;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassArrayInitializer node, Object vi) {

	/* ArrayInitializer ::= "{" ( VariableInitializer ( "," VariableInitializer )* )? ( "," )? "}" */
	return node.childrenAccept(this, vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassVariableDeclaratorId node, Object vi) {

	// check nameclashes
	checkForNameclash(node.getFirstToken(), (VInfo) vi);

	return getStringRepresentation(node);
    } 

    /*
     * ************************
     * CONSTRUCTOR nonterminals
     * ***********************
     */

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vinfo
     *
     * @return
     *
     * @see
     */
    public Object visit(JassConstructorDeclaration node, Object vinfo) {

	/*
	 * ConstructorDeclaration ::=
	 * ( "public" | "protected" | "private" )? <IDENTIFIER> FormalParameters ( "throws" NameList )? "{"
	 * ( ExplicitConstructorInvocation )? MethodBodyBlock "}"
	 */
	VInfo vi = (VInfo) vinfo;

	// reset all counters
	vi.loopCounter = 0;
	vi.checkCounter = 0;

	// set informations
	jass.reflect.Constructor con = new jass.reflect.Constructor();

	vi.method = con;
	vi.inMethod = true;

	con.setDeclaringClass(vi.clazz);
	con.setType(vi.clazz);

	Token t = node.getFirstToken();

	// set modifiers
	jass.reflect.Modifier.clear();

	if (t.image.equals("public") || t.image.equals("protected") 
		|| t.image.equals("private")) {
	    jass.reflect.Modifier.setFromString(t.image);

	    t = t.next;
	} 

	// no modifications ? => set package
	else {
	    jass.reflect.Modifier.setPackage();
	}

	con.setModifier(jass.reflect.Modifier.getModifier());

	/*
	 * // expand name
	 * if (packName != null) con.setName(packName+"."+t.image);
	 * else
	 */
	con.setName(t.image);

	// set the current method in vinfo to the constructor
	// reflect formal Parameters
	node.jjtGetChild(0).jjtAccept(this, vi);

	int child = 1;

	// Traverse NameList to get exceptions thrown by method ... (if present)
	if (node.jjtGetNumChildren() > child 
		&& node.jjtGetChild(child).hasId(JJTNAMELIST)) {

	    // namelist exists ...
	    Vector v = (Vector) node.jjtGetChild(child).jjtAccept(this, vi);

	    con.setExceptionTypes(v);

	    child++;
	} 

	// ExpliciteConstructorInvocation
	if (node.jjtGetNumChildren() > child 
		&& node.jjtGetChild(child).hasId(JJTEXPLICITCONSTRUCTORINVOCATION)) {
	    node.jjtGetChild(child).jjtAccept(this, vi);

	    child++;
	} 

	// Traverse MethodBodyBlock
	for (int i = child; i < node.jjtGetNumChildren(); i++) {
	    node.jjtGetChild(i).jjtAccept(this, vi);
	}

	vi.constructors.addElement(con);

	vi.inMethod = false;

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassExplicitConstructorInvocation node, Object vi) {
	ArgumentExpression	  e = 
	    (ArgumentExpression) node.jjtGetChild(0).jjtAccept(this, vi);
	ConstructorCallExpression cce = 
	    new ConstructorCallExpression(node.getFirstToken().image, e);

	cce.setContainer(((VInfo) vi).clazz);
	cce.setLine(node.getFirstToken().beginLine);
	((Constructor) ((VInfo) vi).method).setConstructorCall(cce);

	return null;
    } 

    /*
     * *******************
     * METHOD nonterminals
     * ******************
     */

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vinfo
     *
     * @return
     *
     * @see
     */
    public Object visit(JassMethodDeclaration node, Object vinfo) {

	// MethodDeclaration ::=
	// (
	// "public" | "protected" | "private"
	// | "static" | "abstract" | "final" | "native" | "synchronized"
	// )*
	// ResultType MethodDeclarator ( "throws" NameList )? ( "{" MethodBodyBlock "}" | ";" )
	VInfo		    vi = (VInfo) vinfo;
	jass.reflect.Method m = new jass.reflect.Method();

	vi.loopCounter = 0;
	vi.checkCounter = 0;
	vi.method = m;
	vi.inMethod = true;

	m.setDeclaringClass(vi.clazz);

	// get first token of ResultType
	Token next_unit_token = node.jjtGetChild(0).getFirstToken();
	Token t = node.getFirstToken();

	// set modifiers
	jass.reflect.Modifier.clear();

	while (t != next_unit_token) {
	    jass.reflect.Modifier.setFromString(t.image);

	    t = t.next;
	} 

	// no modifications ? => set package
	if (jass.reflect.Modifier.getModifier() == 0) {
	    jass.reflect.Modifier.setPackage();
	} 

	// get the type
	Class type = (Class) node.jjtGetChild(0).jjtAccept(this, vi);

	m.setReturnType(type);
	m.setModifier(jass.reflect.Modifier.getModifier());

	// visit the declarator
	node.jjtGetChild(1).jjtAccept(this, vi);

	// Traverse NAMELIST to get exceptions thrown by method ... (if present)
	int child = 2;

	if (node.jjtGetNumChildren() > child 
		&& node.jjtGetChild(child).hasId(JJTNAMELIST)) {

	    // namelist exists ...
	    Vector v = (Vector) node.jjtGetChild(child).jjtAccept(this, vi);

	    m.setExceptionTypes(v);

	    child++;
	} 

	// get last children
	SimpleNode lastChild = 
	    (SimpleNode) node.jjtGetChild(node.jjtGetNumChildren() - 1);

	if (lastChild.hasId(JJTMETHODBODYBLOCK)) {

	    // Traverse MethodBodyBlock for assertions ...
	    lastChild.jjtAccept(this, vi);
	} 

	vi.methods.addElement(m);

	vi.inMethod = false;

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassResultType node, Object vi) {

	/* ResultType ::= ( "void" | Type ) */
	if (node.jjtGetNumChildren() > 0) {

	    // Type
	    return node.jjtGetChild(0).jjtAccept(this, vi);
	} else {

	    // primitiver Datentyp oder void
	    return ClassPool.getClass(node.getFirstToken().image);
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassMethodDeclarator node, Object vi) {

	// MethodDeclarator ::= <IDENTIFIER> FormalParameters ( "[" "]" )*
	String methodname = node.getFirstToken().image;

	// nameclashes ...
	if (methodname.startsWith("jassCheck") 
		|| methodname.startsWith("jassInternal")) {
	    throw new ReflectionError(((VInfo) vi).unitName + ":" 
				      + node.getFirstToken().beginLine 
				      + " <No method with prefix jassCheck/jassInternal can be declared. Methods with this prefix will be generated.>");
	} 

	((VInfo) vi).method.setName(methodname);
	node.childrenAccept(this, vi);

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassFormalParameters node, Object vi) {

	// FormalParameters ::= "(" ( FormalParameter ( "," FormalParameter )* )? ")"
	Vector v = new Vector();

	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    jass.reflect.FormalParameter p = 
		(jass.reflect.FormalParameter) node.jjtGetChild(i).jjtAccept(this, 
		    vi);

	    v.addElement(p);
	} 

	((VInfo) vi).method.setFormalParameters(v);

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassFormalParameter node, Object vi) {

	/* FormalParameter ::= ( "final" )? Type VariableDeclaratorId */
	jass.reflect.FormalParameter p = new jass.reflect.FormalParameter();

	if (node.getFirstToken().image == "final") {
	    p.setFinal(true);
	} else {
	    p.setFinal(false);
	}

	Class  type = (Class) node.jjtGetChild(0).jjtAccept(this, vi);
	String name = (String) node.jjtGetChild(1).jjtAccept(this, vi);

	// reflect array brackets after identifier
	// ?????? better solution: VariableDeclaratorId class
	if (name.indexOf("[") >= 0) {
	    int dims = (name.lastIndexOf("]") - name.indexOf("[") + 1) / 2;

	    name = name.substring(0, name.indexOf("["));

	    int    hd = type.getArrayDims();
	    String hn;

	    if (hd > 0) {
		hn = type.getComponentType().getName();
	    } else {
		hn = type.getName();
	    }

	    type = ClassPool.getClass(ClassPool.componentToArray(hn, 
		    dims + hd));
	} 

	p.setType(type);
	p.setName(name);

	return p;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassMethodBodyBlock node, Object vi) {

	/* MethodBodyBlock ::= ( RequireClause )? ( BlockStatement )* ( EnsureClause )? */
	((VInfo) vi).locals.clear();
	node.childrenAccept(this, vi);

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     */
    public Object visit(JassName node, Object vi) {

	/* Name ::= <IDENTIFIER> ( "." <IDENTIFIER> )* */
	return getStringRepresentation(node);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     */
    public Object visit(JassNameList node, Object vi) {

	/* NameList ::= Name ( "," Name )* */
	Vector v = new Vector();

	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    v.addElement(node.jjtGetChild(i).jjtAccept(this, vi));
	} 

	return v;
    } 

    /*
     * **********************
     * STATEMENT nonterminals
     * *********************
     */

    /*
     * Most of this methods only retrieve informations about expressions in statements,
     * to get dataflow information !
     */

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassBlock node, Object vi) {

	/* Block ::= "{" ( BlockStatement )* "}" */

	// a block is the scope for local variables, push a Mark on the stack to remember
	// the locals that belong to this block
	((VInfo) vi).locals.push(new jass.util.StackMark());
	node.childrenAccept(this, vi);

	// remove elements till next mark from stack
	while (!(((VInfo) vi).locals.peek() instanceof jass.util.StackMark)) {
	    ((VInfo) vi).locals.pop();
	}

	// remove mark
	((VInfo) vi).locals.pop();

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassBlockStatement node, Object vi) {

	/*
	 * BlockStatement ::=
	 * ( LocalVariableDeclaration ";" | Statement | UnmodifiedClassDeclaration | UnmodifiedInterfaceDeclaration )
	 */

	// UnmodifiesClassDeclaration and UnmodifiedInterfaceDeclaration are not reflected. ?????
	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    if (node.jjtGetChild(i) instanceof JassLocalVariableDeclaration 
		    || node.jjtGetChild(i) instanceof JassStatement) {
		node.jjtGetChild(i).jjtAccept(this, vi);
	    } 
	} 

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassStatement node, Object vi) {

	/*
	 * Statement ::=
	 * ( LabeledStatement | Block | EmptyStatement | StatementExpression ";" | SwitchStatement | IfStatement
	 * | WhileStatement | DoStatement | ForStatement | BreakStatement | ContinueStatement | ReturnStatement
	 * | ThrowStatement | SynchronizedStatement | TryStatement | CheckClause )
	 */
	node.childrenAccept(this, vi);

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassForStatement node, Object vi) {

	/* ForStatement ::= "for" "(" ( ForInit )? ";" ( Expression )? ";" ( ForUpdate )? ")" AssertionClause */
	((VInfo) vi).loopCounter++;

	((VInfo) vi).locals.push(new jass.util.StackMark());

	((VInfo) vi).inLoop = true;

	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    if (node.jjtGetChild(i).hasId(JJTEXPRESSION)) {
		((VInfo) vi).method.addExpression((Expression) node.jjtGetChild(i).jjtAccept(this, 
			vi));
	    } else {
		node.jjtGetChild(i).jjtAccept(this, vi);
	    }
	}

	// remove elements till next mark from stack
	while (!(((VInfo) vi).locals.peek() instanceof jass.util.StackMark)) {
	    ((VInfo) vi).locals.pop();
	}

	// remove mark
	((VInfo) vi).locals.pop();

	((VInfo) vi).inLoop = false;

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassWhileStatement node, Object vi) {

	/* WhileStatement ::= "while" "(" Expression ")" AssertionClause */
	((VInfo) vi).loopCounter++;
	((VInfo) vi).inLoop = true;

	((VInfo) vi).method.addExpression((Expression) node.jjtGetChild(0).jjtAccept(this, 
		vi));
	node.jjtGetChild(1).jjtAccept(this, vi);

	((VInfo) vi).inLoop = false;

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassDoStatement node, Object vi) {

	/* DoStatement ::= "do" AssertionClause "while" "(" Expression ")" ";" */
	((VInfo) vi).loopCounter++;
	((VInfo) vi).inLoop = true;

	node.jjtGetChild(0).jjtAccept(this, vi);
	((VInfo) vi).method.addExpression((Expression) node.jjtGetChild(1).jjtAccept(this, 
		vi));

	((VInfo) vi).inLoop = false;

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassIfStatement node, Object vi) {

	/* IfStatement ::= "if" "(" Expression ")" Statement ( "else" Statement )? */
	((VInfo) vi).method.addExpression((Expression) node.jjtGetChild(0).jjtAccept(this, 
		vi));

	for (int i = 1; i < node.jjtGetNumChildren(); i++) {
	    node.jjtGetChild(i).jjtAccept(this, vi);
	}

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassThrowStatement node, Object vi) {

	/* ThrowStatement ::= "throw" Expression ";" */
	((VInfo) vi).method.addExpression((Expression) node.jjtGetChild(0).jjtAccept(this, 
		vi));

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassSynchronizedStatement node, Object vi) {

	/* SynchronizedStatement ::= "synchronized" "(" Expression ")" Block */
	((VInfo) vi).method.addExpression((Expression) node.jjtGetChild(0).jjtAccept(this, 
		vi));
	node.jjtGetChild(1).jjtAccept(this, vi);

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassReturnStatement node, Object vi) {

	/* ReturnStatement ::= "return" ( Expression )? ";" */
	if (node.jjtGetNumChildren() > 0) {
	    ((VInfo) vi).method.addExpression((Expression) node.jjtGetChild(0).jjtAccept(this, 
		    vi));
	} 

	if (((VInfo) vi).inLoop) {
	    ((VInfo) vi).method.setReturnInLoop(true);
	} 

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassSwitchStatement node, Object vi) {

	/* SwitchStatement::="switch" "(" Expression ")" "{" ( SwitchLabel )* "}" */
	((VInfo) vi).method.addExpression((Expression) node.jjtGetChild(0).jjtAccept(this, 
		vi));

	for (int i = 1; i < node.jjtGetNumChildren(); i++) {
	    node.jjtGetChild(i).jjtAccept(this, vi);
	}

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassSwitchLabel node, Object vi) {

	/* SwitchLabel ::= ( "case" Expression ":" | "default" ":" ) ( BlockStatement )* */
	int child = 0;

	if (node.jjtGetNumChildren() > 0 
		&& node.getFirstToken().equals("case")) {
	    ((VInfo) vi).method.addExpression((Expression) node.jjtGetChild(child++).jjtAccept(this, 
		    vi));
	} 

	for (int i = child; i < node.jjtGetNumChildren(); i++) {
	    node.jjtGetChild(i).jjtAccept(this, vi);
	}

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vinfo
     *
     * @return
     *
     * @see
     */
    public Object visit(JassBreakStatement node, Object vinfo) {

	/* BreakStatement ::= "break" ( <IDENTIFIER> )? ";" */

	// No labels allowed if loop contains invariant !
	VInfo vi = (VInfo) vinfo;

	if (vi.method.getLoopInvariant(vi.loopCounter) != null 
		&&!node.getFirstToken().next.image.equals(";")) {
	    throw new ReflectionError(vi.unitName + "." 
				      + vi.method.getIdString() + ":" 
				      + node.getFirstToken().next.beginLine 
				      + " <No label can be used in break-statement if loop has an invariant: " 
				      + node.getFirstToken().next.image 
				      + ".>");
	} 

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vinfo
     *
     * @return
     *
     * @see
     */
    public Object visit(JassContinueStatement node, Object vinfo) {

	/* ContinueStatement ::= "continue" ( <IDENTIFIER> )? ";" */

	// No labels allowed if loop contains invariant !
	VInfo vi = (VInfo) vinfo;

	if ((vi.method.getLoopInvariant(vi.loopCounter) != null || vi.method.getLoopVariant(vi.loopCounter) != null) 
		&&!node.getFirstToken().next.image.equals(";")) {
	    throw new ReflectionError(vi.unitName + "." 
				      + vi.method.getIdString() + ":" 
				      + node.getFirstToken().next.beginLine 
				      + " <No label can be used in continue-statement if loop has an invariant or a variant: " 
				      + node.getFirstToken().next.image 
				      + ".>");
	} 

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassLabeledStatement node, Object vi) {
	node.childrenAccept(this, vi);

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassTryStatement node, Object vi) {

	/* TryStatement ::= "try" Block ( "catch" "(" FormalParameter ")" Block )* ( "finally" Block )? */

	// we must extract the FormalParameter in the catch declaration as local variable
	// the scope is the following block ...
	// visit the try-Block ...
	node.jjtGetChild(0).jjtAccept(this, vi);

	int child = 1;

	while (child < node.jjtGetNumChildren() 
	       && node.jjtGetChild(child).hasId(JJTFORMALPARAMETER)) {

	    // FormalParameter in catch
	    FormalParameter p = 
		(FormalParameter) node.jjtGetChild(child).jjtAccept(this, vi);

	    // generate local variable
	    LocalVariable   l = new LocalVariable(p.getName());

	    l.setType(p.getType());
	    l.setFinal(p.isFinal());

	    if (((VInfo) vi).locals.contains(l)) {
		throw new ReflectionError(((VInfo) vi).unitName + ":" 
					  + node.jjtGetChild(child).getFirstToken().beginLine 
					  + " <Formal parameter in catch-statement can not hide local variable: " 
					  + l.getName() + ".>");

		// Can be easy implemented: type must be subclass of Throwable ?????
	    } 

	    ((VInfo) vi).locals.push(new jass.util.StackMark());
	    ((VInfo) vi).locals.push(l);
	    node.jjtGetChild(child++).jjtAccept(this, vi);

	    // remove elements till next mark from stack
	    while (!(((VInfo) vi).locals.peek() 
		     instanceof jass.util.StackMark)) {
		((VInfo) vi).locals.pop();
	    }

	    // remove mark
	    ((VInfo) vi).locals.pop();
	} 

	return null;
    } 

    /**
     * Reflect an assert statement.
     * We just add the expressions to the method.
     */
    public Object visit(JassAssertStatement node, Object vi) {
	/* AssertStatement ::= "assert" Expression [ ":" Expression ] ";" */
	((VInfo) vi).method.addExpression
	    ((Expression) node.jjtGetChild(0).jjtAccept(this, vi));
	if (node.jjtGetNumChildren() == 2)
	    ((VInfo) vi).method.addExpression
		((Expression) node.jjtGetChild(1).jjtAccept(this, vi));
	return null;
    } 

    /**
     * Reflect a statement.
     *
     * @param node  statement's syntax tree node
     * @param vi    the visit info object
     *
     * @return  the expression of the statement.
     */
    public Object visit(JassStatementExpression node, Object vi) {

	/*
	 * StatementExpression ::=
	 * ( PreIncrementExpression | PreDecrementExpression
	 * | PrimaryExpression ( "++" | "--" | AssignmentOperator Expression )? )
	 */
	if (node.getLastToken().image.equals("++") 
		|| node.getLastToken().image.equals("--")) {

	    // This is a postfix expression statement !
	    // create a unary additive expression
	    Expression e = 
		new UnaryAdditiveExpression((Expression) node.jjtGetChild(0).jjtAccept(this, vi), 
					    false, node.getLastToken().image);

	    ((VInfo) vi).method.addExpression(e);

	    return e;
	} else {
	    if (node.jjtGetNumChildren() > 1) {

		// this is an assignment !
		// get left hand
		Expression	     lhand = 
		    (Expression) node.jjtGetChild(0).jjtAccept(this, vi);
		String		     operator = 
		    node.jjtGetChild(1).getFirstToken().image;

		// get right hand
		Expression	     rhand = 
		    (Expression) node.jjtGetChild(2).jjtAccept(this, vi);
		AssignmentExpression ae = new AssignmentExpression(lhand, 
			operator, rhand);

		((VInfo) vi).method.addExpression(ae);

		return ae;
	    } else {

		// this is a Primary, PreIn or PreDecrementExpression
		Expression e = 
		    (Expression) node.jjtGetChild(0).jjtAccept(this, vi);

		((VInfo) vi).method.addExpression(e);

		return e;
	    } 
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassForInit node, Object vi) {

	/* ForInit ::= ( LocalVariableDeclaration | StatementExpressionList ) */
	return node.childrenAccept(this, vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassForUpdate node, Object vi) {

	/* ForUpdate ::= StatementExpressionList */
	return node.childrenAccept(this, vi);
    } 

    /**
     * Reflect a statement list by reflection each statement
     *
     * @param node  the syntax tree node of the statement list
     * @param vi  the visit context information (of type VInfo)
     *
     * @return  the result of the childrenAccept (null)
     */
    public Object visit(JassStatementExpressionList node, Object vi) {

	/* StatementExpressionList ::= StatementExpression ( "," StatementExpression )* */
	return node.childrenAccept(this, vi);
    } 

    /**
     * Reflect a local variable declaration and store the information to
     * the visit info object (vi of type VInfo).
     *
     * @param node  the syntax tree node of the local variable declaration
     * @param vi    the visit context information (of type VInfo)
     *
     * @return null
     */
    public Object visit(JassLocalVariableDeclaration node, Object vi) {

	// LocalVariableDeclaration ::=
	// ( "final" )? Type VariableDeclarator ( "," VariableDeclarator )*
	VInfo	      vinfo = (VInfo) vi;
	boolean       f;
	LocalVariable p;
	String	      name;

	if (((SimpleNode) node).getFirstToken().image == "final") {
	    f = true;
	} else {
	    f = false;
	}

	Class type = (Class) node.jjtGetChild(0).jjtAccept(this, vi);
	Class htype;

	for (int i = 1; i < node.jjtGetNumChildren(); i++) {
	    htype = type;
	    p = new LocalVariable();

	    VariableDeclarator variableDeclarator = 
		(VariableDeclarator) node.jjtGetChild(i).jjtAccept(this, vi);

	    name = variableDeclarator.getId();

	    if (name.indexOf("[") >= 0) {
		int dims = (name.lastIndexOf("]") - name.indexOf("[") + 1) 
			   / 2;

		name = name.substring(0, name.indexOf("["));

		int    hd = type.getArrayDims();
		String hn;

		if (hd > 0) {
		    hn = type.getComponentType().getName();
		} else {
		    hn = type.getName();
		}

		htype = ClassPool.getClass(ClassPool.componentToArray(hn, 
			dims + hd));
	    } 

	    p.setType(htype);
	    p.setName(name);
	    p.setFinal(f);
	    vinfo.locals.push(p);
	} 

	return null;
    } 

    /*
     * **********************
     * ASSERTION nonterminals
     * *********************
     */

    /* Different productions for the assertions, to make it easy 
       implementing other constraints ... */

    /**
     * Reflect a Jass loop invariant assertion and add it to the current 
     * method (according to the VInfo object vi).
     *
     * @param node  the syntax tree node of the assertion
     * @param vi    the visit info object (instance of VInfo)
     *
     * @return  null
     */
    public Object visit(JassInvariantClause node, Object vi) {

	// InvariantClause ::= <INVARIANT> ( BooleanAssertion ";" )+ <ASS_END>
	// method "jassGetSuperState()" can not be decorated !
	if (((VInfo) vi).method.getIdString().equals("jassGetSuperState()")) {
	    throw new ReflectionError(((VInfo) vi).unitName + ":" 
				      + node.getFirstToken().beginLine 
				      + " <Method 'jassGetSuperState()' can only be decorated with check assertions.>");
	} else {
	    LoopInvariant loop = new LoopInvariant(((VInfo) vi).loopCounter);
	    Vector	  v = new Vector();

	    for (int i = 0; i < node.jjtGetNumChildren(); i++) {

		// get AssertionExpressions and insert them
		v.addElement((BooleanAssertionExpression) node.jjtGetChild(i).jjtAccept(this, 
			vi));
	    } 

	    loop.setAssertionExpressions(v);
	    ((VInfo) vi).method.addAssertion(loop);
	} 

	return null;
    } 


    /**
     * Reflect a Jass loop variant assertion and add it to the current 
     * method (according to the VInfo object vi).
     *
     * @param node  the syntax tree node of the assertion
     * @param vi    the visit info object (instance of VInfo)
     *
     * @return  null
     */
    public Object visit(JassVariantClause node, Object vi) {

	// VariantClause ::= <VARIANT> AssertionExpression <ASS_END>
	if (((VInfo) vi).method.getIdString().equals("jassGetSuperState()")) {
	    throw new ReflectionError(((VInfo) vi).unitName + ":" 
				      + node.getFirstToken().beginLine 
				      + " <Method 'jassGetSuperState()' can only be decorated with check assertions.>");
	} else {
	    LoopVariant vari = new LoopVariant(((VInfo) vi).loopCounter);
	    Vector      v = new Vector();
	    Expression  e = (Expression) node.jjtGetChild(0).jjtAccept(this, 
		    vi);

	    vari.setExpression(e);
	    ((VInfo) vi).method.addAssertion(vari);
	} 

	return null;
    } 

    /**
     * Reflect a Jass variant or invariant assertion by reflecting
     * the nodes children.
     *
     * @param node  the syntax tree node of the assertion
     * @param vi    the visit info object (instance of VInfo)
     *
     * @return  null
     */
    public Object visit(JassAssertionClause node, Object vi) {

	/* AssertionClause ::= ( InvariantClause )? ( VariantClause )? Statement */
	node.childrenAccept(this, vi);

	return null;
    } 

    /**
     * Reflect a Jass precondition assertion and add it to the current 
     * method (according to the VInfo object vi).
     *
     * @param node  the syntax tree node of the assertion
     * @param vi    the visit info object (instance of VInfo)
     *
     * @return  null
     */
    public Object visit(JassRequireClause node, Object vi) {

	// RequireClause ::= <REQUIRE> ( BooleanAssertion ";" )+ <ASS_END>
	if (((VInfo) vi).method.getIdString().equals("jassGetSuperState()")) {
	    throw new ReflectionError(((VInfo) vi).unitName + ":" 
				      + node.getFirstToken().beginLine 
				      + " <Method 'jassGetSuperState()' can only be decorated with check assertions.>");
	} else {
	    Vector       v = new Vector();
	    Precondition precond = new Precondition();

	    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
		v.addElement((BooleanAssertionExpression) node.jjtGetChild(i).jjtAccept(this, 
			vi));
	    } 

	    precond.setAssertionExpressions(v);
	    ((VInfo) vi).method.addAssertion(precond);
	} 

	return null;
    } 

    /**
     * Reflect a Jass postcondition assertion and add it to the current 
     * method (according to the VInfo object vi).
     *
     * @param node  the syntax tree node of the assertion
     * @param vi    the visit info object (instance of VInfo)
     *
     * @return  null
     */
    public Object visit(JassEnsureClause node, Object vi) {

	// EnsureClause ::= <INVARIANT> ( BooleanChangeAssertion ";" )+ <ASS_END>
	if (((VInfo) vi).method.getIdString().equals("jassGetSuperState()")) {
	    throw new ReflectionError(((VInfo) vi).unitName + ":" 
				      + node.getFirstToken().beginLine 
				      + " <Method 'jassGetSuperState()' can only be decorated with check assertions.>");
	} else {
	    Vector	  v = new Vector();
	    Postcondition post = new Postcondition();

	    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
		Object ret = node.jjtGetChild(i).jjtAccept(this, vi);

		// Assertion
		if (ret instanceof BooleanAssertionExpression) {
		    v.addElement((BooleanAssertionExpression) ret);
		} 

		// Changelist
		else if (ret instanceof Vector) {
		    post.setChangeList((Vector) ret);
		} 
	    } 

	    post.setAssertionExpressions(v);
	    ((VInfo) vi).method.addAssertion(post);
	} 

	return null;
    } 

    /**
     * Reflect a Jass check statement and add it to the current method
     * (according to the VInfo object vi).
     *
     * @param node  the syntax tree node of the check statement
     * @param vi    the visit info object (instance of VInfo)
     *
     * @return  null
     */
    public Object visit(JassCheckClause node, Object vi) {

	// CheckClause ::= <CHECK> ( BooleanAssertion ";" )+ <ASS_END>
	// check is allowed in "jassGetSuperState"
	VInfo  vinfo = (VInfo) vi;
	Vector v = new Vector();
	Check  check = new Check(((VInfo) vi).checkCounter++);

	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    BooleanAssertionExpression booleanAssertionExpression = 
		(BooleanAssertionExpression) node.jjtGetChild(i).jjtAccept(this, 
		    vi);

	    v.addElement(booleanAssertionExpression);
	} 

	check.setAssertionExpressions(v);
	vinfo.method.addAssertion(check);

	return null;
    } 

    /**
     * Reflect a (grammar) BooleanAssertion by adding a label to
     * the childs expression.
     *
     * @param node  the syntax tree node of the BooleanAssertion
     * @param vi    the visit info object
     *
     * @return a jass.reflect.BooleanAssertionExpression reflecting the
     *         node.
     */
    public Object visit(JassBooleanAssertion node, Object vi) {

	// BooleanAssertion ::= [ AssertionLabel ] AssertionExpression */
	BooleanAssertionExpression booleanAssertionExpression;
	AssertionLabel		   label = null;
	int			   child = 0;

	if (node.jjtGetChild(child).hasId(JJTASSERTIONLABEL)) {
	    label = (AssertionLabel) node.jjtGetChild(child++).jjtAccept(this, 
						      vi);
	} 

	booleanAssertionExpression = new  BooleanAssertionExpression(
	    (Expression) node.jjtGetChild(child).jjtAccept(this, 
		vi));

	booleanAssertionExpression.setLabel(label);

	return booleanAssertionExpression;
    } 

    /**
     * Reflect a (grammar) AssertionExpression by converting it to a 
     * jass.reflect.Expression.
     *
     *
     * @param node  the AssertionExpression
     * @param vi  additional visit information
     *
     * @return  a jass.reflect.Expression representing the node
     */
    public Object visit(JassAssertionExpression node, Object vi) {
	// AssertionExpression ::=
	// ( AssertionForAllExistsExpression | AssertionConditionalExpression )
	return (Expression) node.getFirstChild().jjtAccept(this, vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionLabel node, Object vi) {

	// AssertionLabel ::= "[" <IDENTIFIER> "]"
	VInfo  vinfo = (VInfo) vi;
	String name;
	Token  tokenName = node.getFirstToken().next;

	name = tokenName.image;

	/*
	 * Vector parameters = new Vector();
	 * Token token = tokenName.next;
	 * while(token.kind != RPAREN)
	 * {
	 * // overread first brace-token and following comma-tokens
	 * token = token.next;
	 * 
	 * String parameter = token.image;
	 * SimpleNameExpression parameterExpression = new SimpleNameExpression(parameter);
	 * parameterExpression.setLocals(new Set(((VInfo)vi).locals.toArray()));
	 * parameterExpression.setLine(node.getFirstToken().beginLine);
	 * parameterExpression.setContainer(vinfo.clazz);
	 * parameters.add(parameterExpression);
	 * token = token.next;
	 * }
	 * SimpleNameExpression[] parameterArray = new SimpleNameExpression[parameters.size()];
	 * parameters.toArray(parameterArray);
	 * 
	 * AssertionLabel label = new AssertionLabel(name, parameterArray, vinfo.method);
	 */
	AssertionLabel label = new AssertionLabel(name);

	return label;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassBooleanChangeAssertion node, Object vi) {

	// BooleanChangeAssertion
	// ::= ( <CHANGEONLY> "{" ( ChangeList )? "}" | BooleanAssertion )
	if (node.getFirstToken().image == "changeonly") {

	    // visit the change list
	    Vector v;

	    // handle empty change list
	    if (node.jjtGetNumChildren() > 0) {
		v = (Vector) node.jjtGetChild(0).jjtAccept(this, vi);
	    } else {
		v = new Vector();
	    }

	    return v;
	} 

	// visit the normal BooleanAssertion ...
	else {
	    return node.jjtGetChild(0).jjtAccept(this, vi);
	}
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassChangeList node, Object vi) {

	/* ChangeList ::= FieldReference ( "," FieldReference )* */
	Vector v = new Vector();

	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    String s = (String) node.jjtGetChild(i).jjtAccept(this, vi);

	    // convert the String to an expression, this makes the parsing easy
	    if (s.indexOf('.') > -1) {
		NameExpression ne = new NameExpression();

		ne.setContainer(((VInfo) vi).clazz);
		ne.setLine(node.getFirstToken().beginLine);

		// empty Set cause no locals for this expression !
		ne.addChildren(s, new Set());
		v.addElement(ne);
	    } else {
		v.addElement(new SimpleNameExpression(s, 
						      node.getFirstToken().beginLine, 
						      ((VInfo) vi).clazz));
	    }
	} 

	return v;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassFieldReference node, Object vi) {

	/* FieldReference ::= ( "this" "." )? <IDENTIFIER> */
	return getStringRepresentation(node);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassRescueClause node, Object vi) {

	/* RescueClause ::= <RESCUE> ( RescueCatch ";" )+ <ASS_END> */
	if (GlobalFlags.VERBOSE > 0 
		&& GlobalFlags.MODE == GlobalFlags.WARNING) {
	    System.out.println("Warning: " + ((VInfo) vi).unitName + ":" 
			       + node.getFirstToken().beginLine 
			       + " <Rescue-clause does not function in WARNING mode.>");
	} 

	node.childrenAccept(this, vi);
	((VInfo) vi).method.setRescues(((VInfo) vi).rescues);
	((VInfo) vi).method.setRetries(((VInfo) vi).retries);

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassRescueCatch node, Object vi) {

	/* RescueCatch ::= "catch" "(" FormalParameter ")" Block  */
	FormalParameter p = 
	    (FormalParameter) node.jjtGetChild(0).jjtAccept(this, vi);

	((VInfo) vi).rescues.addElement(p);

	boolean retry = false;

	// look for a retry (important info for code generation !)
	for (int i = 1; i < node.jjtGetNumChildren(); i++) {
	    if (node.jjtGetChild(i).hasId(JJTRETRYSTATEMENT)) {
		retry = true;
	    } 
	}

	if (retry) {
	    ((VInfo) vi).retries.addElement(Boolean.TRUE);
	} else {
	    ((VInfo) vi).retries.addElement(Boolean.FALSE);
	}

	return null;
    } 

    /*
     * ****************************
     * TRACE-ASSERTION nonterminals
     * ***************************
     */

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassTraceAssertion node, Object vi) {

	// TraceAssertion ::= ( AssertionLabel )? TraceAssertionDeclaration
	VInfo vinfo = (VInfo) vi;

	vinfo.traceAssertionClass = vinfo.clazz;

	Node	       firstNonterminal = node.getFirstChild();
	Node	       nodeAssertionLabel = 
	    firstNonterminal.hasId(JJTASSERTIONLABEL) ? firstNonterminal 
	    : null;
	Node	       nodeTraceAssertionDeclaration = node.getLastChild();
	AssertionLabel assertionLabel;

	if (nodeAssertionLabel != null) {
	    assertionLabel = 
		(AssertionLabel) nodeAssertionLabel.jjtAccept(this, vinfo);
	} else {
	    assertionLabel = 
		new AssertionLabel(TraceAssertionExpression.newInternalNameForLabel());
	} 

	TraceAssertionExpression traceAssertionExpression = 
	    new TraceAssertionExpression(vinfo.traceAssertion, 
					 assertionLabel);

	traceAssertionExpression.setContainer(vinfo.clazz);
	traceAssertionExpression.setLine(node.getFirstToken().beginLine);

	vinfo.traceAssertionExpression = traceAssertionExpression;

	nodeTraceAssertionDeclaration.jjtAccept(this, vinfo);

	return traceAssertionExpression;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassTraceAssertionDeclaration node, Object vi) {

	// TraceAssertionDeclaration ::= <TRACE>
	// <TRACE> ( CommunicationExpressions )?
	// <LPAREN> ( TraceConstant )* ( ProcessDeclaration )* <RPAREN>
	VInfo			 vinfo = (VInfo) vi;
	TraceAssertionExpression traceAssertionExpression = 
	    vinfo.traceAssertionExpression;
	int			 numberOfChildren = node.jjtGetNumChildren();
	int			 childNr = 0;

	if (numberOfChildren == 0) {
	    return traceAssertionExpression;
	} 

	Node    firstNonterminal = node.getFirstChild();
	boolean explicitAlphabet = 
	    firstNonterminal.hasId(JJTCOMMUNICATIONEXPRESSIONS);

	if (explicitAlphabet) {
	    TraceAlphabet	     alphabet = new TraceAlphabet();
	    CommunicationExpressions expressions;

	    expressions = 
		(CommunicationExpressions) firstNonterminal.jjtAccept(this, 
		    vi);

	    alphabet.setCommunications(expressions);
	    traceAssertionExpression.setAlphabet(alphabet);

	    ++childNr;
	} 

	Node      actualNode;

	// A trace assertion is mapped to a java class
	// begin faking class
	ClassFake classFake = new ClassFake(vinfo);
	VInfo     vinfoFake = classFake.getVinfo();
	Class     mappedClass = classFake.begin(traceAssertionExpression);

	// class will be reflected now ...
	mappedClass.setUnreflected(false);

	if (topLevelClass == null) {
	    topLevelClass = mappedClass;

	    // set fields of class explicitly
	} 

	while (childNr < numberOfChildren 
	       && (actualNode = 
		   node.jjtGetChild(childNr)).hasId(JJTTRACECONSTANT)) {
	    actualNode.jjtAccept(this, vinfoFake);

	    ++childNr;
	} 

	mappedClass.setDeclaredFields(vinfoFake.fields);

	// end faking class. This sets also reflected values of class
	classFake.end();

	while (childNr < numberOfChildren 
	       && (actualNode = 
		   node.jjtGetChild(childNr)).hasId(JJTPROCESSDECLARATION)) {
	    Process process = (Process) actualNode.jjtAccept(this, vi);

	    traceAssertionExpression.add(process);

	    ++childNr;
	} 

	return traceAssertionExpression;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassCommunicationExpressions node, Object vi) {

	// CommunicationExpressions ::=
	// (
	// CommunicationExpression
	// | <LBRACE> CommunicationExpression ( <COMMA> CommunicationExpression )* <RBRACE>
	// )
	// ( <TRACE_COMPLEMENT_OPERATOR> CommunicationExpressions )?
	VInfo			 vinfo = (VInfo) vi;
	Token			 actualToken = node.getFirstToken();
	int			 childNr = 0;
	CommunicationExpressions communicationExpressions = 
	    new CommunicationExpressions();

	communicationExpressions.setFileBounds(vinfo.traceAssertionClass, 
					       node.getFirstToken().beginLine);

	Node nodeExpression = null;

	// There's at least one node of type CommunicationExpression
	while (childNr < node.jjtGetNumChildren() && (nodeExpression = 
		node.jjtGetChild(childNr++)).hasId(JJTCOMMUNICATIONEXPRESSION)) {
	    CommunicationExpression communicationExpression = 
		(CommunicationExpression) nodeExpression.jjtAccept(this, vi);

	    communicationExpressions.add(communicationExpression);
	} 

	boolean hasExceptions = 
	    nodeExpression.hasId(JJTCOMMUNICATIONEXPRESSIONS);

	if (hasExceptions) {

	    // Complement
	    CommunicationExpressions exceptions = 
		(CommunicationExpressions) nodeExpression.jjtAccept(this, vi);

	    communicationExpressions.setExceptions(exceptions);
	} 

	return communicationExpressions;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassCommunicationExpression node, Object vi) {

	// CommunicationExpression ::=
	// ( Communication | CommunicationWithWildcards )
	VInfo			vinfo = (VInfo) vi;
	Token			actualToken = node.getFirstToken();
	int			childNr = 0;
	CommunicationExpression communicationExpression;
	Node			nodeExpression = node.jjtGetChild(childNr++);

	communicationExpression = 
	    (CommunicationExpression) nodeExpression.jjtAccept(this, vi);
	actualToken = nodeExpression.getLastToken().next;

	communicationExpression.setContainer(vinfo.traceAssertionClass);
	communicationExpression.setLine(node.getFirstToken().beginLine);

	return communicationExpression;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassCommunication node, Object vi) {

	// Communication ::=
	// ( <THIS> "." )? Name
	// CommunicationParameterList ( "." <IDENTIFIER> )?
	VInfo   vinfo = (VInfo) vi;
	Node    nodeName = node.getFirstChild();
	Node    nodeParameterList = node.jjtGetChild(1);
	String  name = (String) nodeName.jjtAccept(this, vi);
	Token   firstToken = node.getFirstToken();
	boolean isThisReference = (firstToken.kind == THIS);

	if (isThisReference) {
	    name = ToString.token(THIS) + "." + name;
	} 

	CommunicationExpression communicationExpression = 
	    new CommunicationExpression();

	communicationExpression.setUnreflectedName(name);
	communicationExpression.setFileBounds(vinfo.traceAssertionClass, 
					      node.getFirstToken().beginLine);
	communicationExpression.setStringRepresentation(ToString.node(node));

	CommunicationParameter[] parameters = 
	    (CommunicationParameter[]) nodeParameterList.jjtAccept(this, vi);

	communicationExpression.setParameters(parameters);

	Token tokenAfterParameterList = nodeParameterList.getLastToken().next;

	setCommunicationType(communicationExpression, name, 
			     tokenAfterParameterList, vinfo);

	return communicationExpression;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassCommunicationParameterList node, Object vi) {

	// CommunicationParameterList ::=
	// <LPAREN> ( CommunicationParameter ( <COMMA> CommunicationParameter )* )? <RPAREN>
	CommunicationParameter[] parameters;
	int			 numberOfParameters = 
	    node.jjtGetNumChildren();

	parameters = new CommunicationParameter[numberOfParameters];

	for (int iParameter = 0; iParameter < numberOfParameters; 
		++iParameter) {
	    Node		   nodeParameter = 
		node.jjtGetChild(iParameter);
	    CommunicationParameter parameter = 
		(CommunicationParameter) nodeParameter.jjtAccept(this, vi);

	    parameters[iParameter] = parameter;
	} 

	return parameters;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassCommunicationParameter node, Object vi) {

	// CommunicationParameter ::=
	// (
	// LayedDownParameter | TypedParameter | InputParameter
	// | OutputParameter | "*"
	// )
	// (
	// LayedDownParameter | TypedParameter | OutputParameter
	// | InputParameter | <STAR> | <HOOK>
	// )
	CommunicationParameter parameter;
	boolean		       isWildcard = (node.jjtGetNumChildren() == 0);

	if (isWildcard) {
	    int wildcardType = node.getFirstToken().kind;
	    int communicationType = -1;

	    switch (wildcardType) {

	    case STAR: {
		communicationType = 
		    CommunicationParameter.UNREFLECTEDTYPE_ARBITRARY;

		break;
	    } 

	    case HOOK: {
		communicationType = 
		    CommunicationParameter.UNREFLECTEDTYPE_ONE_ARBITRARY;
	    } 
	    }

	    parameter = new WildcardParameter(communicationType);
	} else {
	    Node nodeParameter = node.getFirstChild();

	    parameter = (CommunicationParameter) nodeParameter.jjtAccept(this, 
		    vi);
	} 

	return parameter;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassLayedDownParameter node, Object vi) {

	// LayedDownParameter ::=
	// ( <FLOATING_POINT_LITERAL> | <INTEGER_LITERAL> | <NULL> | <STRING_LITERAL> )
	Token parameterToken = node.getFirstToken();
	int   parameterType = -1;

	switch (parameterToken.kind) {

	case FLOATING_POINT_LITERAL: {
	    parameterType = CommunicationParameter.UNREFLECTEDTYPE_FLOATING;

	    break;
	} 

	case INTEGER_LITERAL: {
	    parameterType = CommunicationParameter.UNREFLECTEDTYPE_INTEGER;

	    break;
	} 

	case NULL: {
	    parameterType = CommunicationParameter.UNREFLECTEDTYPE_NULL;

	    break;
	} 

	case CHARACTER_LITERAL: {
	    parameterType = CommunicationParameter.UNREFLECTEDTYPE_CHAR;

	    break;
	} 

	case STRING_LITERAL: {
	    parameterType = CommunicationParameter.UNREFLECTEDTYPE_STRING;

	    break;
	} 
	}

	LayedDownParameter parameter = new LayedDownParameter(parameterType);

	parameter.setValue(parameterToken.image);

	return parameter;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassTypedParameter node, Object vi) {

	// TypedParameter ::=  Type ( <IDENTIFIER> )?
	VInfo	       vinfo = (VInfo) vi;
	Node	       nodeType = node.getFirstChild();
	Class	       type = (Class) nodeType.jjtAccept(this, vi);
	TypedParameter parameter = new TypedParameter(type);

	return parameter;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassInputParameter node, Object vi) {

	// InputParameter ::= <HOOK> <IDENTIFIER>
	VInfo		     vinfo = (VInfo) vi;
	Token		     tokenIdentifier = node.getFirstToken().next;
	SimpleNameExpression nameExpression = 
	    new SimpleNameExpression(tokenIdentifier.image, 
				     tokenIdentifier.beginLine, vinfo.clazz);
	ExchangeParameter    parameter = 
	    new ExchangeParameter(CommunicationParameter.UNREFLECTEDTYPE_INPUT, 
				  nameExpression);

	return parameter;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassOutputParameter node, Object vi) {

	// OutputParameter ::= <BANG> <IDENTIFIER>
	VInfo		     vinfo = (VInfo) vi;
	Token		     tokenIdentifier = node.getFirstToken().next;
	SimpleNameExpression nameExpression = 
	    new SimpleNameExpression(tokenIdentifier.image, 
				     tokenIdentifier.beginLine, vinfo.clazz);
	ExchangeParameter    parameter = 
	    new ExchangeParameter(CommunicationParameter.UNREFLECTEDTYPE_OUTPUT, 
				  nameExpression);

	return parameter;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassCommunicationWithWildcards node, Object vi) {

	// CommunicationWithWildcards ::=
	// (
	// <STAR> "." <IDENTIFIER> CommunicationParameterList ( "." <IDENTIFIER> )?
	// | <STAR>
	// | ( <PRIVATE> | <PACKAGE> | <PROTECTED> | <PUBLIC> )? ( <THIS> | Name ) "." <STAR>
	// )
	VInfo				vinfo = (VInfo) vi;
	Token				firstToken = node.getFirstToken();
	Token				actualToken = firstToken;
	int				childNr = 0;
	CommunicationWildcardExpression wildcardExpression = 
	    new CommunicationWildcardExpression();

	wildcardExpression.setFileBounds(vinfo.traceAssertionClass, 
					 node.getFirstToken().beginLine);
	wildcardExpression.setStringRepresentation(ToString.node(node));

	String name = "";

	if (actualToken.kind == STAR) {
	    if (actualToken.next.kind == DOT) {

		// arbitrary class
		// overread '*.'
		name += actualToken.image;
		actualToken = actualToken.next;
		name += actualToken.image;
		actualToken = actualToken.next;

		// method identifier
		name += actualToken.image;
		actualToken = actualToken.next;

		CommunicationParameter[] parameters;
		Node			 nodeParameters = 
		    node.jjtGetChild(childNr++);

		parameters = 
		    (CommunicationParameter[]) nodeParameters.jjtAccept(this, 
			vi);

		wildcardExpression.setParameters(parameters);
		wildcardExpression.setToArbitraryClass();

		actualToken = nodeParameters.getLastToken().next;
	    } else {

		// arbitrary class & method
		name = actualToken.image;
		actualToken = actualToken.next;

		wildcardExpression.setToArbitraryClass();
		wildcardExpression.setToArbitraryMethod();
	    } 
	} else {

	    // arbitary method
	    switch (actualToken.kind) {

	    case PRIVATE:

	    case PACKAGE:

	    case PROTECTED:

	    case PUBLIC: {
		wildcardExpression.setMethodLimitation(actualToken.kind);

		actualToken = actualToken.next;

		break;
	    } 
	    }

	    if (actualToken.kind == THIS) {
		name += actualToken.image;
		actualToken = actualToken.next;
	    } else {
		Node nodeName = node.jjtGetChild(childNr++);

		name += (String) nodeName.jjtAccept(this, vi);
		actualToken = nodeName.getLastToken().next;
	    } 

	    // overread '.*'
	    name += actualToken;
	    actualToken = actualToken.next;
	    name += actualToken;
	    actualToken = actualToken.next;

	    wildcardExpression.setToArbitraryMethod();

	    // set communication type
	    setCommunicationType(wildcardExpression, name, actualToken, 
				 vinfo);
	} 

	wildcardExpression.setUnreflectedName(name);

	return wildcardExpression;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassTraceConstant node, Object vi) {

	// TraceConstant ::= FieldDeclaration
	// <FINAL> ( PrimitiveType | <IDENTIFIER> )
	// VariableDeclarator ( "," VariableDeclarator )* <SEMICOLON>
	VInfo vinfo = (VInfo) vi;

	node.getFirstChild().jjtAccept(this, vi);

	Enumeration fieldEnumeration = vinfo.fields.elements();

	while (fieldEnumeration.hasMoreElements()) {
	    Field field;

	    field = (Field) fieldEnumeration.nextElement();

	    Modifier modifier = new Modifier(field.getModifier());

	    if (modifier.getModifier() != Modifier.FINAL) {
		throw new ReflectionError("Trace Assertion Constant '" 
					  + field.getName() 
					  + "' must be declared FINAL and must not have any other modifiers");
	    } 

	    modifier.setStatic();
	    field.setModifier(modifier.getModifier());

	    if (field.getInitializerString() == null) {
		throw new ReflectionError("Trace Assertion Constant '" 
					  + field.getName() 
					  + "' must be initialized");
	    } 
	} 

	return null;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassProcessDeclaration node, Object vi) {

	// ProcessDeclaration ::=
	// (
	// ( ProcessDeclarator <LBRACE> ( FieldDeclaration )* ProcessExpression <RBRACE> )
	// | ( FieldDeclaration )* ProcessExpression
	// )
	VInfo		      vinfo = (VInfo) vi;
	int		      childNr = 0;
	Node		      child = node.jjtGetChild(childNr);
	JassProcessDeclarator nodeDeclarator = null;
	ProcessDeclarator     declarator;
	boolean		      hasDeclarator = 
	    child.hasId(JJTPROCESSDECLARATOR);

	if (hasDeclarator) {
	    nodeDeclarator = (JassProcessDeclarator) child;

	    String name = node.getFirstToken().image;

	    declarator = new ProcessDeclarator(name);
	    child = node.jjtGetChild(++childNr);
	} else {
	    declarator = new ProcessDeclarator(Process.MAIN);
	} 

	declarator.setFileBounds(vinfo.traceAssertionClass, 
				 node.getFirstToken().beginLine);

	Process     process = 
	    new Process(vinfo.traceAssertionExpression, declarator, 
			Class.getIdentifier(vinfo.unitName));

	// A process of a trace assertion is mapped to a java class
	// begin faking class
	ClassFake   classFake = new ClassFake(vinfo);
	VInfo       vinfoFake = classFake.getVinfo();
	Class       mappedClass = classFake.begin(process);

	// reflect formal parameters of declarator
	// ProcessDeclarator ::= <IDENTIFIER> FormalParameters
	// a process declarator is mapped to a java constructor
	Constructor mappedConstructor = declarator.getMappedConstructor();

	mappedConstructor.setContainer(vinfoFake.clazz);

	// begin faking method
	MethodFake methodFake = new MethodFake(vinfoFake);

	methodFake.begin(mappedConstructor);
	mappedConstructor.setDeclaringClass(vinfoFake.clazz);
	mappedConstructor.setType(vinfoFake.clazz);
	mappedConstructor.setModifier(Modifier.PUBLIC);

	// set formal parameters
	if (hasDeclarator) {
	    nodeDeclarator.jjtAccept(this, vinfoFake);
	} 

	// end faking method
	methodFake.end();

	// class will be reflected now ...
	mappedClass.setUnreflected(false);

	if (topLevelClass == null) {
	    topLevelClass = mappedClass;

	    // set fields of class explicitly
	} 

	while (child.hasId(JJTFIELDDECLARATION)) {
	    JassFieldDeclaration nodeVariableDeclaration = 
		(JassFieldDeclaration) child;

	    nodeVariableDeclaration.jjtAccept(this, vinfoFake);

	    child = node.jjtGetChild(++childNr);
	} 

	Vector  fields = new Vector();
	Field[] traceAssertionConstants = 
	    vinfoFake.traceAssertionExpression.getConstants();

	for (int iConstant = 0; iConstant < traceAssertionConstants.length; 
		++iConstant) {
	    Field	       constant = traceAssertionConstants[iConstant];
	    VariableDeclarator variable = new VariableDeclarator(constant.getName(), 
		    " " 
		    + vinfoFake.traceAssertionExpression.getMappedClassname() 
		    + "." + constant.getName());
	    Field	       field = new Field(variable);

	    field.setType(constant.getType());
	    field.setModifier(constant.getModifier());
	    fields.add(field);
	} 

	fields.addAll(vinfoFake.fields);
	mappedClass.setDeclaredFields(fields);

	vinfoFake.process = process;

	JassProcessExpression nodeExpression = (JassProcessExpression) child;
	ProcessExpression     expression = 
	    (ProcessExpression) nodeExpression.jjtAccept(this, vinfoFake);

	// end faking class. This sets also reflected values of class
	classFake.end();
	process.setExpression(expression);

	return process;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassProcessExpression node, Object vi) {

	// ProcessExpression ::= ProcessParallelExpression
	VInfo		  vinfo = (VInfo) vi;
	Node		  firstNonterminal = node.getFirstChild();
	ProcessExpression processExpression = 
	    (ProcessExpression) firstNonterminal.jjtAccept(this, vi);

	processExpression.setFileBounds(vinfo.traceAssertionClass, 
					firstNonterminal.getFirstToken().beginLine);

	return processExpression;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassProcessDeclarator node, Object vi) {

	// ProcessDeclarator ::= <IDENTIFIER> FormalParameters
	// <IDENTIFIER> was allready reflected by visitor of ProcessDeclaration
	VInfo vinfo = (VInfo) vi;

	node.getFirstChild().jjtAccept(this, vi);

	FormalParameter[] parameters = vinfo.method.getFormalParameters();

	for (int iParameter = 0; iParameter < parameters.length; 
		++iParameter) {
	    FormalParameter parameter = parameters[iParameter];
	    String	    parametername = parameter.getName();
	    Class	    parametertype = getClass(parametername, 
						     parameter.getType());
	    Field	    field = 
		new jass.reflect.Field(new VariableDeclarator(parametername, 
		    null));

	    field.setType(parametertype);
	    vinfo.fields.addElement(field);
	} 

	return null;
    } 

    /*
     * public Object visit(JassProcessDeclarator node, Object vi)
     * {
     * // ProcessDeclarator ::= <IDENTIFIER> FormalParameters
     * VInfo vinfo = (VInfo) vi;
     * 
     * String name = node.getFirstToken().image;
     * ProcessDeclarator declarator = new ProcessDeclarator(name);
     * declarator.setFileBounds(vinfo.clazz, node.getFirstToken().beginLine);
     * 
     * // a process declarator is mapped to a java constructor
     * Constructor mappedConstructor = declarator.getMappedConstructor();
     * 
     * // begin faking method
     * MethodFake fake = new MethodFake(vinfo);
     * fake.begin(mappedConstructor);
     * mappedConstructor.setDeclaringClass(vinfo.clazz);
     * mappedConstructor.setType(vinfo.clazz);
     * mappedConstructor.setModifier(Modifier.PUBLIC);
     * // set formal parameters
     * node.childrenAccept(this, vi);
     * // end faking method
     * fake.end();
     * 
     * return declarator;
     * }
     */

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassProcessParallelExpression node, Object vi) {

	// ProcessParallelExpression ::=
	// ProcessChoiceExpression ( <TRACE_PARALLEL_OPERATOR> ProcessChoiceExpression )*
	VInfo		 vinfo = (VInfo) vi;
	int		 numberOfExpressions = node.jjtGetNumChildren();
	ReferedProcess[] referedProcesses = 
	    new ReferedProcess[numberOfExpressions];

	for (int iExpression = 0; iExpression < numberOfExpressions; 
		++iExpression) {
	    Node	      nodeExpression = node.jjtGetChild(iExpression);

	    // create process declarator
	    String	      processname = vinfo.process.getName() + "_" 
					    + iExpression;
	    ProcessDeclarator declarator = new ProcessDeclarator(processname);

	    declarator.setFileBounds(vinfo.traceAssertionClass, 
				     nodeExpression.getFirstToken().beginLine);

	    Process     process = 
		new Process(vinfo.traceAssertionExpression, declarator, 
			    Class.getIdentifier(vinfo.unitName));

	    // begin faking class
	    ClassFake   classFake = new ClassFake(vinfo);
	    VInfo       vinfoFake = classFake.getVinfo();
	    Class       mappedClass = classFake.begin(process);

	    // reflect formal parameters of declarator
	    // ProcessDeclarator ::= <IDENTIFIER> FormalParameters
	    // a process declarator is mapped to a java constructor
	    Constructor mappedConstructor = declarator.getMappedConstructor();

	    mappedConstructor.setContainer(vinfoFake.clazz);
	    mappedConstructor.setDeclaringClass(vinfoFake.clazz);
	    mappedConstructor.setType(vinfoFake.clazz);
	    mappedConstructor.setModifier(Modifier.PUBLIC);

	    // class will be reflected now ...
	    mappedClass.setUnreflected(false);

	    if (topLevelClass == null) {
		topLevelClass = mappedClass;
	    } 

	    ProcessExpression expression = 
		(ProcessExpression) nodeExpression.jjtAccept(this, vinfoFake);

	    expression.setFileBounds(vinfoFake.traceAssertionClass, 
				     nodeExpression.getFirstToken().beginLine);
	    process.setExpression(expression);

	    // end faking class. This sets also reflected values of class
	    classFake.end();
	    vinfo.traceAssertionExpression.add(process);

	    // parallelism activates several processes simultaneously
	    referedProcesses[iExpression] = new ReferedProcess(processname, 
		    mappedConstructor.getFormalParameters());
	} 

	ProcessCall processCall = new ProcessCall(referedProcesses);

	processCall.setStringRepresentation(ToString.node(node));

	return processCall;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassProcessChoiceExpression node, Object vi) {

	// ProcessChoiceExpression ::=
	// ProcessPrefixExpression ( <TRACE_CHOICE_OPERATOR> ProcessPrefixExpression )*
	VInfo			vinfo = (VInfo) vi;
	ProcessChoiceExpression choiceExpression = 
	    new ProcessChoiceExpression();

	choiceExpression.setStringRepresentation(ToString.node(node));

	int numberOfExpressions = node.jjtGetNumChildren();

	for (int iExpression = 0; iExpression < numberOfExpressions; 
		++iExpression) {
	    Node	      nodeExpression = node.jjtGetChild(iExpression);
	    ProcessExpression expression = 
		(ProcessExpression) nodeExpression.jjtAccept(this, vi);

	    expression.setFileBounds(vinfo.traceAssertionClass, 
				     nodeExpression.getFirstToken().beginLine);
	    choiceExpression.add(expression);
	} 

	return choiceExpression;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassProcessPrefixExpression node, Object vi) {

	// ProcessPrefixExpression ::=
	// ( ProcessPrimaryExpression ) ( <TRACE_PREFIX_OPERATOR> ( ProcessPrimaryExpression ) )*
	VInfo			vinfo = (VInfo) vi;
	ProcessPrefixExpression prefixExpression = 
	    new ProcessPrefixExpression();

	prefixExpression.setStringRepresentation(ToString.node(node));

	int numberOfExpressions = node.jjtGetNumChildren();

	for (int iExpression = 0; iExpression < numberOfExpressions; 
		++iExpression) {
	    Node	      nodeExpression = node.jjtGetChild(iExpression);
	    ProcessExpression expression = 
		(ProcessExpression) nodeExpression.jjtAccept(this, vi);

	    expression.setFileBounds(vinfo.traceAssertionClass, 
				     nodeExpression.getFirstToken().beginLine);
	    prefixExpression.add(expression);
	} 

	return prefixExpression;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassProcessPrimaryExpression node, Object vi) {

	// ProcessPrimaryExpression ::=
	// ( ConditionalCommunication | CheckVerification | ProcessCall
	// | ProcessJavaBlock | BasicProcess | ProcessIfElseExpression |
	// ( <LPAREN> ProcessExpression <RPAREN> ) )
	ProcessExpression expression = 
	    (ProcessExpression) node.getFirstChild().jjtAccept(this, vi);

	return expression;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassConditionalCommunication node, Object vi) {

	// ConditionalCommunication ::=
	// ( InputParameter | OutputParameter )? CommunicationExpressions
	// ( <TRACE_CONDITION> <LPAREN> AssertionExpression <RPAREN> )?
	VInfo		  vinfo = (VInfo) vi;
	int		  childNr = 0;
	Node		  actualNode = node.jjtGetChild(childNr);
	ExchangeParameter parameter = null;
	boolean		  hasParameter = 
	    actualNode.hasId(JJTINPUTPARAMETER) 
	    || actualNode.hasId(JJTOUTPUTPARAMETER);

	if (hasParameter) {
	    parameter = (ExchangeParameter) actualNode.jjtAccept(this, vi);
	    ++childNr;
	} 

	actualNode = node.jjtGetChild(childNr++);

	CommunicationExpressions communicationExpressions = 
	    (CommunicationExpressions) actualNode.jjtAccept(this, vi);
	ConditionalCommunication conditionalCommunication = 
	    new ConditionalCommunication(communicationExpressions);

	conditionalCommunication.setStringRepresentation(ToString.node(node));
	conditionalCommunication.setReturnType(parameter);

	boolean hasCondition = (childNr < node.jjtGetNumChildren());

	if (hasCondition) {
	    actualNode = node.jjtGetChild(childNr++);

	    Expression expression = 
		(Expression) actualNode.jjtAccept(this, vi);

	    conditionalCommunication.setCondition(expression, actualNode);
	} 

	return conditionalCommunication;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassProcessCall node, Object vi) {

	// ProcessCall ::=
	// <TRACE_CALL_OPERATOR> <IDENTIFIER> Arguments
	String		   name = node.getFirstToken().next.image;
	ArgumentExpression arguments = 
	    (ArgumentExpression) node.getFirstChild().jjtAccept(this, vi);
	ProcessCall	   processCall = 
	    new ProcessCall(new ReferedProcess(name, arguments));

	processCall.setStringRepresentation(ToString.node(node));

	return processCall;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassProcessJavaBlock node, Object vi) {

	// ProcessJavaBlock ::=
	// <TRACE_EXECUTE_OPERATOR> <LPAREN> BlockStatements <RPAREN>
	VInfo		 vinfo = (VInfo) vi;
	Node		 nodeStatements = node.getFirstChild();
	Token		 firstToken = node.getFirstToken();
	ProcessJavaBlock javaBlock = new ProcessJavaBlock(nodeStatements, 
		firstToken.beginLine, firstToken.beginColumn);

	// A java block in a trace assertion is mapped to a java method
	Method		 mappedMethod = javaBlock.getMappedMethod();

	// begin faking method
	MethodFake       fake = new MethodFake(vinfo);

	fake.begin(mappedMethod);
	mappedMethod.setDeclaringClass(vinfo.clazz);

	// set method properties to archive a method of kind
	// public void <methodname>()
	mappedMethod.setModifier(Modifier.PUBLIC);
	mappedMethod.setReturnType(ClassPool.getClass("void"));
	mappedMethod.setFormalParameters(new Vector());

	// no exceptions, m.setExceptionTypes(Vector);
	// Visit block statements
	nodeStatements.jjtAccept(this, vi);
	vinfo.methods.addElement(mappedMethod);

	// end faking method
	fake.end();

	return javaBlock;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassBasicProcess node, Object vi) {

	// BasicProcess ::=
	// ( <TRACE_DEADLOCK_OPERATOR> | <TRACE_CHAOS_OPERATOR> | <TRACE_TERMINATION_OPERATOR> )
	int		  operatorType = node.getFirstToken().kind;
	ProcessExpression basicProcess = null;

	switch (operatorType) {

	case TRACE_DEADLOCK_OPERATOR: {
	    basicProcess = new BasicProcessDeadlock();

	    break;
	} 

	case TRACE_CHAOS_OPERATOR: {
	    basicProcess = new BasicProcessChaos();

	    break;
	} 

	case TRACE_TERMINATION_OPERATOR: {
	    basicProcess = new BasicProcessTermination();

	    break;
	} 
	}

	basicProcess.setStringRepresentation(ToString.node(node));

	return basicProcess;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassProcessIfElseExpression node, Object vi) {

	// ProcessIfElseExpression ::=
	// <TRACE_IF_OPERATOR> <LPAREN> Expression <RPAREN> <LBRACE> ProcessExpression <RBRACE>
	// <TRACE_ELSE_OPERATOR> <LBRACE> ProcessExpression <RBRACE>
	VInfo		  vinfo = (VInfo) vi;
	Expression	  condition = 
	    (Expression) node.jjtGetChild(0).jjtAccept(this, vi);
	Node		  nodeIf = node.jjtGetChild(1);
	Node		  nodeElse = node.jjtGetChild(2);
	ProcessExpression ifExpression = 
	    (ProcessExpression) nodeIf.jjtAccept(this, vi);
	ProcessExpression elseExpression = 
	    (ProcessExpression) nodeElse.jjtAccept(this, vi);

	ifExpression.setFileBounds(vinfo.traceAssertionClass, 
				   nodeIf.getFirstToken().beginLine);
	elseExpression.setFileBounds(vinfo.traceAssertionClass, 
				     nodeElse.getFirstToken().beginLine);

	ProcessIfElseExpression ifElseExpression = 
	    new ProcessIfElseExpression(ifExpression, elseExpression);

	ifElseExpression.setStringRepresentation(ToString.node(node));
	ifElseExpression.setCondition(condition);

	return ifElseExpression;
    } 

    /*
     * *******************
     * EXPRESSIONS parsing
     * ******************
     */

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassLiteral node, Object vi) {
	Token   first = node.getFirstToken();
	Literal l = new Literal(first.image);

	l.setLine(node.getFirstToken().beginLine);
	l.setContainer(((VInfo) vi).clazz);

	if (l.getImage().equals("null")) {
	    l.setType(ClassPool.Null);

	    return l;
	} 

	if (l.getImage().equals("true") || l.getImage().equals("false")) {
	    l.setType(ClassPool.Boolean);

	    return l;
	} 

	switch (first.kind) {

	case JassParserConstants.INTEGER_LITERAL:
	    if (first.image.endsWith("l") || first.image.endsWith("L")) {
		l.setType(ClassPool.Long);
	    } else {
		l.setType(ClassPool.Int);
	    }

	    return l;

	case JassParserConstants.FLOATING_POINT_LITERAL:
	    if (first.image.endsWith("d") || first.image.endsWith("D")) {
		l.setType(ClassPool.Double);
	    } else {
		l.setType(ClassPool.Float);
	    }

	    return l;

	case JassParserConstants.CHARACTER_LITERAL:
	    l.setType(ClassPool.Char);

	    return l;

	case JassParserConstants.STRING_LITERAL:
	    l.setType(ClassPool.getClass("java.lang.String"));

	    return l;
	}

	return l;
    } 

    /**
     * Reflects a JassExpression node into a jass.reflect.Expression.
     *
     * @param node The JassExpression node.
     * @param vi ignored
     *
     * @return the Expression that corresponds to the JassExpression.
     *
     */
    public Object visit(JassExpression node, Object vi) {
	/* 
	 * Expression ::= ConditionalExpression
	 *        [ AssignmentOperator Expression ]
	 */

	/* In most cases it is just a ConditionalExpression */
	Expression expr =
	    (Expression) node.jjtGetChild(0).jjtAccept(this, vi);
	
	if (node.jjtGetNumChildren() == 3) {
	    /* This is an assignment expression, child 0 is lvalue,
	     * child 1 is assignment operator, child 2 is rvalue
	     */
	    String operator = node.jjtGetChild(1).getFirstToken().image;
	    Expression rvalue = (Expression) 
		node.jjtGetChild(2).jjtAccept(this, vi);
	    expr = new AssignmentExpression(expr, operator, rvalue);
	}

	return expr;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassConditionalExpression node, Object vi) {
	Expression	  c = (Expression) node.jjtGetChild(0).jjtAccept(this, 
		vi);
	Expression	  f = (Expression) node.jjtGetChild(1).jjtAccept(this, 
		vi);
	Expression	  s = (Expression) node.jjtGetChild(2).jjtAccept(this, 
		vi);
	TernaerExpression te = new TernaerExpression(c, f, s);

	te.setContainer(((VInfo) vi).clazz);
	te.setLine(node.getFirstToken().beginLine);

	return te;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassConditionalOrExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.CONDITIONAL_OR, 
				      vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassConditionalAndExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.CONDITIONAL_AND, 
				      vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassInclusiveOrExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.INCLUSIVE_OR, 
				      vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassExclusiveOrExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.EXCLUSIVE_OR, 
				      vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAndExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.AND, vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassEqualityExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.EQUALITY, vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassInstanceOfExpression node, Object vi) {
	return new InstanceOfExpression((Expression) node.jjtGetChild(0).jjtAccept(this, vi), 
					(Class) node.jjtGetChild(1).jjtAccept(this, 
					vi));
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassRelationalExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.RELATIONAL, vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassShiftExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.SHIFT, vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAdditiveExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.ADDITIVE, vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassMultiplicativeExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.MULTIPLICATIVE, 
				      vi);
    } 

    // no difference between UnaryPlusMins and UnaryNOTPlusMinus !

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassUnaryExpression node, Object vi) {
	return new UnaryExpression((Expression) node.jjtGetChild(1).jjtAccept(this, vi), 
				   node.getFirstToken().image);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassUnaryExpressionNotPlusMinus node, Object vi) {
	return new UnaryExpression((Expression) node.jjtGetChild(1).jjtAccept(this, vi), 
				   node.getFirstToken().image);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassPreIncrementExpression node, Object vi) {
	return new UnaryAdditiveExpression((Expression) node.jjtGetChild(0).jjtAccept(this, vi), 
					   true, node.getFirstToken().image);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassPreDecrementExpression node, Object vi) {
	return new UnaryAdditiveExpression((Expression) node.jjtGetChild(0).jjtAccept(this, vi), 
					   true, node.getFirstToken().image);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassPostfixExpression node, Object vi) {
	return new UnaryAdditiveExpression((Expression) node.jjtGetChild(0).jjtAccept(this, vi), 
					   false, node.getLastToken().image);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassCastExpression node, Object vi) {

	// visit Type
	CastExpression cast = 
	    new CastExpression((Class) node.jjtGetChild(0).jjtAccept(this, 
		vi));

	// visit (Unary)Expression
	Expression[]   e = new Expression[1];

	e[0] = (Expression) node.jjtGetChild(1).jjtAccept(this, vi);

	cast.setChildren(e);

	return cast;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAllocationExpression node, Object vi) {

	// first child is the type (primitive or name)
	// next is an argument expression or array dims and inits
	// perhaps there is a classbody, but this is NOT parsed (anonyme class) !
	Class cl;

	if (node.jjtGetChild(0).hasId(JJTPRIMITIVETYPE)) {
	    cl = 
		ClassPool.getClass(node.jjtGetChild(0).getFirstToken().image);
	} else {
	    String s = (String) node.jjtGetChild(0).jjtAccept(this, vi);
	    String type_name = NameAnalysis.expandTypeName(s, 
		    ((VInfo) vi).clazz);

	    if (type_name == null) {
		throw new ReflectionError(((VInfo) vi).unitName + ":" 
					  + node.getFirstToken().beginLine 
					  + " <Can not expand " + s 
					  + " to full qualified type name.>");
	    } 

	    cl = ClassPool.getClass(type_name);

	    if (cl == null) {
		throw new ReflectionError(((VInfo) vi).unitName + ":" 
					  + node.getFirstToken().beginLine 
					  + " <" + s 
					  + " is not a proper type.>");
	    } 
	} 

	// rside is ArrayDimsAndInit or ArgumentsExpression !
	AllocationExpression ae = new AllocationExpression(cl, 
		(Expression) node.jjtGetChild(1).jjtAccept(this, vi));

	ae.setContainer(((VInfo) vi).clazz);
	ae.setLine(node.getFirstToken().beginLine);

	return ae;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassArrayDimsAndInits node, Object vi) {

	// no initializer are parsed !
	Vector     v = new Vector();
	Expression e = new Expression();

	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    if (node.jjtGetChild(i).hasId(JJTEXPRESSION)) {
		v.addElement(node.jjtGetChild(i).jjtAccept(this, vi));
	    } 
	} 

	e.setChildren(v);

	return e;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassPrimaryExpression node, Object vi) {
	Expression e = (Expression) handlePrimaryExpression(node, vi);

	return e;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassPrimaryPrefix node, Object vi) {
	if (node.jjtGetNumChildren() > 0) {

	    // Nested Expression ?
	    if (node.getFirstToken().image.equals("(")) {

		// NestedEXpressions need no line and container
		NestedExpression ne = 
		    new NestedExpression((Expression) node.jjtGetChild(0).jjtAccept(this, 
			vi));

		return ne;
	    } 

	    // Literal ?
	    if (node.jjtGetChild(0).hasId(JJTLITERAL)) {
		Literal l = (Literal) node.jjtGetChild(0).jjtAccept(this, vi);

		// Literals need not line and container
		return l;
	    } 

	    // Reference ?
	    if (node.jjtGetChild(0).hasId(JJTNAME)) {
		String refer = (String) node.jjtGetChild(0).jjtAccept(this, 
							 vi);

		if (refer.indexOf('.') > -1) {
		    NameExpression re = new NameExpression();

		    re.setContainer(((VInfo) vi).clazz);
		    re.setLine(node.getFirstToken().beginLine);
		    re.addChildren(refer, 
				   new Set(((VInfo) vi).locals.toArray()));

		    return re;
		} 

		SimpleNameExpression sne = new SimpleNameExpression(refer);

		sne.setLocals(new Set(((VInfo) vi).locals.toArray()));
		sne.setLine(node.getFirstToken().beginLine);
		sne.setContainer(((VInfo) vi).clazz);

		return sne;
	    } 

	    if (node.jjtGetChild(0).hasId(JJTALLOCATIONEXPRESSION)) {
		return (AllocationExpression) node.jjtGetChild(0).jjtAccept(this, 
			vi);
	    } 
	} 

	// THIS reference ?
	if (node.getFirstToken().image.equals("this")) {
	    SimpleNameExpression re = new SimpleNameExpression("this");

	    re.setLine(node.getFirstToken().beginLine);
	    re.setContainer(((VInfo) vi).clazz);

	    // this can not be the name of a local variable, but other method will use them
	    re.setLocals(new Set(((VInfo) vi).locals.toArray()));

	    return re;
	} 

	// Must be super.<IDENTIFIER>
	NameExpression re = new NameExpression();

	re.setContainer(((VInfo) vi).clazz);
	re.setLine(node.getFirstToken().beginLine);
	re.addChildren("super." + node.getLastToken().image, null);

	return re;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassPrimarySuffix node, Object vi) {

	// ArrayDimReferenceExpression and ArgumentExpression is only needed for a short time,
	// they will be splitted and inserted in other expressions (methodcall, arrayaccess)
	if (node.jjtGetNumChildren() > 0) {

	    // ArrayBrackets ?
	    if (node.getFirstToken().image.equals("[")) {
		return new ArrayDimReferenceExpression((Expression) node.jjtGetChild(0).jjtAccept(this, 
			vi));
	    } 

	    // Arguments ?
	    if (node.getFirstToken().image.equals("(")) {
		return (ArgumentExpression) node.jjtGetChild(0).jjtAccept(this, 
			vi);
	    } 
	} 

	// Must be reference
	SimpleNameExpression sne = 
	    new SimpleNameExpression(node.getLastToken().image);

	sne.setLocals(new Set(((VInfo) vi).locals.toArray()));
	sne.setLine(node.getFirstToken().beginLine);
	sne.setContainer(((VInfo) vi).clazz);

	return sne;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassArguments node, Object vi) {

	// empty list ?
	if (node.jjtGetNumChildren() > 0) {
	    return (ArgumentExpression) node.jjtGetChild(0).jjtAccept(this, 
		    vi);
	} 

	ArgumentExpression empty = new ArgumentExpression();

	/*
	 * empty.setLine(node.getFirstToken().beginLine);
	 * empty.setContainer(((VInfo)vi).clazz);
	 */
	return empty;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassArgumentList node, Object vi) {
	Expression[]       ex = new Expression[node.jjtGetNumChildren()];
	ArgumentExpression ae = new ArgumentExpression();

	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    ex[i] = (Expression) node.jjtGetChild(i).jjtAccept(this, vi);
	} 

	ae.setChildren(ex);

	/*
	 * ae.setLine(node.getFirstToken().beginLine);
	 * ae.setContainer(c);
	 */
	return ae;
    } 

    /*
     * *********************
     * ASSERTION EXPRESSIONS
     * ********************
     */

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionForAllExistsExpression node, Object vi) {

	// AssertionForAllExistsExpression ::=
	// ( <FORALL> | <EXISTS> ) <IDENTIFIER> ":"
	// ( AssertionFiniteEnumerationCreation | AssertionRangeExpression ) AssertionExpression
	int		 forallnr = ((VInfo) vi).forallCounter++;

	// two possibilities
	// 1.) AllocationExpression
	// 2.) RangeExpression
	String		 id = node.getFirstToken().next.image;
	Expression       args = 
	    (Expression) node.jjtGetChild(0).jjtAccept(this, vi);
	Expression       child = 
	    (Expression) node.jjtGetChild(1).jjtAccept(this, vi);
	ForAllExpression fae = new ForAllExpression(id, args, child, 
						    forallnr, 
						    node.getFirstToken().kind 
						    == FORALL);

	fae.setLine(node.getFirstToken().beginLine);
	fae.setContainer(((VInfo) vi).clazz);
	((VInfo) vi).forall_exists.addElement(fae);

	return fae;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionRangeExpression node, Object vi) {

	/* AssertionRangeExpression ::= "{" AssertionShiftExpression <RANGE> AssertionShiftExpression "}" */
	Expression      bot = (Expression) node.jjtGetChild(0).jjtAccept(this, 
		vi);
	Expression      top = (Expression) node.jjtGetChild(1).jjtAccept(this, 
		vi);
	RangeExpression range = new RangeExpression(bot, top);

	range.setContainer(((VInfo) vi).clazz);
	range.setLine(node.jjtGetChild(0).getFirstToken().beginLine);

	return range;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionFiniteEnumerationCreation node, 
			Object vi) {

	/* AssertionFiniteEnumerationCreation ::= "new" Name AssertionArguments */
	Class  cl;
	String s = (String) node.jjtGetChild(0).jjtAccept(this, vi);
	String type_name = NameAnalysis.expandTypeName(s, ((VInfo) vi).clazz);

	if (type_name == null) {
	    throw new ReflectionError(((VInfo) vi).unitName + ":" 
				      + node.getFirstToken().beginLine 
				      + " <Can not expand " + s 
				      + " to full qualified type name.>");
	} 

	cl = ClassPool.getClass(type_name);

	if (cl == null) {
	    throw new ReflectionError(((VInfo) vi).unitName + ":" 
				      + node.getFirstToken().beginLine + " <" 
				      + s + " is not a proper type.>");
	} 

	AllocationExpression ae = new AllocationExpression(cl, 
		(Expression) node.jjtGetChild(1).jjtAccept(this, vi));

	ae.setContainer(((VInfo) vi).clazz);
	ae.setLine(node.getFirstToken().beginLine);

	return ae;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionConditionalExpression node, Object vi) {

	// AssertionConditionalExpression ::=
	// AssertionConditionalOrExpression ( "?" AssertionExpression ":" AssertionExpression )?
	Expression	  cond = 
	    (Expression) node.jjtGetChild(0).jjtAccept(this, vi);
	Expression	  f = (Expression) node.jjtGetChild(1).jjtAccept(this, 
		vi);
	Expression	  s = (Expression) node.jjtGetChild(2).jjtAccept(this, 
		vi);
	TernaerExpression te = new TernaerExpression(cond, f, s);

	te.setContainer(((VInfo) vi).clazz);
	te.setLine(node.jjtGetChild(0).getFirstToken().beginLine);

	return te;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionConditionalOrExpression node, 
			Object vi) {
	return handleBinaryExpression(node, BinaryExpression.CONDITIONAL_OR, 
				      vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionConditionalAndExpression node, 
			Object vi) {
	return handleBinaryExpression(node, BinaryExpression.CONDITIONAL_AND, 
				      vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionInclusiveOrExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.INCLUSIVE_OR, 
				      vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionExclusiveOrExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.EXCLUSIVE_OR, 
				      vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionAndExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.AND, vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionEqualityExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.EQUALITY, vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionInstanceOfExpression node, Object vi) {
	return new InstanceOfExpression((Expression) node.jjtGetChild(0).jjtAccept(this, vi), 
					(Class) node.jjtGetChild(1).jjtAccept(this, 
					vi));
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionRelationalExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.RELATIONAL, vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionShiftExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.SHIFT, vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionAdditiveExpression node, Object vi) {
	return handleBinaryExpression(node, BinaryExpression.ADDITIVE, vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionMultiplicativeExpression node, 
			Object vi) {
	return handleBinaryExpression(node, BinaryExpression.MULTIPLICATIVE, 
				      vi);
    } 

    // no difference between UnaryPlusMins and UnaryNOTPlusMinus !

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionUnaryExpression node, Object vi) {
	return new UnaryExpression((Expression) node.jjtGetChild(1).jjtAccept(this, vi), 
				   node.getFirstToken().image);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionUnaryExpressionNotPlusMinus node, 
			Object vi) {
	return new UnaryExpression((Expression) node.jjtGetChild(1).jjtAccept(this, vi), 
				   node.getFirstToken().image);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionCastExpression node, Object vi) {

	// visit Type
	CastExpression cast = 
	    new CastExpression((Class) node.jjtGetChild(0).jjtAccept(this, 
		vi));

	// visit (Unary)Expression
	Expression[]   e = new Expression[1];

	e[0] = (Expression) node.jjtGetChild(1).jjtAccept(this, vi);

	cast.setChildren(e);

	return cast;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionPrimaryExpression node, Object vi) {
	return handlePrimaryExpression(node, vi);
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionPrimaryPrefix node, Object vi) {

	// AssertionPrimaryPrefix ::=
	// ( Literal | "this" | "super" "." <IDENTIFIER> | "(" AssertionExpression ")" | Name )
	if (node.jjtGetNumChildren() > 0) {

	    // Nested Expression ?
	    if (node.getFirstToken().image.equals("(")) {
		NestedExpression ne = 
		    new NestedExpression((Expression) node.jjtGetChild(0).jjtAccept(this, 
			vi));

		return ne;
	    } 

	    // Literal ?
	    if (((SimpleNode) node.jjtGetChild(0)).hasId(JassParserTreeConstants.JJTLITERAL)) {
		return (Literal) node.jjtGetChild(0).jjtAccept(this, vi);
	    } 

	    // Reference ?
	    if (node.jjtGetChild(0).hasId(JJTNAME)) {
		String refer = (String) node.jjtGetChild(0).jjtAccept(this, 
							 vi);

		if (refer.indexOf('.') > -1) {
		    NameExpression re = new NameExpression();

		    re.setContainer(((VInfo) vi).clazz);
		    re.setLine(node.getFirstToken().beginLine);
		    re.addChildren(refer, 
				   new Set(((VInfo) vi).locals.toArray()));

		    return re;
		} 

		SimpleNameExpression sne = new SimpleNameExpression(refer);

		sne.setLocals(new Set(((VInfo) vi).locals.toArray()));
		sne.setLine(node.getFirstToken().beginLine);
		sne.setContainer(((VInfo) vi).clazz);

		return sne;
	    } 
	} 

	// THIS reference ?
	if (node.getFirstToken().image.equals("this")) {
	    SimpleNameExpression re = new SimpleNameExpression("this");

	    re.setLine(node.getFirstToken().beginLine);
	    re.setContainer(((VInfo) vi).clazz);
	    re.setLocals(new Set(((VInfo) vi).locals.toArray()));

	    return re;
	} 

	// Must be super.<IDENTIFIER>
	NameExpression re = new NameExpression();

	re.setContainer(((VInfo) vi).clazz);
	re.setLine(node.getFirstToken().beginLine);
	re.addChildren("super." + node.getLastToken().image, null);

	return re;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionPrimarySuffix node, Object vi) {

	// AssertionPrimarySuffix ::=
	// ( "[" AssertionExpression "]" | "." <IDENTIFIER> | AssertionArguments )
	if (node.jjtGetNumChildren() > 0) {

	    // ArrayBrackets ?
	    if (node.getFirstToken().image.equals("[")) {
		return new ArrayDimReferenceExpression((Expression) node.jjtGetChild(0).jjtAccept(this, 
			vi));
	    } 

	    // Arguments ?
	    if (node.getFirstToken().image.equals("(")) {
		return (ArgumentExpression) node.jjtGetChild(0).jjtAccept(this, 
			vi);
	    } 
	} 

	// Must be reference
	SimpleNameExpression sne = 
	    new SimpleNameExpression(node.getLastToken().image);

	sne.setLocals(new Set(((VInfo) vi).locals.toArray()));
	sne.setLine(node.getFirstToken().beginLine);
	sne.setContainer(((VInfo) vi).clazz);

	return sne;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see
     */
    public Object visit(JassAssertionArguments node, Object vi) {
	if (node.jjtGetNumChildren() > 0) {
	    return (ArgumentExpression) node.jjtGetChild(0).jjtAccept(this, 
		    vi);
	} 

	return new ArgumentExpression();
    } 

    /**
     * AssertionArgumentList ::= AssertionExpression 
     *                           ( "," AssertionExpression )*
     *
     * @param node
     * @param vi
     *
     * @return
     *
     * @see */
    public Object visit(JassAssertionArgumentList node, Object vi) {

	// 
	Expression[]       ex = new Expression[node.jjtGetNumChildren()];
	ArgumentExpression ae = new ArgumentExpression();

	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    ex[i] = (Expression) node.jjtGetChild(i).jjtAccept(this, vi);
	} 

	ae.setChildren(ex);

	return ae;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

