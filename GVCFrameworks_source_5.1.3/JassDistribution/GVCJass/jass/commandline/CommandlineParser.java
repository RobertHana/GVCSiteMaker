package jass.commandline;

import jass.GlobalFlags;
import jass.util.SourceFile;
import java.util.*;

/**
 * This class has only one public method, that is used to parse the
 * commandline. If the commandline is syntactically correct, it will 
 * set the  GlobalFlags appropriately.
 */
public class CommandlineParser implements CommandlineParserConstants {

    /**
     * parse the command line and return a list of filenames, if it 
     * was syntactically correct.
     */
    public static SourceFile[] parse(String[] args) throws Exception {
	if (args.length == 0) {
	    throw new Exception("missing arguments");
	}
	Vector arglist = new Vector (args.length);
	arglist.addAll(Arrays.asList(args));

	arglist = parseCompilationMode(arglist);
	arglist = parseGeneralOptions(arglist);
	return parseFilenames(arglist);
    }

    /**
     * Check whether the first argument is a compilation mode. If yes, we
     * may also have to check for compilation options. The method throws
     * an exception if it can not parse the compilation options.
     */
    private static Vector parseCompilationMode(Vector args) throws Exception {
	String first = firstArg(args);
	String opts  = null;
	int    index = first.indexOf('[');
	if (index > 0) {
	    opts  = first.substring(index);
	    first = first.substring(0,index);
	}
	for (int key = 0; key < COMPILATION_MODES.length; key ++) {
	    if (first.equalsIgnoreCase(COMPILATION_MODES[key])) {
		switch (key) {
		case KEY_CONTRACT: 
		    GlobalFlags.MODE = GlobalFlags.CONTRACT;
		    break;
		case KEY_WARNING: 
		    GlobalFlags.MODE = GlobalFlags.WARNING; 
		    break;
		case KEY_NOTHING: 
		    GlobalFlags.MODE = GlobalFlags.NOTHING;
		    break;
		case KEY_INTERFERENCE: 
		    GlobalFlags.MODE = GlobalFlags.INTERFERENCE; 
		    break;
		case KEY_JML: 
		    GlobalFlags.MODE = GlobalFlags.GENERATE_JML; 
		    break;
		}
		removeFirstArg(args);
		if (opts != null) {
		    args.add(0,opts);
		}
		if (GlobalFlags.MODE == GlobalFlags.CONTRACT 
		    || GlobalFlags.MODE == GlobalFlags.WARNING) {
		    args = findCompilationOptions(args);
		}
		return args;
	    }
	}
	// no compilation mode found
	//  => it is ok, cause they are optional.
	return args;
    }


    /**
     * check whether the first argument starts a compilation option 
     * list. If yes, we will try to parse the list until the closing
     * bracket. If this fails, the method will throw an exception, else
     * it will return the list of not handled arguments.
     */
    private static Vector findCompilationOptions(Vector args) 
	throws Exception {
	if (args.size() == 0) {
	    return args;
	}
	String current = removeFirstArg(args);
	if (! current.startsWith("[")) {
	    // no compilation options, so return.
	    return args;
	}
	// collect all compilation options
	current = current.substring(1);
	StringBuffer options = new StringBuffer();
	
	while (! (current.endsWith("]"))) {
	    options.append(current).append(" ");
	    if (args.size() == 0) {
		throw new Exception ("closing bracket ] for "+
				     "compilation options not found!");
	    }
	    current = removeFirstArg(args);
	}
	options.append(current.substring(0,current.length()-1));
	parseCompilationOptions(options.toString());
	return args;
    }

    /**
     * get the first argument in the list as String. This auxiliary function
     * is used by findCompilationOptions(...) and parseCompilationMode(...);
     */
    private static String firstArg(Vector args) 
	throws NoSuchElementException {
	return ((String) args.firstElement());
    }
	    
    /**
     * parse the compilation options and set the GlobalFlags appropriately.
     * This method throws an exception if an unknown compilation options
     * was found.
     */
    public static void parseCompilationOptions(String opt) throws Exception {
	GlobalFlags.GENERATE_PRE = false;
	GlobalFlags.GENERATE_POST = false;
	GlobalFlags.GENERATE_INV = false;
	GlobalFlags.GENERATE_LOOP = false;
	GlobalFlags.GENERATE_CHECK = false;
	GlobalFlags.GENERATE_FORALL = false;
	GlobalFlags.GENERATE_REFINE = false;
	GlobalFlags.GENERATE_TRACE = false;
	GlobalFlags.OPTIMIZING = false;
	StringTokenizer tokens = new StringTokenizer(opt, ", \t");
	while(tokens.hasMoreTokens()) {
	    String token = tokens.nextToken();
	    for (int key = 0; (key < COMPILATION_OPTIONS.length)
		     && (token != null); key++) {
		if (token.equalsIgnoreCase(COMPILATION_OPTIONS[key])) {
		    token = null;
		    switch (key) {
		    case KEY_PRE: GlobalFlags.GENERATE_PRE = true;
			break;
		    case KEY_POST: GlobalFlags.GENERATE_POST = true;
			break;
		    case KEY_INV: GlobalFlags.GENERATE_INV = true;
			break;
		    case KEY_LOOP: GlobalFlags.GENERATE_LOOP = true;
			break;
		    case KEY_CHECK: GlobalFlags.GENERATE_CHECK = true;
			break;
		    case KEY_FORALL: GlobalFlags.GENERATE_FORALL = true;
			break;
		    case KEY_REFINE: GlobalFlags.GENERATE_REFINE = true;
			break;
		    case KEY_TRACE: GlobalFlags.GENERATE_TRACE = true;
			break;
		    case KEY_OPT: GlobalFlags.OPTIMIZING = true;
			break;
		    }
		}
	    }
	    if (token != null) {
		throw new Exception ("unknown compilation mode \"" 
				     + token + "\"");
	    }
	}
    }

