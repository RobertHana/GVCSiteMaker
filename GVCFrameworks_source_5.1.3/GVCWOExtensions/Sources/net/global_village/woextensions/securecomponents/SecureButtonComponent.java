package net.global_village.woextensions.securecomponents;

import com.webobjects.appserver.*;

/**
 * A Form Button which can conditionally display itself as a button or not display at all.  This allows the UI to dynamically change based on object state or user permissions.  It must be placed inside a WOForm.  When isVisible is bound to true it displays a WOSubmitButton displaying the valueForBinding of buttonText
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */
public class SecureButtonComponent extends SecureActionComponent
{

    /**
     * Designated constructor.
     */
    public SecureButtonComponent(WOContext aContext)
    {
        super(aContext);
    }


}
