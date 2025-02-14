/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion.traceMonitor;

import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.tree.*;
import jass.runtime.traceAssertion.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public abstract class MonitorTraceAssertion extends TraceAssertion 
    implements Freezable {
    public static final String CLASSNAME = 
	"jass.runtime.traceAssertion.traceMonitor.MonitorTraceAssertion";
    FrameMonitor	       monitor;

    /**
     * Method declaration
     *
     *
     * @param c
     * @param parameters
     *
     * @see
     */
    public void communicate(Communication c, Parameter[] parameters) {
	super.communicate(c, parameters);
	monitor.communicate(c, parameters);
	monitor.announceTrace(this);
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void run() {
	ProcessContext   mainContext = getMainContext();
	DefaultTreeModel treeModel = 
	    new DefaultTreeModel(mainContext.getNode());

	treeModel.addTreeModelListener(new ProcessTreeModelListener());

	monitor = new FrameMonitor(treeModel);

	mainContext.setMonitoring(monitor, treeModel);
	getProcessObserver().setTraceMonitor(monitor);

	// monitor.pack();
	monitor.setSize(1024, 768);
	monitor.show();
	super.run();
    } 

    // Interface Freezable

    /**
     * Method declaration
     *
     *
     * @see
     */
    public synchronized void freeze() {
	try {
	    wait();
	} catch (Exception e) {
	    System.err.println(e);
	    System.exit(0);
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public synchronized void thaw() {
	notify();
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

