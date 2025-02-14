/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion.traceMonitor;

import javax.swing.JTree;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ProcessTreeModelListener implements TreeModelListener {

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    public void treeNodesChanged(TreeModelEvent e) {}

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    public void treeNodesInserted(TreeModelEvent e) {
	System.err.println("###" + e.getSource() + "###");

	/*
	 * JTree processTree = (JTree) e.getSource();
	 * processTree.makeVisible(e.getTreePath());
	 */
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    public void treeNodesRemoved(TreeModelEvent e) {}

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    public void treeStructureChanged(TreeModelEvent e) {}

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