    /**
     * parse the general options. Every argument, that is not a general
     * option ore an argument for a general option must appear at the
     * end of the arglist.
     */
    private static Vector parseGeneralOptions(Vector args) throws Exception {
	Vector leftArgs = new Vector(1);
	while(args.size() > 0) {
	    String arg = removeFirstArg(args);
	    for (int key = 0; key < GENERAL_OPTIONS.length &&
		     arg != null; key++) {
		if (arg.equalsIgnoreCase(GENERAL_OPTIONS[key])) {
		    if (leftArgs.size() != 0) {
			throw new 
			    Exception("not allowed to specify global option " 
				      + arg
				      + "after first filename");
		    }		    
		    arg = null;
		    switch (key) {
		    case KEY_HELP:
			GlobalFlags.MODE = GlobalFlags.PRINT_HELP;
			break;
		    case KEY_VERSION:
			GlobalFlags.MODE = GlobalFlags.PRINT_VERSION;
			break;
		    case KEY_SOURCE:
			if (args.size() > 0) {
			    String token = removeFirstArg(args);
			    if (Double.parseDouble(token) >= 1.4) {
				GlobalFlags.PARSE_ASSERT = true;
			    }
			} else {
			    throw new 
				Exception("missing source information");
			}
			break;
		    case KEY_DESTINATION:
			if (args.size() > 0) {
			    GlobalFlags.destinationDir = 
				new java.io.File(removeFirstArg(args));
			} else {
			    throw new 
				Exception("missing destination directory");
			}
			break;
		    case KEY_DEPEND:
			GlobalFlags.DEPEND = true;
			break;
		    case KEY_NODEPEND:
			GlobalFlags.DEPEND = false;
			break;
		    case KEY_QUIET:
			GlobalFlags.VERBOSE = 0;
			break;
		    case KEY_NORMAL:
			GlobalFlags.VERBOSE = 1;
			break;
		    case KEY_VERBOSE:
			GlobalFlags.VERBOSE = 3;
			break;
		    case KEY_DATAFLOW:
			GlobalFlags.ANALYSE_DATAFLOW = true;
			break;
		    case KEY_NODATAFLOW:
			GlobalFlags.ANALYSE_DATAFLOW = false;
			break;

		    case KEY_JD_JAVADOC:
			GlobalFlags.JD_MODE = GlobalFlags.JD_JAVADOC;
			break;
		    case KEY_JD_HTML:
			GlobalFlags.JD_MODE = GlobalFlags.JD_HTML;
			break;
		    case KEY_JD_NOTHING:
			GlobalFlags.JD_MODE = GlobalFlags.JD_NOTHING;
			break;
		    }
		}
	    }
	    if (arg != null) {	
		leftArgs.add(arg);
	    }
	}
	return leftArgs;
    }		    

    
    /** 
     * parse the left arguments as filenames. Currently we only allow
     * one file, so this list should include exactly one argument.
     */
    private static SourceFile[] parseFilenames(Vector args) throws Exception {
	if (args.size() < 1) {
	    if (GlobalFlags.MODE ==  GlobalFlags.PRINT_HELP ||
		GlobalFlags.MODE ==  GlobalFlags.PRINT_VERSION) {
		return null;
	    }
	    throw new Exception ("missing filename!");
	}
	SourceFile[] files = new SourceFile[args.size()];
	for (int i = 0; i < args.size(); i++) {
	    files[i] = new SourceFile( (String) args.elementAt(i));
	    if (! files[i].exists()) {
		throw new Exception ("file " + files[i].getName() 
				     + " not found!");
	    }
	}
	return files;
    }


    /**
     * get the first argument in the list as String and remove it from the 
     * vector. This auxiliary function is used by parseGlobalOptions(...);
     */
    private static String removeFirstArg(Vector args) 
	throws NoSuchElementException {
	String returnVal = firstArg(args);
	args.removeElementAt(0);
	return returnVal;
    }

}
