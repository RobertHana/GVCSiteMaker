package net.global_village.woextensions.securecomponents;

import com.webobjects.appserver.*;


/**
 * Super class for Secure Components that take in an Action binding
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
  */
public class SecureActionComponent extends SecureComponent
{


    /**
     * Designated constructor.
     */
    public SecureActionComponent(WOContext aContext)
    {
        super(aContext);
    }

   

    /**
     * Accessor for displayText binding.  The text displayed for the link
     *
     * @return value for binding displayText
     */
    public String displayText()
    {
        return (String) valueForBinding("displayText");
    }



    /**
     * Accessor for action binding.  The action binding provides a method which is called when the button is pressed.
     *
     * @return the method to call when the button is pressed
     */
    public com.webobjects.appserver.WOElement boundAction()
    {
        return (com.webobjects.appserver.WOElement) valueForBinding("action");
    }



    /**
     * False mutator to satisfy WebObjects binding synchronization.  It is never called.
     *
     * @param newAction should never passed in because this method should never be called.
     */
    public void setBoundAction(com.webobjects.appserver.WOElement newAction)
    {
        throw new RuntimeException("setBoundAction should never be called");
    }



}
