/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion.traceMonitor;

import javax.swing.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class PanelLinkProperties extends JPanel {
    JLabel  label = new JLabel();
    JButton buttonProperties = new JButton();

    /**
     * Constructor declaration
     *
     *
     * @param labelText
     *
     * @see
     */
    public PanelLinkProperties(String labelText) {
	try {
	    jbInit();
	    label.setText(labelText);
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
	buttonProperties.setText("Properties");
	this.add(label, null);
	this.add(buttonProperties, null);
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

