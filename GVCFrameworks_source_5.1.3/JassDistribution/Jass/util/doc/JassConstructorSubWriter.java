package jass.util.doc;

import com.sun.tools.doclets.standard.*;
import com.sun.tools.doclets.*;
import com.sun.javadoc.*;
import java.util.*;
import java.io.*;


/**
 * This class overrides com.sun.javadoc.ConstructorSubWriter in order
 * to read JASSDoc-style comments for pre- and postconditions.
 */
public class JassConstructorSubWriter extends ConstructorSubWriter implements Tags {

    /**
     * Constructor.
     *
     * @param writer The SubWriterHolderWriter.
     * @param class The ClassDoc.
     */
    public JassConstructorSubWriter(SubWriterHolderWriter writer, 
				  ClassDoc classdoc) {
        super(writer, classdoc);
    }

    /**
     * Constructor.
     *
     * @param writer The SubWriterHolderWriter.
     */
    public JassConstructorSubWriter(SubWriterHolderWriter writer) {
        super(writer);
    }

    /**
     * This method was changed to include JASSDoc-style comments.
     *
     * @param member The ProgramElementDoc.
     */
    protected void printTags(ProgramElementDoc member) {
        ParamTag[] params = ((ConstructorDoc)member).paramTags();


        Tag[] requires = member.tags(JASSDOC_PRE); // Neues Tag[] 
        Tag[] ensures = member.tags(JASSDOC_POST);   // Neues Tag[] 

        ThrowsTag[] thrown = ((ConstructorDoc)member).throwsTags();
        Tag[] sinces = member.tags("since");
        SeeTag[] sees = member.seeTags();
        if (params.length + thrown.length + sinces.length
	    + sees.length + requires.length > 0) { // requires.length neu
            writer.dd();
            writer.dl();
            printParamTags(params);
	    printJASSTags(requires, "Requires:"); // Neu 
	    printJASSTags(ensures, "Ensures:"); // Neu 
            printThrowsTags(thrown);
            writer.printSinceTag(member);
            writer.printSeeTags(member);
            writer.dlEnd();
            writer.ddEnd();
        }
    }    
    

    /**
     * Prints the JASS- (method-) tags. 
     *  
     * @param params The tags to print.
     * @param name The type of assertion.
     */
    protected void printJASSTags(Tag[] params, String name) {
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
}  
    
    





