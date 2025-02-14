/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import jass.reflect.Class;
import jass.reflect.Method;
import jass.util.Set;
import java.util.Hashtable;
import jass.GlobalFlags;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class DataFlow {
    protected static Hashtable path = new Hashtable();
    protected static Hashtable classes = new Hashtable();

    /**
     * Method declaration
     *
     *
     * @param m
     *
     * @return
     *
     * @see
     */
    public DependencyCollector analyseDataFlow(Method m) {
	if (GlobalFlags.VERBOSE > 2) {
	    System.out.println("Message: Dataflow analysis of method " 
			       + m.getIdString() + " ...");
	} 

	DependencyCollector dc = new DependencyCollector();

	// get the methods dependencies ...
	// m.reflect();
	dc.union(m.getDependencies());

	// is m a constructor ? -> dataflow of created instance must be analysed:
	if (m instanceof Constructor 
		&&!classes.containsKey(m.getContainer())) {
	    Class cl = (Class) m.getContainer();

	    // mark class
	    path.put(cl, "");

	    if (GlobalFlags.VERBOSE > 2) {
		System.out.println("Message: Dataflow analysis of class " 
				   + cl.getName() + " ...");
	    } 

	    dc.union(cl.getDependencies());
	} 

	// informations of calls in constructors are inserted automatically while expression reflection.
	Object[] calls = m.getCalls().elements();

	for (int i = 0; i < calls.length; i++) {
	    if (!path.containsKey(calls[i])) {
		path.put(calls[i], "");
		dc.union(analyseDataFlow((Method) calls[i]));
	    } 
	} 

	return dc;
    } 

    // TODO: Kontrollflussermittlung ...

    /**
     * Method declaration
     *
     *
     * @param m
     *
     * @return
     *
     * @see
     */
    public Set analyseCalls(Method m) {
	Set c = new Set();

	// get the methods dependencies ...
	m.reflect();
	c.union(m.getDependencies().getCalls());

	Object[] calls = m.getCalls().elements();

	// recursive descend the call stack ...
	for (int i = 0; i < calls.length; i++) {
	    if (!path.containsKey(calls[i])) {
		path.put(calls[i], "");
		c.union(analyseCalls((Method) calls[i]));
	    } 
	} 

	return c;
    } 

    /**
     * Method declaration
     *
     *
     * @param m
     * @param dc
     *
     * @return
     *
     * @see
     */
    public Set getControlFlows(Method m, DependencyCollector dc) {
	Set      run = new Set();
	Class    c;
	Class    runnable = ClassPool.getClass("java.lang.Runnable");
	Object[] creates;

	if (dc == null) {
	    creates = analyseDataFlow(m).getCreates().elements();
	} else {
	    creates = dc.getCreates().elements();
	}

	for (int i = 0; i < creates.length; i++) {
	    c = ((Creation) creates[i]).getType();

	    if (c.doesImplement(runnable)) {
		run.addElement(c);
	    } 
	} 

	return run;
    } 

    /**
     * Method declaration
     *
     *
     * @param m
     *
     * @see
     */
    public void checkInterference(Method m) {

	// get control flows ...
	DependencyCollector dc = analyseDataFlow(m);
	Object[]	    cfs = getControlFlows(m, dc).elements();
	Class[]		    flows = new Class[cfs.length + 1];

	flows[0] = (Class) m.getContainer();

	for (int i = 0; i < cfs.length; i++) {
	    flows[i + 1] = (Class) cfs[i];
	}

	DependencyCollector[] dcs = new DependencyCollector[cfs.length + 1];

	dcs[0] = dc;

	if (cfs.length < 1) {
	    if (GlobalFlags.VERBOSE > 0) {
		GlobalFlags.pout.println("Message: Only one control flow involved. NO INTERFERENCE possible.");
	    } 
	} else {
	    Method hm;

	    // reflect control flows ...
	    for (int i = 0; i < cfs.length; i++) {
		if (GlobalFlags.VERBOSE > 0) {
		    GlobalFlags.pout.println("Message: Scanning control flow " 
					     + ((Class) cfs[i]).getName() 
					     + " ...");
		} 

		hm = ((Class) cfs[i]).getMethod("run()");

		if (hm == null) {
		    throw new ReflectionError(((Class) cfs[i]).getName() 
					      + ": <Class must implement method run().>");
		} 

		dcs[i + 1] = analyseDataFlow(hm);
	    } 

	    // interference check follows ...
	    boolean inter = false;

	    for (int i = 0; i < flows.length; i++) {

		// get methods of the control flow
		Object[]	 ms = dcs[i].getCalls().elements();
		java.util.Vector v = new java.util.Vector();

		// get Assertions of this methods
		for (int j = 0; j < ms.length; j++) {
		    BooleanAssertion[] as = ((Method) ms[j]).getAssertions();

		    for (int l = 0; l < as.length; l++) {
			v.addElement(as[l]);
		    }
		} 

		// for all assertions (of one control flow) do ...
		for (int k = 0; k < v.size(); k++) {
		    BooleanAssertion a = (BooleanAssertion) v.elementAt(k);

		    a.reflectExpressions();

		    // compare with the other controlflows ...
		    for (int t = 0; t < flows.length; t++) {
			if (t != i) {

			    // System.out.println(flows[t]+" | "+dcs[t].getWrites()+" | "+a.getReads());
			    if (!fieldIntersects(dcs[t].getWrites(), 
						 a.getReads()).empty()) {

				// ??? perhaps next part can be optimized a bit ...
				inter = true;

				String asid = "assertion";

				if (a instanceof Precondition) {
				    asid = 
					"precondition of method \n  " 
					+ ((Method) a.container).getContainer().getName() 
					+ "." 
					+ ((Method) a.container).getIdString();
				} 

				if (a instanceof Check) {
				    asid = 
					"check assertion number " 
					+ ((Check) a).getCheckNumber() 
					+ " of method \n  " 
					+ ((Method) a.container).getContainer().getName() 
					+ "." 
					+ ((Method) a.container).getIdString();
				} 

				if (a instanceof LoopInvariant) {
				    asid = 
					"loop invariant number " 
					+ ((LoopInvariant) a).getLoopNumber() 
					+ " of method \n  " 
					+ ((Method) a.container).getContainer().getName() 
					+ "." 
					+ ((Method) a.container).getIdString();
				} 

				if (a instanceof LoopVariant) {
				    asid = 
					"loop variant " 
					+ ((LoopVariant) a).getLoopNumber() 
					+ " of method \n  " 
					+ ((Method) a.container).getContainer().getName() 
					+ "." 
					+ ((Method) a.container).getIdString();
				} 

				if (a instanceof Postcondition) {
				    asid = 
					"postcondition of method \n  " 
					+ ((Method) a.container).getContainer().getName() 
					+ "." 
					+ ((Method) a.container).getIdString();
				} 

				if (a instanceof Invariant) {
				    asid = "invariant of class \n  " 
					   + ((Class) a.container).getName();
				} 

				System.out.println("\nPotential interference in " 
						   + asid + ":");
				System.out.println("  [" + flows[i].getName() 
						   + " | " 
						   + flows[t].getName() 
						   + "]  ->\n  " 
						   + fieldIntersects(dcs[t].getWrites(), 
								     a.getReads()));
			    } 
			} 
		    } 
		} 
	    } 

	    if (!inter) {
		System.out.println("Message: No potential interference found.");
	    } 
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param a
     * @param b
     *
     * @return
     *
     * @see
     */
    private Set fieldIntersects(Set a, Set b) {
	Object[] os = a.elements();
	Set      s = new Set();

	for (int i = 0; i < os.length; i++) {
	    if (os[i] instanceof Field && b.contains(os[i])) {
		s.addElement(os[i]);
	    } 
	} 

	return s;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

