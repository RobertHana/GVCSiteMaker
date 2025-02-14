/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public abstract class TraceAssertionManager {
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.TraceAssertionManager";
    TraceAssertion[]	       assertions;

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public TraceAssertionManager() {
	assertions = initiateTraceAssertions();

	for (int iAssertion = 0; iAssertion < assertions.length; 
		++iAssertion) {
	    TraceAssertion traceAssertion = assertions[iAssertion];

	    traceAssertion.init();
	    traceAssertion.run();
	} 
    }

    /**
     * Method declaration
     *
     *
     * @param methodReference
     * @param parameters
     *
     * @see
     */
    public void communicate(MethodReference methodReference, 
			    Parameter[] parameters) {

	// System.out.println("##" + toString() + " receives " + methodReference);
	for (int iAssertion = 0; iAssertion < assertions.length; 
		++iAssertion) {
	    TraceAssertion  traceAssertion = assertions[iAssertion];
	    SetOfMethods    alphabet = traceAssertion.getAlphabet();
	    MethodReference reference = methodReference;
	    boolean	    referenceBelongsToAssertion = false;
	    Class	    clazz = null;

	    do {

		// System.out.println("##TraceAssertion Manager tries:" + reference);
		if (alphabet.contains(reference)) {
		    referenceBelongsToAssertion = true;
		} else {
		    try {
			clazz = Class.forName(reference.getPackagename() 
					      + "." 
					      + reference.getClassname());
		    } catch (ClassNotFoundException e) {
			System.out.println(e);
		    } 

		    clazz = clazz.getSuperclass();

		    if (clazz != null) {
			String fullClassname = clazz.getName();
			int    endOfPackage = fullClassname.lastIndexOf('.');
			String packagename = fullClassname.substring(0, 
				endOfPackage);
			String classname = 
			    fullClassname.substring(endOfPackage + 1, 
						    fullClassname.length());

			reference = 
			    new MethodReference(packagename, classname, 
						reference.getMethodname(), 
						reference.isBeginOfMethod());
		    } 
		} 
	    } while (!referenceBelongsToAssertion && clazz != null);

	    if (referenceBelongsToAssertion) {
		traceAssertion.communicate(reference, parameters);
	    } 
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
    public abstract TraceAssertion[] initiateTraceAssertions();
}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

