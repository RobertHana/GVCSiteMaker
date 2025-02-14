package jass.commandline;

import jass.commandline.CommandlineParserConstants;
import jass.runtime.util.ToString;
import jass.util.SourceFile;


/**
 * This class provides the usage String and a method to output the help
 * information.
 */
public class Help implements CommandlineParserConstants {
    final static String		COMPILER_OPTIONS_LABEL = "compilation options";
    final static String		GENERAL_OPTIONS_LABEL = "general options";
    final static String		FILENAME = "filenames...";
    final static String		SPACE_BETWEEN_CELLS = "   ";
    public final static String		USAGE = 
	"Usage: jass [" + COMPILER_OPTIONS_LABEL + "] [" 
	+ GENERAL_OPTIONS_LABEL + "] " + FILENAME + ToString.NEWLINE 
	+ "       jass " + HELP + ToString.NEWLINE 
	+ "       jass " + VERSION;

    /*
     * final static String FORHELP
     * = "For help use 'jass " + ToString.ctoken(CommandlineParserConstants.HELP) + "'";
     */
    final static String[][]     THE_HELP = new String[][] {
	 {
	    COMPILER_OPTIONS_LABEL + " include"
	}, {
	    "", CONTRACT + " [ <arg> ... ]"
	}, {
	    "", "", "violation of '<arg>' stops program execution."
	}, {
	    "", "", "<arg> is one of"
	}, {
	    "", "", "", CHECK, "check-assertion"
	}, {
	    "", "", "", FORALL, 
	    "assertion which include forall or exists"
	}, {
	    "", "", "", INV, "class-invariant"
	}, {
	    "", "", "", LOOP, "loop-variant or -invariant"
	}, {
	    "", "", "", PRE, "pre-condition"
	}, {
	    "", "", "", POST, "post-condition"
	}, {
	    "", "", "", REFINE, "refinement"
	}, {
	    "", "", "", TRACE, "trace assertions"
	}, {
	    "", CONTRACT, 
	    "use all the above arguments (default)"
	}, {
	    "", CONTRACT + " [opt]", 
	    "optimize code when translation ." + SourceFile.JASS_EXTENSION 
	    + " to ." + SourceFile.JAVA_EXTENSION + " (default)"
	}, {
	    "", WARNING + " [ <arg> ...]"
	}, {
	    "", "", "violation of '<arg>' prints warning."
	}, {
	    "", "", "for possible <arg>s see '" + CONTRACT + '\''
	}, {
	    "", WARNING, "use all the above arguments"
	}, {
	    "", NOTHING, 
	    "just copy ." + SourceFile.JASS_EXTENSION + " to ." 
	    + SourceFile.JAVA_EXTENSION
	}, {
	    "", INTERFERENCE, 
	    "check for interferences of parallel java-processes"
	}, {
	    ""
	}, {
	    GENERAL_OPTIONS_LABEL + " include"
	}, {
	    "", DESTINATION + " <dir>", 
	    "output will go to directory <dir>"
	}, {
	    "", SOURCE + " <release>", 
	    "provide source compatibility with specified Java release"
	}, {
	    "", NORMAL, 
	    "show some status messages while parsing (default)"
	}, {
	    "", VERBOSE, 
	    "show a lot of status messages while parsing"
	}, {
	    "", QUIET, "show no status messages"
	}, {
	    "", DEPEND, 
	    "translate superclasses of refined class (default)"
	}, {
	    "", NODEPEND, 
	    "don't translate dependinf classes", 
	}, {
	    "", DATAFLOW, 
	    "activate dataflow analysis to detect side effects", 
	}, {					       // this entry belongs to the entry above
	    "", "", "of methods used in assertions"
	}, {
	    "", NODATAFLOW, "no dataflow analysis (default)"
	}, {
	    "", JD_JAVADOC, "userdefined sections for assertions in formal comments (default)"
	}, {
	    "", JD_HTML, "pure HTML code for assertions in formal comments"
	}, {
	    "", JD_NOTHING, "no code for assertions in formal comments"
	}
    };

    /**
     * Print a help sreen to <code>System.out</code>.
     *
     * @see #THE_HELP
     */
    public final static void printHelp() {
	System.out.println(USAGE);
	System.out.println();

	int[] tabToMaxLength = {
	    0, 0, 0, 0, 0, 0
	};

	//calculate the cell sizes
	for (int iLine = 0; iLine < THE_HELP.length; ++iLine) {
	    String[] line = THE_HELP[iLine];

	    for (int iTab = 1; iTab < line.length; ++iTab) {
		String cell = line[iTab];
		int    cellsize = cell.length();

		if (1 + iTab < line.length) {
		    int oldSize = tabToMaxLength[iTab];

		    if (cellsize > oldSize) {
			tabToMaxLength[iTab] = cellsize;
		    } 
		} 
	    } 
	} 

	//print all lines
	for (int iLine = 0; iLine < THE_HELP.length; ++iLine) {
	    String[] line = THE_HELP[iLine];

	    for (int iTab = 0; iTab < line.length; ++iTab) {
		String cell = line[iTab];
		System.out.print(cell);

		int cellsize = tabToMaxLength[iTab];
		for (int iSpace = cell.length(); iSpace < cellsize; 
			++iSpace) {
		    System.out.print(" ");
		} 

		System.out.print(SPACE_BETWEEN_CELLS);
	    } 
	    System.out.println();
	} 
    } 

}
