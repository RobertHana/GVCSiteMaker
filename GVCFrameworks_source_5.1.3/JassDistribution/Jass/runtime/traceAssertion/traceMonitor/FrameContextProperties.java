/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion.traceMonitor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class FrameContextProperties extends JFrame {
    JLabel	     labelContainsProcesses = new JLabel();
    JPanel	     jPanel1 = new JPanel();
    JLabel	     labelFlat = new JLabel();
    JComboBox	     boxFlat = new JComboBox();
    JLabel	     labelNested = new JLabel();
    JComboBox	     boxNested = new JComboBox();
    PanelAlphabet    panelExpandedAlphabet = 
	new PanelAlphabet("Expanded Alphabet", new DefaultListModel());
    PanelAlphabet    panelSyncAlphabet = 
	new PanelAlphabet("Synchronisation Alphabet", new DefaultListModel());
    PanelAlphabet    panelTrace = new PanelAlphabet("Trace", 
	    new DefaultListModel());
    FlowLayout       flowLayout1 = new FlowLayout();
    GridBagLayout    gridBagLayout1 = new GridBagLayout();
    PanelCloseButton panelClose = new PanelCloseButton();

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public FrameContextProperties() {
	try {
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
	labelContainsProcesses.setText("ContextX contains i Processes");
	this.getContentPane().setLayout(gridBagLayout1);
	labelFlat.setText("i flat:");
	labelNested.setText("i nested:");
	jPanel1.setLayout(flowLayout1);
	boxNested.addActionListener(new java.awt.event.ActionListener() {

	    /**
	     * Method declaration
	     *
	     *
	     * @param e
	     *
	     * @see
	     */
	    public void actionPerformed(ActionEvent e) {
		boxNested_actionPerformed(e);
	    } 

	});
	this.getContentPane().add(labelContainsProcesses, 
				  new GridBagConstraints(0, 0, 1, 1, 1.0, 
				  0.0, GridBagConstraints.NORTHWEST, 
				  GridBagConstraints.HORIZONTAL, 
				  new Insets(5, 5, 0, 5), 223, 0));
	this.getContentPane().add(jPanel1, 
				  new GridBagConstraints(0, 1, 1, 1, 1.0, 
				  0.0, GridBagConstraints.CENTER, 
				  GridBagConstraints.BOTH, 
				  new Insets(0, 5, 0, 5), 40, 0));
	jPanel1.add(labelFlat, null);
	jPanel1.add(boxFlat, null);
	jPanel1.add(labelNested, null);
	jPanel1.add(boxNested, null);
	this.getContentPane().add(panelExpandedAlphabet, 
				  new GridBagConstraints(0, 2, 1, 1, 1.0, 
				  1.0, GridBagConstraints.CENTER, 
				  GridBagConstraints.BOTH, 
				  new Insets(0, 5, 0, 5), 0, 0));
	this.getContentPane().add(panelSyncAlphabet, 
				  new GridBagConstraints(0, 3, 1, 1, 1.0, 
				  1.0, GridBagConstraints.CENTER, 
				  GridBagConstraints.BOTH, 
				  new Insets(0, 5, 0, 5), 0, 0));
	this.getContentPane().add(panelTrace, 
				  new GridBagConstraints(0, 4, 1, 1, 1.0, 
				  1.0, GridBagConstraints.CENTER, 
				  GridBagConstraints.BOTH, 
				  new Insets(0, 5, 0, 5), 0, 0));
	this.getContentPane().add(panelClose, 
				  new GridBagConstraints(0, 5, 1, 1, 1.0, 
				  0.0, GridBagConstraints.CENTER, 
				  GridBagConstraints.BOTH, 
				  new Insets(0, 5, 0, 5), 0, 0));
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    void boxNested_actionPerformed(ActionEvent e) {}

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

