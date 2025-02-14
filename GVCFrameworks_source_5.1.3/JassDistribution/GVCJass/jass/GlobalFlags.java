/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/
package jass;

import java.io.File;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class GlobalFlags {

    // destination directory
    public static File                destinationDir = null;

    // compiler modes
    public static final int	      CONTRACT = 1;
    public static final int	      WARNING = 2;
    public static final int	      NOTHING = 0;
    public static final int	      INTERFERENCE = 3;
    public static final int	      PRINT_HELP = 4;
    public static final int	      PRINT_VERSION = 5;
    public static final int	      GENERATE_JML = 6;
    public static int		      MODE = CONTRACT;

    // compiler controls
    public static boolean	      OPTIMIZING = false;
    public static boolean	      GENERATE_INV = true;
    public static boolean	      GENERATE_PRE = true;
    public static boolean	      GENERATE_POST = true;
    public static boolean	      GENERATE_LOOP = true;
    public static boolean	      GENERATE_FORALL = true;
    public static boolean	      GENERATE_CHECK = true;
    public static boolean	      GENERATE_REFINE = true;
    public static boolean	      GENERATE_TRACE = true;
    public static boolean	      DEPEND = false;
    public static int		      VERBOSE = 1;
    public static boolean	      ANALYSE_DATAFLOW = false;
    public static boolean	      PARSE_ASSERT = false;

    // JavaDoc mode
    public static final int	      JD_JAVADOC = 1;
    public static final int	      JD_HTML = 2;
    public static final int	      JD_NOTHING = 0;
    /**
     * Which output format should be used to integrate assertions 
     * (pre- and postconditions, invariants) into JavaDoc comments
     * of methods:
     * JD_JAVADOC - use JavaDoc section keywords (for use with specialized 
     *              Doclet). This is the default.
     * JD_HTML    - directly insert HTML code (not so noice but will work
     *              more often)
     * JD_NOTHING - do not insert anything
     */
    public static int		      JD_MODE = JD_JAVADOC;
    

    /**
     * Set all global flags to their default.
     */
    public static void reset() {
	destinationDir = null;

	MODE = CONTRACT;

	OPTIMIZING = false;
	GENERATE_INV = true;
	GENERATE_PRE = true;
	GENERATE_POST = true;
	GENERATE_LOOP = true;
	GENERATE_FORALL = true;
	GENERATE_CHECK = true;
	GENERATE_REFINE = true;
	GENERATE_TRACE = true;
	DEPEND = false;
	VERBOSE = 1;
	ANALYSE_DATAFLOW = false;
	PARSE_ASSERT = false;

	JD_MODE = JD_JAVADOC;
    }


    // getting the system's newline character
    public static String              NEWLINE = System.getProperty("line.separator");

    // max of local vars per method:
    public static int		      MAX_LOCAL_VARS = 100;
    public static java.io.PrintWriter pout = 
	new java.io.PrintWriter(System.out, true);

    // identification constants:
    public static final int	      PRECONDITION = 0;
    public static final int	      POSTCONDITION = 1;
    public static final int	      INVARIANT = 2;
    public static final int	      REFINEMENT = 3;
    public static final int	      CHECK = 4;
    public static final int	      LOOPINVARIANT_WHILE_AND_DO = 5;
    public static final int	      LOOPINVARIANT_FOR = 6;
    public static final int	      LOOPVARIANT_WHILE_AND_DO = 7;
    public static final int	      LOOPVARIANT_FOR = 8;
    public static final int	      FORALL_DEC = 9;
    public static final int	      FORALL_BOUNDS = 10;

    // The string order follows the id constants order !
    public static final String[]      contract_strings = {
	"jass.runtime.PreconditionException", 
	"jass.runtime.PostconditionException", 
	"jass.runtime.InvariantException", 
	"jass.runtime.RefinementException", "jass.runtime.CheckException", 
	"jass.runtime.LoopInvariantException", 
	"jass.runtime.LoopInvariantException", 
	"jass.runtime.LoopVariantException", 
	"jass.runtime.LoopVariantException", 
	"jass.runtime.AssertionException", "jass.runtime.AssertionException"
    };
    public static final String[]      warning_strings = {
	"PreconditionViolation", "PostconditionViolation", 
	"InvariantViolation", "RefinementViolation", "CheckViolation", 
	"LoopInvariantViolation", "LoopInvariantViolation", 
	"LoopVariantViolation", "LoopVariantViolation", "AssertionViolation", 
	"AssertionViolation"
    };
}



/*--- formatting done in "Sun Java Convention" style on 03-06-2001 ---*/
