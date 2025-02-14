package jass.commandline;


/**
 * This interface provides all the constants, that are accepted
 * at the jass command line.
 */
interface CommandlineParserConstants {
    // compilation modes
    static final String CONTRACT     = "-contract";
    static final String WARNING      = "-warning";
    static final String NOTHING      = "-nothing";
    static final String INTERFERENCE = "-interference";
    static final String JML          = "-jml";

    static final String[] COMPILATION_MODES =
    { CONTRACT, WARNING, NOTHING, INTERFERENCE };

    static final int KEY_CONTRACT = 0;
    static final int KEY_WARNING = 1;
    static final int KEY_NOTHING = 2;
    static final int KEY_INTERFERENCE = 3;
    static final int KEY_JML = 4;

    // compilation options
    static final String PRE    = "pre";
    static final String POST   = "post" ;
    static final String INV    = "inv" ;
    static final String LOOP   = "loop";
    static final String FORALL = "forall";
    static final String CHECK  = "check";
    static final String OPT    = "opt";
    static final String REFINE = "refine";
    static final String TRACE  = "trace";

    static final String[] COMPILATION_OPTIONS = 
    { PRE, POST, INV, LOOP, FORALL, CHECK, OPT, REFINE, TRACE };

    static final int KEY_PRE = 0;
    static final int KEY_POST = 1;
    static final int KEY_INV = 2;
    static final int KEY_LOOP = 3;
    static final int KEY_FORALL = 4;
    static final int KEY_CHECK = 5;
    static final int KEY_OPT = 6;
    static final int KEY_REFINE = 7;
    static final int KEY_TRACE = 8;

    // General options
    /** print usage information and abort */
    static final String HELP = "-help";
    /** print version information and abort */
    static final String VERSION = "-version";
    /** specify source code version. When parsing source code for Java 
	>=1.4 version, 'assert' has to be treated as a keyword, otherwise
        not. */
    static final String SOURCE = "-source";
    /** specify the destination directory */
    static final String DESTINATION = "-d";
    /** check dependencies */
    static final String DEPEND = "-depend";
    /** do not check dependencies */
    static final String NODEPEND = "-nodepend";
    /** verbose level: quiet */
    static final String QUIET = "-quiet";
    /** verbose level: normal */
    static final String NORMAL = "-normal";
    /** verbose level: verbose */
    static final String VERBOSE = "-verbose";
    /** analyse dataflow */
    static final String DATAFLOW = "-dataflow";
    /** do not analyse dataflow */
    static final String NODATAFLOW = "-nodataflow";

    /** 
     * generate JavaDoc conform section for pre- and postconditions... 
     * This is the default mode. 
     * @see jass.Globalflags.JD_MODE
     */
    static final String JD_JAVADOC = "-javadoc";
    /** 
     * generate HTML code for pre- and postconditions... 
     * @see jass.Globalflags.JD_MODE
     */
    static final String JD_HTML = "-htmldoc";
    /** 
     * generate no documentation code for pre- and postconditions... 
     * @see jass.Globalflags.JD_MODE
     */
    static final String JD_NOTHING = "-nodoc";
    
    static final String[] GENERAL_OPTIONS = 
    { HELP, VERSION, SOURCE, DESTINATION, DEPEND, NODEPEND, QUIET, 
      NORMAL, VERBOSE, DATAFLOW, NODATAFLOW, JD_JAVADOC, JD_HTML, 
      JD_NOTHING };  

    static final int KEY_HELP = 0;
    static final int KEY_VERSION = 1;
    static final int KEY_SOURCE = 2;
    static final int KEY_DESTINATION = 3;
    static final int KEY_DEPEND = 4;
    static final int KEY_NODEPEND = 5;
    static final int KEY_QUIET = 6;
    static final int KEY_NORMAL = 7;
    static final int KEY_VERBOSE = 8;
    static final int KEY_DATAFLOW = 9;
    static final int KEY_NODATAFLOW = 10;
    static final int KEY_JD_JAVADOC = 11;
    static final int KEY_JD_HTML = 12;
    static final int KEY_JD_NOTHING = 13;
}
