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
public class PanelCloseButton extends JPanel {
    FlowLayout flowLayout1 = new FlowLayout();
    JButton    buttonClose = new JButton();

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public PanelCloseButton() {
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
	buttonClose.setText("Close");
	flowLayout1.setAlignment(FlowLayout.RIGHT);
	this.setLayout(flowLayout1);
	this.add(buttonClose, null);
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

