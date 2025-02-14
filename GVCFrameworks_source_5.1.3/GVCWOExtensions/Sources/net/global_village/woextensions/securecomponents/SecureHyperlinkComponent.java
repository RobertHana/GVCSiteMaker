package net.global_village.woextensions.securecomponents;

import com.webobjects.appserver.*;

/**
 * A HyperLink which can conditionally display itself as a HyperLink or nothing.  This allows the UI to dynamically change based on object state or user permissions.   When isVisible is bound to true it displays a WOHyperlink displaying the valueForBinding of linkText.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */
public class SecureHyperlinkComponent extends SecureActionComponent
{

    /**
     * Designated constructor.
     */
    public SecureHyperlinkComponent(WOContext aContext)
    {
        super(aContext);
    }


}
