/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion.traceMonitor;

import java.util.Vector;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import jass.runtime.traceAssertion.*;
import java.awt.event.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class FrameMonitor extends JFrame {
    final int	     USER_ACTION_START = 0;
    final int	     USER_ACTION_TRACE = 1;
    final int	     USER_ACTION_STEP = 2;
    Freezable	     frozen;
    int		     lastUserAction;
    GridBagLayout    gridBagLayout1 = new GridBagLayout();
    JPanel	     jPanel1 = new JPanel();
    JLabel	     jLabel1 = new JLabel();
    JComboBox	     jComboBox1 = new JComboBox();
    JLabel	     jLabel2 = new JLabel();
    JLabel	     labelProcessNum = new JLabel();
    JPanel	     jPanel2 = new JPanel();
    JLabel	     jLabel3 = new JLabel();
    JTree	     processTree;
    DefaultTreeModel processTreeModel;
    GridBagLayout    gridBagLayout3 = new GridBagLayout();
    PanelAlphabet    panelTrace;
    JPanel	     jPanel3 = new JPanel();
    JButton	     buttonClose = new JButton();
    JButton	     buttonStart = new JButton();
    GridBagLayout    gridBagLayout2 = new GridBagLayout();
    DefaultListModel traceList;
    JScrollPane      jScrollPane1 = new JScrollPane();
    JPanel	     jPanel4 = new JPanel();
    JPanel	     jPanel5 = new JPanel();
    GridBagLayout    gridBagLayout4 = new GridBagLayout();
    JButton	     buttonStep = new JButton();
    JButton	     buttonTrace = new JButton();
    FlowLayout       flowLayout1 = new FlowLayout();

    /**
     * Constructor declaration
     *
     *
     * @param processTreeModel
     *
     * @see
     */
    public FrameMonitor(TreeModel processTreeModel) {
	traceList = new DefaultListModel();
	processTree = new JTree(processTreeModel);
	lastUserAction = USER_ACTION_STEP;

	try {
	    panelTrace = new PanelAlphabet("Trace", traceList);

	    jbInit();
	} catch (Exception e) {
	    e.printStackTrace();
	} 
    }

    /**
     * Method declaration
     *
     *
     * @throws Exception
     *
     * @see
     */
    private void jbInit() throws Exception {
	this.getContentPane().setLayout(gridBagLayout1);
	jLabel1.setText("Trace Assertion");
	jLabel2.setText("Processes: ");
	labelProcessNum.setText("0");
	jLabel3.setText("Enviroment");
	jPanel2.setLayout(gridBagLayout3);
	buttonClose.setText("Close");
	buttonClose.addActionListener(new java.awt.event.ActionListener() {

	    /**
	     * Method declaration
	     *
	     *
	     * @param e
	     *
	     * @see
	     */
	    public void actionPerformed(ActionEvent e) {
		buttonClosePressed(e);
	    } 

	});
	buttonStart.setText("Start");
	buttonStart.addActionListener(new java.awt.event.ActionListener() {

	    /**
	     * Method declaration
	     *
	     *
	     * @param e
	     *
	     * @see
	     */
	    public void actionPerformed(ActionEvent e) {
		buttonStartPressed(e);
	    } 

	});
	jPanel3.setLayout(gridBagLayout2);
	jPanel4.setLayout(gridBagLayout4);
	buttonStep.setText("Step");
	buttonStep.addActionListener(new java.awt.event.ActionListener() {

	    /**
	     * Method declaration
	     *
	     *
	     * @param e
	     *
	     * @see
	     */
	    public void actionPerformed(ActionEvent e) {
		buttonStepPressed(e);
	    } 

	});
	buttonTrace.setText("Trace");
	buttonTrace.addActionListener(new java.awt.event.ActionListener() {

	    /**
	     * Method declaration
	     *
	     *
	     * @param e
	     *
	     * @see
	     */
	    public void actionPerformed(ActionEvent e) {
		buttonTracePressed(e);
	    } 

	});
	jPanel5.setLayout(flowLayout1);
	flowLayout1.setAlignment(FlowLayout.LEFT);
	this.getContentPane().add(jPanel1, 
				  new GridBagConstraints(1, 0, 1, 1, 1.0, 
				  0.0, GridBagConstraints.CENTER, 
				  GridBagConstraints.BOTH, 
				  new Insets(0, 0, 0, 0), 1, 1));
	jPanel1.add(jLabel1, null);
	jPanel1.add(jComboBox1, null);
	jPanel1.add(jLabel2, null);
	jPanel1.add(labelProcessNum, null);
	this.getContentPane().add(jPanel2, 
				  new GridBagConstraints(1, 1, 1, 1, 1.0, 
				  1.0, GridBagConstraints.CENTER, 
				  GridBagConstraints.BOTH, 
				  new Insets(0, 0, 0, 0), 1, 1));
	jPanel2.add(jLabel3, 
		    new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, 
					   GridBagConstraints.NORTHWEST, 
					   GridBagConstraints.HORIZONTAL, 
					   new Insets(0, 5, 5, 5), 0, 0));
	jPanel2.add(panelTrace, 
		    new GridBagConstraints(1, 0, 1, 2, 1.0, 1.0, 
					   GridBagConstraints.NORTHWEST, 
					   GridBagConstraints.BOTH, 
					   new Insets(0, 0, 0, 0), 0, 0));
	jPanel2.add(jScrollPane1, 
		    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
					   GridBagConstraints.CENTER, 
					   GridBagConstraints.BOTH, 
					   new Insets(5, 5, 5, 5), 0, 0));
	jScrollPane1.getViewport().add(processTree, null);
	this.getContentPane().add(jPanel3, 
				  new GridBagConstraints(0, 3, 2, 1, 1.0, 
				  0.0, GridBagConstraints.SOUTH, 
				  GridBagConstraints.HORIZONTAL, 
				  new Insets(0, 0, 0, 0), 0, 0));
	jPanel3.add(jPanel4, 
		    new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, 
					   GridBagConstraints.NORTHWEST, 
					   GridBagConstraints.HORIZONTAL, 
					   new Insets(0, 0, 0, 0), 0, 0));
	jPanel4.add(jPanel5, 
		    new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, 
					   GridBagConstraints.NORTHWEST, 
					   GridBagConstraints.HORIZONTAL, 
					   new Insets(5, 5, 5, 5), 0, 0));
	jPanel5.add(buttonStart, null);
	jPanel5.add(buttonTrace, null);
	jPanel5.add(buttonStep, null);
	jPanel3.add(buttonClose, 
		    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, 
					   GridBagConstraints.EAST, 
					   GridBagConstraints.NONE, 
					   new Insets(5, 5, 5, 5), 0, 0));
	processTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	processTree.setShowsRootHandles(true);
    } 

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
	traceList.addElement(c);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public JTree getProcessTree() {
	return processTree;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public DefaultTreeModel getProcessTreeModel() {
	return processTreeModel;
    } 

    /**
     * Method declaration
     *
     *
     * @param freezable
     *
     * @see
     */
    public void announceStep(Freezable freezable) {
	if (lastUserAction == USER_ACTION_STEP) {
	    freeze(freezable);
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param freezable
     *
     * @see
     */
    public void announceTrace(Freezable freezable) {
	if (lastUserAction == USER_ACTION_TRACE) {
	    freeze(freezable);
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param freezable
     *
     * @see
     */
    void freeze(Freezable freezable) {
	frozen = freezable;

	freezable.freeze();
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    void thaw() {
	if (frozen != null) {
	    frozen.thaw();

	    frozen = null;
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    void buttonStartPressed(ActionEvent e) {
	thaw();

	lastUserAction = USER_ACTION_START;
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    void buttonTracePressed(ActionEvent e) {
	thaw();

	lastUserAction = USER_ACTION_TRACE;
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    void buttonStepPressed(ActionEvent e) {
	thaw();

	lastUserAction = USER_ACTION_STEP;
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    void buttonClosePressed(ActionEvent e) {
	dispose();
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

