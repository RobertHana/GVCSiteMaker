// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;

/** This takes care of most of the annoying busy work involved in
 * creating one of those dreaded confirm/deny components.
 * <p>
 * 
 * The nice thing about this component (if I may say so myself) is
 * that if you don't need to dress up the GUI, you can conjure one of
 * these up in no time:
 *
 * <pre> 
 * WOSMConfirmAction confirmPage = (WOSMConfirmAction)pageWithName("WOSMConfirmAction");
 * confirmPage.setCallingComponent(this); // Don't forget this!
 * return confirmPage; 
 * </pre>
 *
 * If, on the other hand, if you need to doctor up the GUI, you can
 * subclass, leave the class file empty (aside from the class
 * declaration, of course), and whip up a fancy .wod and .html file.
 * 
 * @author   B.W. Fitzpatrick &lt;fitz@apple.com&gt;
 * @version $Revision: 1.1 $
*/
public class WOSMConfirmAction extends WOComponent {

    protected WOSMConfirmable callingComponent;
    protected String confirmString;
    protected String denyString;
    protected String message;
    protected String actionString;

    public WOSMConfirmAction(WOContext aContext)
    {
        super(aContext);
        // Set up some reasonable defaults.
        actionString = "Action";
        confirmString = "Yes";
        denyString    = "No";
        message       = "Are you sure you want to do this?";
    }

    /** Calls confirmAction on the callingComponent with a value of
     * <tt>true</tt> . */
    public WOComponent doCallerActionIfConfirmed() {
        return callingComponent.confirmAction(true);
    }

    /** Calls confirmAction on the callingComponent with a value of
     * <tt>false</tt> . */
    public WOComponent doCallerActionIfDenied() {
        return callingComponent.confirmAction(false);
    }

    /* Generic setters and getters ***************************************/
    /** Pass in <tt>this</tt> from the component that you're using to
     * call WOSMConfirmAction. This is mandatory. */
    public void setCallingComponent(WOSMConfirmable value) {
        callingComponent = value;
    }
    /** Returns the calling component (if you set it.)*/
    public WOSMConfirmable callingComponent() {
        return callingComponent;
    }

    /** Sets the String that appears in the confirm button. The default is "Yes" */
    public void setConfirmString(String value) {
        confirmString = value;
    }
    /** Returns the confirmString. */
    public String confirmString() {
        return confirmString;
    }

    /** Sets the String that appears in the deny button. The default is "No" */
    public void setDenyString(String value) {
        denyString = value;
    }
    /** Returns the denyString. */
    public String denyString() {
        return denyString;
    }

    /** Sets the confirmString that will appear in the component. The
     * default is "Are you sure you want to do this?" */
    public void setMessage (String value) {
        message = value;
    }
    /** Returns the message. */
    public String message() {
        return message;
    }

    /** Returns the actionString. */
    public String actionString() {
        return actionString;
    }
    /** Sets the actionString that will appear in the title after "Confirm". The
     * default is "Action" */
    public void setActionString(String newActionString) {
        actionString = newActionString;
    }
    
}
