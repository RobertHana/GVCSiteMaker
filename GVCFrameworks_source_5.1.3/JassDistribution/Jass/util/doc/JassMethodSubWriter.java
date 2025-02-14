package jass.util.doc;

import com.sun.tools.doclets.standard.*;
import com.sun.tools.doclets.*;
import com.sun.javadoc.*;
import java.util.*;
import java.io.*;

class JassMethodSubWriter extends MethodSubWriter implements Tags {

    /**
     * Constructor. Parameters remain undocumentated as in original class.
     *
     * @see com.sun.tools.doclets.standard.MethodSubWriter
     */
    public JassMethodSubWriter(SubWriterHolderWriter writer, 
			       ClassDoc classdoc) {
        super(writer, classdoc);
    }    

    /**
     * Prints the Jass- (method-) tags. 
     *  
     * @param params The tags to print.
     * @param name The type of assertion.
     */
    protected void printJassTags(Tag[] params, String name) {
        if (params.length > 0) {
            writer.dt();
            writer.bold(name); // Bezeichnung für Token ausgeben
	    print("</DT>");
	    writer.dd();
            for (int i = 0; i < params.length; ++i) {
                Tag pt = params[i];
		print(pt.text());
            }
	    writer.ddEnd();
        }
    }

    /** 
     * Proceeds through the classes tags and prints them. 
     * This is done by filtering the tags from the Doc and calling the matching
     * printing-subroutines. 
     *
     * @param member A ProgramElementDoc
     */
    protected void printTags(ProgramElementDoc member) {
        MethodDoc method = (MethodDoc)member;
        ParamTag[] params = method.paramTags();
        Tag[] returns = method.tags("return");
        Tag[] sinces = method.tags("since");
        Tag[] requires = method.tags(JASSDOC_PRE); // Neues Tag[] 
        Tag[] ensures = method.tags(JASSDOC_POST);   // Neues Tag[] 
        ThrowsTag[] thrown = method.throwsTags();
        SeeTag[] sees = method.seeTags();
        ClassDoc[] intfacs = member.containingClass().interfaces();
        ClassDoc overridden = method.overriddenClass();
        if (intfacs.length > 0 || overridden != null) {
            printTagsInfoHeader();
            printImplementsInfo(method);
            printOverridden(overridden, method);
            printTagsInfoFooter();
        }
        if (params.length + returns.length + thrown.length + sinces.length
	    + sees.length + requires.length > 0) { // requires.length neu
            printTagsInfoHeader();
            printParamTags(params);
	    printJassTags(requires, "Requires:"); // Neu 
	    printJassTags(ensures, "Ensures:"); // Neu 
            printReturnTag(returns);
            printThrowsTags(thrown);
            writer.printSinceTag(method);
            writer.printSeeTags(method);
            printTagsInfoFooter();
        } else {   // no tags are specified
            MethodDoc taggedMeth = new TaggedMethodFinder().
                                      search(method.containingClass(), method);
            if (taggedMeth != null) {
                printTagsFromTaggedMethod(taggedMeth);
            }
        }
    }
}
