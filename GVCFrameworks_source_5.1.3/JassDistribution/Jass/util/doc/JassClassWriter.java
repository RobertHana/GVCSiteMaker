package jass.util.doc;

import com.sun.tools.doclets.standard.*;
import com.sun.tools.doclets.*;
import com.sun.javadoc.*;
import java.util.*;
import java.io.*;

public class JassClassWriter extends ClassWriter implements Tags {
    
    /**
     * Constructor. Parameters remain undocumentated as in original class.
     *
     * @see com.sun.tools.doclets.standard.ClassWriter
     */
    public JassClassWriter(String path, String filename, ClassDoc classdoc,
			 ClassDoc prev, ClassDoc next, ClassTree classtree,
			 boolean nopackage) throws IOException, DocletAbortException {
	super(path, filename, classdoc, prev, next, classtree, nopackage);
        methodSubWriter = new JassMethodSubWriter(this, classdoc); // geändert
        constrSubWriter = new JassConstructorSubWriter(this, classdoc);
        fieldSubWriter = new FieldSubWriter(this, classdoc);
        innerSubWriter = new ClassSubWriter(this, classdoc);
    }
    
    /**
     * Generate a class page.
     *
     * @param prev the previous class to generated, or null if no previous.
     * @param classdoc the class to generate.
     * @param next the next class to be generated, or null if no next.
     */
    public static void generate(ClassDoc classdoc, ClassDoc prev, 
                             ClassDoc next, ClassTree classtree, 
                             boolean nopackage) throws DocletAbortException {
            ClassWriter clsgen;
            String path = 
               DirectoryManager.getDirectoryPath(classdoc.containingPackage());
            String filename = classdoc.name() + ".html";
            try {
                clsgen = new JassClassWriter(path, filename, classdoc, 
					     prev, next, classtree, nopackage);
                clsgen.generateClassFile();
                clsgen.close();
            } catch (IOException exc) {
                Standard.configuration().standardmessage.
                    error("doclet.exception_encountered",
                           exc.toString(), filename);
                throw new DocletAbortException();
            }
    }


   public void generateTagInfo(Doc doc) {
	   super.generateTagInfo(doc);
	   //Tag[] requires = method.tags(JASSDOC_PRE);
	   //Tag[] ensures = method.tags(JASSDOC_POST);
	   Tag[] invariants = this.classdoc.tags(JASSDOC_INV);
	   if (invariants.length > 0) {
		   this.dl();
		   this.dt();
		   this.bold("Invariant:");
		   this.dd();
		   for (int i = 0; i < invariants.length; ++i) {
			   Tag it = invariants[i];
			   print(it.text());
		   }
		   this.ddEnd();
		   this.dlEnd();
	   }
   }
}








