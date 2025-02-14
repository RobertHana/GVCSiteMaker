/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion.traceMonitor;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class PanelAlphabet extends JPanel {
    JLabel	  labelAlphabet = new JLabel();
    JList	  communicationList = new JList();
    JScrollPane   communicationScrollPane = new JScrollPane();
    GridBagLayout gridBagLayout1 = new GridBagLayout();

    /*
     * public PanelAlphabet() {
     * try  {
     * jbInit();
     * }
     * catch(Exception e) {
     * e.printStackTrace();
     * }
     * }
     */

    /**
     * Constructor declaration
     *
     *
     * @param alphabetType
     * @param listModel
     *
     * @see
     */
    public PanelAlphabet(String alphabetType, ListModel listModel) {
	try {
	    jbInit();
	    labelAlphabet.setText(alphabetType);
	    communicationList.setModel(listModel);
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
	labelAlphabet.setText("Alphabet");
	this.setLayout(gridBagLayout1);
	this.add(labelAlphabet, 
		 new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, 
					GridBagConstraints.WEST, 
					GridBagConstraints.HORIZONTAL, 
					new Insets(5, 5, 5, 5), 0, 0));
	this.add(communicationScrollPane, 
		 new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, 
					GridBagConstraints.CENTER, 
					GridBagConstraints.BOTH, 
					new Insets(5, 5, 5, 5), 0, 0));
	communicationScrollPane.getViewport().add(communicationList, null);
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

