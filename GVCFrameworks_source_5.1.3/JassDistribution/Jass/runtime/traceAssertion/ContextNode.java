/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import javax.swing.JTree;
import javax.swing.tree.*;
import jass.runtime.traceAssertion.traceMonitor.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ContextNode extends DefaultMutableTreeNode {

    /**
     * Constructor declaration
     *
     *
     * @param communicator
     *
     * @see
     */
    public ContextNode(Communicator communicator) {
	super(communicator);
    }

    /**
     * Method declaration
     *
     *
     * @param node
     * @param traceMonitor
     *
     * @see
     */
    public void add(MutableTreeNode node, FrameMonitor traceMonitor) {
	super.add(node);

	/*
	 * TreePath myPath = new TreePath(getPath());
	 * JTree processTree = traceMonitor.getProcessTree();
	 * processTree.makeVisible(myPath);
	 */
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Communicator getObject() {
	return (Communicator) getUserObject();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public ProcessContext getParentContext() {
	TreeNode       parent;
	ProcessContext parentContext = null;

	parent = getParent();

	if (parent != null) {
	    ContextNode parentNode = (ContextNode) parent;

	    parentContext = (ProcessContext) parentNode.getObject();
	} 

	return parentContext;
    } 

    /**
     * Method declaration
     *
     *
     * @param node
     * @param traceMonitor
     *
     * @see
     */
    public void remove(MutableTreeNode node, FrameMonitor traceMonitor) {
	super.remove(node);

	/*
	 * JTree processTree = traceMonitor.getProcessTree();
	 * processTree.validate();
	 */
    } 

    /**
     * Method declaration
     *
     *
     * @param communicator
     *
     * @see
     */
    public void setObject(Communicator communicator) {
	setUserObject(communicator);
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

