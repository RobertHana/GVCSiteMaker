/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion.traceMonitor;

import javax.swing.*;
import java.awt.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class FrameProcessProperties extends JFrame {
    Process		process;
    GridBagLayout       gridBagLayout1 = new GridBagLayout();
    PanelLinkProperties panelContext;
    PanelAlphabet       panelAlphabet = new PanelAlphabet("Alphabet", 
	    new DefaultListModel());
    PanelAlphabet       panelTrace = new PanelAlphabet("Trace", 
	    new DefaultListModel());
    PanelCloseButton    panelClose = new PanelCloseButton();

    /**
     * Constructor declaration
     *
     *
     * @param process
     *
     * @see
     */
    public FrameProcessProperties(Process process) {
	try {
	    this.process = process;
	    panelContext = new PanelLinkProperties("test");

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
	this.getContentPane().add(panelContext, 
				  new GridBagConstraints(0, 0, 1, 1, 1.0, 
				  0.0, GridBagConstraints.CENTER, 
				  GridBagConstraints.HORIZONTAL, 
				  new Insets(0, 0, 0, 0), 1, 1));
	this.getContentPane().add(panelAlphabet, 
				  new GridBagConstraints(0, 1, 1, 1, 1.0, 
				  1.0, GridBagConstraints.CENTER, 
				  GridBagConstraints.BOTH, 
				  new Insets(0, 0, 0, 0), 1, 1));
	this.getContentPane().add(panelTrace, 
				  new GridBagConstraints(0, 2, 1, 1, 1.0, 
				  1.0, GridBagConstraints.CENTER, 
				  GridBagConstraints.BOTH, 
				  new Insets(0, 0, 0, 0), 1, 1));
	this.getContentPane().add(panelClose, 
				  new GridBagConstraints(0, 3, 1, 1, 1.0, 
				  0.0, GridBagConstraints.CENTER, 
				  GridBagConstraints.HORIZONTAL, 
				  new Insets(0, 0, 0, 0), 1, 1));
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

