/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import java.io.*;
import java.util.*;
import java.util.Arrays;
import jass.reflect.*;
import jass.reflect.Class;    // to avoid conflicts with java.lang.Class
import jass.runtime.util.ToJava;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class TraceAssertion extends Assertion {
    public final static String FIELDNAME_TRACEASSERTIONS = "traceAssertions";
    TraceAssertionExpression[] expressions;
    String		       packagename;

    /**
     * Constructor declaration
     *
     *
     * @param packagename
     *
     * @see
     */
    public TraceAssertion(String packagename) {
	this.packagename = packagename;
    }

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflectExpressions() {
	for (int iExpression = 0; iExpression < expressions.length; 
		++iExpression) {
	    TraceAssertionExpression expression = expressions[iExpression];

	    expression.reflect();
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String createJavaFile() {
	String		 classidentifier = getMappedClassidentifier();
	String		 classname = packagename + "." + classidentifier;
	String		 filename = classname.replace('.', File.separatorChar) 
				    + ".java";
	FileOutputStream targetStream = null;

	try {
	    targetStream = new FileOutputStream(filename);
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	} 

	PrintWriter writer = new PrintWriter(targetStream);

	writer.println("package " + packagename + ";");
	writer.println();
	writer.println("public class " + classidentifier + " extends " 
		       + jass.runtime.traceAssertion.TraceAssertionManager.CLASSNAME);
	writer.println("{");

	String classnameTraceAssertion = 
	    jass.runtime.traceAssertion.TraceAssertion.CLASSNAME;

	writer.println(ToJava.INDENT1 + "public " + classnameTraceAssertion 
		       + "[] " + "initiateTraceAssertions() {");
	writer.println(ToJava.INDENT2 + "return new " 
		       + classnameTraceAssertion + "[] {");

	for (int iAssertion = 0; iAssertion < expressions.length; 
		++iAssertion) {
	    TraceAssertionExpression assertion = expressions[iAssertion];

	    writer.print(ToJava.INDENT3 + "new " 
			 + assertion.getMappedClassname() + "()");

	    if (iAssertion + 1 < expressions.length) {
		writer.print(", ");
	    } 

	    writer.println();
	} 

	writer.println(ToJava.INDENT2 + "};");
	writer.println(ToJava.INDENT1 + "}");
	writer.println("} // end of class");
	writer.close();

	return filename;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String[] createJavaFiles() {
	Vector filenameVector = new Vector();

	for (int iAssertionExpression = 0; 
		iAssertionExpression < expressions.length; 
		++iAssertionExpression) {
	    TraceAssertionExpression assertionExpression = 
		expressions[iAssertionExpression];
	    String[]		     filenames = 
		assertionExpression.createJavaFiles();

	    filenameVector.addAll(Arrays.asList(filenames));
	} 

	String filename = createJavaFile();

	filenameVector.add(filename);

	String[] filenames = new String[filenameVector.size()];

	filenameVector.toArray(filenames);

	return filenames;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public TraceAssertionExpression[] getExpressions() {
	return expressions;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getMappedClassidentifier() {
	String classname = "";

	classname += 
	    jass.runtime.traceAssertion.TraceAssertion.CLASS_IDENTIFIERPREFIX;
	classname += Class.getIdentifier(getContainer().getUnitName());

	return classname;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getMappedPackagename() {
	return packagename;
    } 

    /**
     * Method declaration
     *
     *
     * @param expressionVector
     *
     * @see
     */
    public void setAssertionExpressions(Vector expressionVector) {
	expressions = new TraceAssertionExpression[expressionVector.size()];

	expressionVector.toArray(expressions);
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

